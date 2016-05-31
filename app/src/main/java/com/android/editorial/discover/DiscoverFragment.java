package com.android.editorial.discover;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;

import com.android.editorial.R;
import com.android.editorial.Search;
import com.android.editorial.adapter.ImageAdapter;
import com.android.editorial.data.Editorial;
import com.android.editorial.data.Interaction;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class DiscoverFragment extends Fragment {

	private ImageButton btnSearch;
	private GridView imageGrid;
	private ImageAdapter imageAdapter;
	private List<Editorial> feedItems;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.com_editorial_ui_discover,
				container, false);

		btnSearch = (ImageButton) v.findViewById(R.id.search_button);

		imageGrid = (GridView) v.findViewById(R.id.grid);

		feedItems = new ArrayList<Editorial>();
		imageAdapter = new ImageAdapter(getActivity(), feedItems);
		imageGrid.setAdapter(imageAdapter);

		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent searchIntent = new Intent(getActivity(), Search.class);
				startActivity(searchIntent);
			}
		});
		new GetEditorials().execute();

		return v;
	}

	class GetEditorials extends AsyncTask<Void, Void, List<Editorial>> {

		@Override
		protected List<Editorial> doInBackground(Void... params) {
			// TODO Auto-generated method stub

			ParseQuery<Interaction> userfriendsQuery = ParseQuery
					.getQuery(Interaction.class);
			userfriendsQuery.whereEqualTo("type", "follow");
			userfriendsQuery.whereEqualTo("fromUser",
					ParseUser.getCurrentUser());
			userfriendsQuery.whereNotEqualTo("toUser",
					ParseUser.getCurrentUser());

			ParseQuery<Interaction> userlikesPostsQuery = ParseQuery
					.getQuery(Interaction.class);
			userlikesPostsQuery.whereEqualTo("type", "follow");
			userlikesPostsQuery.whereMatchesKeyInQuery("fromUser", "toUser",
					userfriendsQuery);
			userlikesPostsQuery.whereNotEqualTo("fromUser",
					ParseUser.getCurrentUser());

			ParseQuery<Editorial> likedPhotos = ParseQuery
					.getQuery(Editorial.class);
			likedPhotos.whereMatchesKeyInQuery("created_by", "toUser",
					userlikesPostsQuery);
			likedPhotos.whereNotEqualTo("created_by",
					ParseUser.getCurrentUser());
			likedPhotos.addDescendingOrder("commentCount");
			likedPhotos.addDescendingOrder("createdAt");

			try {
				return likedPhotos.find();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(List<Editorial> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				feedItems.clear();
				feedItems.addAll(result);
				imageAdapter.notifyDataSetChanged();
			}
		}

	}
}
