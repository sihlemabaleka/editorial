package com.android.editorial.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.android.editorial.FullScreenFragment;
import com.android.editorial.R;
import com.android.editorial.data.Editorial;
import com.android.editorial.helpers.OfflineMethods;
import com.android.editorial.profile.EditProfile;
import com.android.editorial.views.RoundImageView;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

public class EditorialPagerAdapter extends PagerAdapter {

	private Activity _activity;
	private List<Editorial> editorials;
	private LayoutInflater inflater;
	private OfflineMethods helper;

	// constructor
	public EditorialPagerAdapter(Activity activity, List<Editorial> editorials) {
		this._activity = activity;
		this.editorials = editorials;
	}

	@Override
	public int getCount() {
		return this.editorials.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((LinearLayout) object);
	}

	@Override
	public Object instantiateItem(final ViewGroup container, int position) {

		RoundImageView mProfilePicture;
		TextView mName, mTimestamp, mCaption;
		ParseImageView mEditorialPicture;
		ImageButton btnOptions;

		inflater = (LayoutInflater) _activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.com_editorial_ui_feed_item,
				container, false);

		mProfilePicture = (RoundImageView) v.findViewById(R.id.profilePic);
		mName = (TextView) v.findViewById(R.id.name);
		mTimestamp = (TextView) v.findViewById(R.id.timestamp);
		mCaption = (TextView) v.findViewById(R.id.txtStatusMsg);

		mEditorialPicture = (ParseImageView) v
				.findViewById(R.id.editorial_picture);
		btnOptions = (ImageButton) v.findViewById(R.id.options);

		helper = new OfflineMethods(_activity);

		ParseUser user = editorials.get(position).getCreatedBy();
		final Editorial editorial = editorials.get(position);

		Picasso.with(_activity)
				.load(user.getParseFile("profile_picture").getUrl())
				.into(mProfilePicture);
		mName.setText(user.getString("name"));
		mTimestamp.setText(getTimeStamp(editorial.getCreatedAt().getTime()));
		mCaption.setText(editorial.getEditorialName());
		Picasso.with(_activity)
				.load(editorial.getEditorialCoverPhoto().getUrl())
				.into(mEditorialPicture);

		mEditorialPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				_activity
						.getFragmentManager()
						.beginTransaction()
						.replace(
								R.id.container,
								FullScreenFragment.newInstance(editorial
										.getObjectId())).commit();
			}
		});

		if (editorial.getCreatedBy() == ParseUser.getCurrentUser()) {
			btnOptions.setVisibility(View.VISIBLE);
		}

		btnOptions.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPopUpMenu(v);
			}

			private void showPopUpMenu(View v) {
				// TODO Auto-generated method stub
				PopupMenu popup = new PopupMenu(_activity, v);
				MenuInflater inflater = popup.getMenuInflater();
				// if (ParseUser.getCurrentUser().equals(postObject.getUser()))
				inflater.inflate(R.menu.post_options, popup.getMenu());
				// else
				// inflater.inflate(R.menu.post_settings, popup.getMenu());
				popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						switch (item.getItemId()) {
						case R.id.edit_post:
							_activity.getFragmentManager().beginTransaction()
									.replace(R.id.container, new EditProfile())
									.addToBackStack("profile").commit();
							return true;
						case R.id.delete_editorial:
							helper.deleteEditoria(editorial,
									ParseUser.getCurrentUser());
							notifyDataSetChanged();
							return true;
						default:
							return true;
						}
					}
				});

				popup.show();
			}
		});

		((ViewPager) container).addView(v);

		return v;
	}

	private CharSequence getTimeStamp(long time) {
		// TODO Auto-generated method stub
		return DateUtils.getRelativeTimeSpanString(time,
				System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((LinearLayout) object);

	}
}