package com.android.editorial;

import java.util.List;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.android.editorial.adapter.FullScreenImageAdapter;
import com.android.editorial.data.Piece;
import com.android.editorial.helpers.OfflineMethods;
import com.parse.ParseUser;

public class FullScreenFragment extends Fragment {

	OfflineMethods methods;
	ParseUser user;

	FullScreenImageAdapter adapter;
	ViewPager pager;

	ProgressDialog pDialog;

	public static FullScreenFragment newInstance(String objectId) {
		FullScreenFragment fragment = new FullScreenFragment();
		Bundle args = new Bundle();
		args.putString("objectId", objectId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.com_editorial_ui_view_pager,
				container, false);

		methods = new OfflineMethods(getActivity());

		pager = (ViewPager) v.findViewById(R.id.pager);

		new GetEditorials().execute();

		return v;
	}

	public class GetEditorials extends AsyncTask<Void, Void, List<Piece>> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pDialog = new ProgressDialog(getActivity());
			pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			pDialog.setMessage("Just a moment");
			pDialog.show();
		}

		@Override
		protected List<Piece> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return methods.getEditorialPictures(getArguments().getString(
					"objectId"));
		}

		@Override
		protected void onPostExecute(List<Piece> editorials) {
			// TODO Auto-generated method stub
			super.onPostExecute(editorials);
			if (editorials != null) {
				if (editorials.size() > 0) {
					adapter = new FullScreenImageAdapter(getActivity(),
							editorials);
					pager.setAdapter(adapter);
				}
			} else {
				System.out.println("No Editorials");
			}
			pDialog.hide();

		}

	}

}
