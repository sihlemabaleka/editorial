package com.android.editorial.profile;

import java.util.List;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridView;

import com.android.editorial.FullScreenFragment;
import com.android.editorial.R;
import com.android.editorial.adapter.DoubleColumnGridAdapter;
import com.android.editorial.data.Editorial;
import com.android.editorial.helpers.AppConstant;
import com.android.editorial.helpers.OfflineMethods;
import com.android.editorial.helpers.Utils;
import com.parse.ParseUser;

public class GridEditorialView extends Fragment {

	private OfflineMethods methods;

	private DoubleColumnGridAdapter adapter;
	private GridView gridView;
	private int columnWidth;

	private ProgressDialog pDialog;

	private Utils utils;

	public static EditorialView newInstance(String objectId) {
		EditorialView fragment = new EditorialView();
		Bundle args = new Bundle();
		args.putString("objectId", objectId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.com_editorial_ui_gridview,
				container, false);

		methods = new OfflineMethods(getActivity());

		gridView = (GridView) v.findViewById(R.id.grid_view);

		utils = new Utils(getActivity());

		// Initilizing Grid View
		InitilizeGridLayout();

		new GetEditorials().execute();

		return v;
	}

	private void InitilizeGridLayout() {

		columnWidth = (int) (utils.getScreenWidth() / 2);

		gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
		gridView.setColumnWidth(columnWidth);
		gridView.setStretchMode(GridView.NO_STRETCH);
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
			List<Editorial> editorials = methods.getEditorials(ParseUser
					.getCurrentUser());
			Editorial.pinAllInBackground(editorials);
			return editorials;
		}

		@Override
		protected void onPostExecute(List<Editorial> editorials) {
			// TODO Auto-generated method stub
			super.onPostExecute(editorials);
			if (editorials != null) {
				if (editorials.size() > 0) {
					setAdapters(editorials);
				}
			} else {
			}
			pDialog.hide();

		}

	}

	public void setAdapters(List<Editorial> editorials) {
		// TODO Auto-generated method stub
		adapter = new DoubleColumnGridAdapter(getActivity(), editorials,
				columnWidth, this);
		gridView.setAdapter(adapter);
	}

	public void clickedEditorial(String objectId) {
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.container,
						FullScreenFragment.newInstance(objectId)).commit();
	}

}
