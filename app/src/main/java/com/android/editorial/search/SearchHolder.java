package com.android.editorial.search;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.editorial.R;
import com.android.editorial.adapter.UserAdapter;
import com.android.editorial.helpers.OfflineMethods;
import com.parse.ParseUser;

public class SearchHolder extends Fragment {

	private ListView listView;
	private UserAdapter listAdapter;
	private List<ParseUser> feedItems;

	private OfflineMethods helpers;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.com_editorial_ui_fragment_main,
				container, false);

		helpers = new OfflineMethods(getActivity());

		listView = (ListView) v.findViewById(R.id.list);
		feedItems = new ArrayList<ParseUser>();

		listAdapter = new UserAdapter(getActivity(), feedItems);
		listView.setAdapter(listAdapter);

		new GetSearch().execute();

		return v;
	}

	class GetSearch extends AsyncTask<Void, Void, List<ParseUser>> {

		@Override
		protected List<ParseUser> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return helpers.getSearchedUsers();
		}

		@Override
		protected void onPostExecute(List<ParseUser> list) {
			// TODO Auto-generated method stub
			super.onPostExecute(list);
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
