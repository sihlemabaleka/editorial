package com.android.editorial.search;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.android.editorial.R;
import com.android.editorial.adapter.UserAdapter;
import com.android.editorial.helpers.AppConstant;
import com.android.editorial.helpers.OfflineMethods;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class SearchingFragment extends Fragment {

	private ListView listView;
	private UserAdapter listAdapter;
	private List<ParseUser> feedItems;

	private ProgressDialog pDialog;
	private OfflineMethods helpers;

	public static SearchingFragment newInstance(String string) {
		SearchingFragment fragment = new SearchingFragment();
		Bundle args = new Bundle();
		args.putString("search_query", string);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.com_editorial_ui_fragment_main,
				container, false);

		helpers = new OfflineMethods(getActivity());

		listView = (ListView) v.findViewById(R.id.list);

		feedItems = helpers.getSearchedUsers();

		if (feedItems == null)
			feedItems = new ArrayList<ParseUser>();

		listAdapter = new UserAdapter(getActivity(), feedItems);
		listView.setAdapter(listAdapter);

		new GetSearch().execute();

		return v;
	}

	class GetSearch extends AsyncTask<Void, Void, List<ParseUser>> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pDialog = new ProgressDialog(getActivity());
			pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			pDialog.setMessage("Just a moment");
			pDialog.show();
		}

		@Override
		protected List<ParseUser> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ParseQuery<ParseUser> user_query = ParseUser.getQuery();
			user_query.whereStartsWith("display_name", getArguments()
					.getString("search_query").trim());
			user_query.addAscendingOrder("likeCount");
			try {
				List<ParseUser> users = user_query.find();
				ParseUser.pinAll(AppConstant.SEARCHED_USERS, users);
				return users;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<ParseUser> list) {
			// TODO Auto-generated method stub
			super.onPostExecute(list);
			pDialog.dismiss();
			if (list != null) {
				feedItems.clear();
				feedItems.addAll(list);
				listAdapter.notifyDataSetChanged();
			} else {
				Toast.makeText(getActivity(), "User list is null",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

}
