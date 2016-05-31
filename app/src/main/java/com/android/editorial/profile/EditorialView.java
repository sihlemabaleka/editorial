package com.android.editorial.profile;

import java.util.List;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.android.editorial.FullScreenActivity;
import com.android.editorial.R;
import com.android.editorial.adapter.ImageAdapter;
import com.android.editorial.data.Editorial;
import com.android.editorial.helpers.OfflineMethods;
import com.parse.ParseUser;

public class EditorialView extends Fragment {

	OfflineMethods methods;
	ParseUser user;

	private GridView imageGrid;
	private ImageAdapter adapter;

	ProgressDialog pDialog;

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

		imageGrid = (GridView) v.findViewById(R.id.grid_view);
		imageGrid.setNumColumns(1);

		new GetEditorials().execute();

		return v;
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
					adapter = new ImageAdapter(getActivity(), editorials);
					imageGrid.setAdapter(adapter);
					imageGrid.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							Intent i = new Intent(getActivity(),
									FullScreenActivity.class);
							i.putExtra("objectId", editorials.get(position)
									.getObjectId());
							getActivity().startActivity(i);
						}
					});
				}
			} else {
				System.out.println("No Editorials");
			}
			pDialog.hide();

		}

	}

}
