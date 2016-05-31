
package com.android.editorial.feed;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.editorial.R;
import com.android.editorial.adapter.BasicAdapter;
import com.android.editorial.adapter.EditorialAdapter;
import com.android.editorial.data.Editorial;
import com.android.editorial.data.Interaction;
import com.android.editorial.discover.DiscoverFriends;
import com.android.editorial.helpers.AppConstant;
import com.android.editorial.helpers.OfflineMethods;
import com.android.editorial.helpers.Utils;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class HomeFragment extends Fragment implements OnRefreshListener {

	private RecyclerView listView;
	private RecyclerView.Adapter adapter;
	List<Editorial> editorials = new ArrayList<Editorial>();
	private RecyclerView.LayoutManager mLayoutManager;

	OfflineMethods parseMethods;

	SwipeRefreshLayout swipeRefreshLayout;
	Utils utils;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(
				R.layout.com_editorial_ui_fragment_main, container, false);
		utils = new Utils(getActivity());

		swipeRefreshLayout = (SwipeRefreshLayout) rootView
				.findViewById(R.id.refresh_list_view);

		parseMethods = new OfflineMethods(getActivity());
		listView = (RecyclerView) rootView.findViewById(R.id.list);


		mLayoutManager = new LinearLayoutManager(getActivity());
		listView.setLayoutManager(mLayoutManager);

		if(savedInstanceState == null)
		new GetEditorials(swipeRefreshLayout, false).execute();

		swipeRefreshLayout.setOnRefreshListener(this);

		return rootView;

	}

	private void loadObjects() {
		// TODO Auto-generated method stub
		ParseQuery<Editorial> query = ParseQuery.getQuery(Editorial.class);
		query.whereEqualTo("created_by", ParseUser.getCurrentUser());

		ParseQuery<Interaction> friendsQuery = ParseQuery
				.getQuery(Interaction.class);
		friendsQuery.whereEqualTo("type", "follow");
		friendsQuery.whereEqualTo("fromUser", ParseUser.getCurrentUser());

		ParseQuery<Editorial> friendPosts = ParseQuery
				.getQuery(Editorial.class);
		friendPosts
				.whereMatchesKeyInQuery("created_by", "toUser", friendsQuery);

		List<ParseQuery<Editorial>> queries = new ArrayList<ParseQuery<Editorial>>();
		queries.add(query);
		queries.add(friendPosts);

		ParseQuery<Editorial> mainQuery = ParseQuery.or(queries);
		mainQuery.whereExists("cover_photo");
		mainQuery.addDescendingOrder("createdAt");
		mainQuery.findInBackground(new FindCallback<Editorial>() {

			@Override
			public void done(final List<Editorial> list, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					Editorial.unpinAllInBackground(
							AppConstant.MAIN_EDITORIAL_FEED,
							new DeleteCallback() {

								@Override
								public void done(ParseException e) {
									// TODO Auto-generated method stub
									if (e == null) {
										Editorial
												.pinAllInBackground(
														AppConstant.MAIN_EDITORIAL_FEED,
														list,
														new SaveCallback() {

															@Override
															public void done(
																	ParseException e) {
																// TODO
																// Auto-generated
																// method stub
																if (e == null) {
																	System.out
																			.println("Editorials Pinned");
																	new GetEditorials(
																			swipeRefreshLayout,
																			true)
																			.execute();
																}
															}
														});
										System.out
												.println("Getting Editorials");
									}
								}
							});
				}
			}
		});
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		try {
			loadObjects();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public class GetEditorials extends AsyncTask<Void, Void, List<Editorial>> {

		SwipeRefreshLayout refreshLayout;
		Boolean isRefreshing;

		public GetEditorials(SwipeRefreshLayout refreshLayout,
				Boolean isRefreshing) {
			super();
			this.refreshLayout = refreshLayout;
			this.isRefreshing = isRefreshing;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			swipeRefreshLayout.setRefreshing(true);
		}

		@Override
		protected List<Editorial> doInBackground(Void... params) {
			// TODO Auto-generated method stub

			ParseQuery<Editorial> query = new ParseQuery<Editorial>(
					Editorial.class);
			query.fromLocalDatastore();
			query.addDescendingOrder("createdAt");
			try {
				return query.find();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Editorial> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				if (result.size() > 0) {
					adapter = new EditorialAdapter(getActivity(), result);
					listView.setAdapter(adapter);
				} else {
					getFragmentManager().beginTransaction()
							.replace(R.id.container, new DiscoverFriends())
							.addToBackStack("home").commit();
				}
			} else {
				getFragmentManager().beginTransaction()
						.replace(R.id.container, new DiscoverFriends())
						.addToBackStack("home").commit();
			}
			swipeRefreshLayout.setRefreshing(false);
		}
	}
}