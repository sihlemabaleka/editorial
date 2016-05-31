package com.android.editorial.profile;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.android.editorial.FullScreenActivity;
import com.android.editorial.R;
import com.android.editorial.adapter.ImageAdapter;
import com.android.editorial.adapter.MyEditorialAdapter;
import com.android.editorial.data.Editorial;
import com.android.editorial.helpers.OfflineMethods;
import com.android.editorial.views.RoundImageView;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserProfile extends Fragment implements OnClickListener {

	private Button btnEditorials;
	private RoundImageView mProfilePicture;
	private TextView mName, mBio;
	private Picasso picasso;
	private Button btnFriendCount;

	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;

	private OfflineMethods methods;
	private ProgressDialog pDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.profile_main_container,
				container, false);

		//initCollapsingToolbar(v);

		methods = new OfflineMethods(getActivity());

		recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

		RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(10), true));
		recyclerView.setItemAnimator(new DefaultItemAnimator());

		picasso = Picasso.with(getActivity());

		mProfilePicture = (RoundImageView) v.findViewById(R.id.profilePic);
		mName = (TextView) v.findViewById(R.id.name);
		mBio = (TextView) v.findViewById(R.id.bio);

		btnFriendCount = (Button) v.findViewById(R.id.friend_count);
		btnEditorials = (Button) v.findViewById(R.id.post_count);

		//new GetEditorials().execute();
		getProfileValues();

		return v;
	}

	private void getProfileValues() {
		// TODO Auto-generated method stub
		ParseUser user = ParseUser.getCurrentUser();
		try {
			mName.setText(user.getString("name"));
			if (!TextUtils.isEmpty(user.getString("bio"))) {
				mBio.setText(user.getString("bio"));
				mBio.setVisibility(View.VISIBLE);
			}

			if (!TextUtils.isEmpty(user.getString("display_name"))) {
				mName.setText(user.getString("display_name"));
				mName.setVisibility(View.VISIBLE);
			}

			btnEditorials.setText(user.getInt("editorial_count") + "\nEditorials");
			btnFriendCount.setText(user.getInt("friendCount") + "\nFriends");

			picasso.load(ParseUser.getCurrentUser().getParseFile("profile_picture").getUrl()).into(mProfilePicture);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.options:
			showPopUpMenu(v);
			break;
		}
	}

	private void showPopUpMenu(View v) {
		// TODO Auto-generated method stub
		PopupMenu popup = new PopupMenu(getActivity(), v);
		MenuInflater inflater = popup.getMenuInflater();
		// if (ParseUser.getCurrentUser().equals(postObject.getUser()))
		inflater.inflate(R.menu.profile_options, popup.getMenu());
		// else
		// inflater.inflate(R.menu.post_settings, popup.getMenu());
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				switch (item.getItemId()) {
				case R.id.edit_profile:
					getFragmentManager().beginTransaction()
							.replace(R.id.container, new EditProfile())
							.addToBackStack("profile").commit();
					return true;
				default:
					return true;
				}
			}
		});
		popup.show();
	}

	/**
	 * Initializing collapsing toolbar
	 * Will show and hide the toolbar title on scroll
	 */
	private void initCollapsingToolbar(View view) {
		final CollapsingToolbarLayout collapsingToolbar =
				(CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
		collapsingToolbar.setTitle("");
		AppBarLayout appBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
		appBarLayout.setExpanded(true);

		// hiding & showing the title when toolbar expanded & collapsed
		appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
			boolean isShow = false;
			int scrollRange = -1;

			@Override
			public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
				if (scrollRange == -1) {
					scrollRange = appBarLayout.getTotalScrollRange();
				}
				if (scrollRange + verticalOffset == 0) {
					collapsingToolbar.setTitle(getString(R.string.app_name));
					isShow = true;
				} else if (isShow) {
					collapsingToolbar.setTitle(" ");
					isShow = false;
				}
			}
		});
	}

	/**
	 * RecyclerView item decoration - give equal margin around grid item
	 */
	public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

		private int spanCount;
		private int spacing;
		private boolean includeEdge;

		public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
			this.spanCount = spanCount;
			this.spacing = spacing;
			this.includeEdge = includeEdge;
		}

		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
			int position = parent.getChildAdapterPosition(view); // item position
			int column = position % spanCount; // item column

			if (includeEdge) {
				outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
				outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

				if (position < spanCount) { // top edge
					outRect.top = spacing;
				}
				outRect.bottom = spacing; // item bottom
			} else {
				outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
				outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
				if (position >= spanCount) {
					outRect.top = spacing; // item top
				}
			}
		}
	}

	/**
	 * Converting dp to pixel
	 */
	private int dpToPx(int dp) {
		Resources r = getResources();
		return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
	}


	public class GetEditorials extends AsyncTask<Void, Void, List<Editorial>> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pDialog = new ProgressDialog(getActivity());
			pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			pDialog.setMessage("Just a moment");
			pDialog.show();
		}

		@Override
		protected List<Editorial> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return methods.getMyEditorials(ParseUser.getCurrentUser());
		}

		@Override
		protected void onPostExecute(final List<Editorial> editorials) {
			// TODO Auto-generated method stub
			super.onPostExecute(editorials);
			if (editorials != null) {
				if (editorials.size() > 0) {
					adapter = new MyEditorialAdapter(getActivity(), editorials);
					recyclerView.setAdapter(adapter);
				}
			} else {
				System.out.println("No Editorials");
			}
			pDialog.hide();

		}

	}
}
