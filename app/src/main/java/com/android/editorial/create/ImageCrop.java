package com.android.editorial.create;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.android.editorial.R;
import com.android.editorial.adapter.UploadImageAdapter;
import com.android.editorial.data.Editorial;
import com.android.editorial.helpers.OfflineMethods;
import com.android.editorial.views.CustomViewPager;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class ImageCrop extends Fragment implements OnImageUpload {

	private OfflineMethods methods;

	private UploadImageAdapter adapter;
	private CustomViewPager pager;

	private ProgressDialog pDialog;
	private String mCategory, mName, mUrl;
	private ArrayList<String> filePaths;
	int numberOfUploadedImages = 0;

	Editorial _editorial;

	OnImageUpload onUploadDone;

	Bundle i;
	GetEditorials getEditorial;

	public static ImageCrop newInstance(String mCategory, String mName,
			String mUrl, ArrayList<String> filePaths) {
		ImageCrop fragment = new ImageCrop();
		Bundle args = new Bundle();
		args.putString("name", mName);
		args.putString("category", mCategory);
		args.putString("url", mUrl);
		args.putStringArrayList("filePaths", filePaths);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.view_pager, container, false);

		onUploadDone = (OnImageUpload) this;

		methods = new OfflineMethods(getActivity());

		pager = (CustomViewPager) v.findViewById(R.id.pager);

		i = getArguments();

		filePaths = i.getStringArrayList("filePaths");
		mCategory = i.getString("category");
		mName = i.getString("name");
		if (!TextUtils.isEmpty(i.getString("url"))) {
			mUrl = i.getString("url");
		} else {
			mUrl = ParseUser.getCurrentUser().getString("website");
		}

		getEditorial = new GetEditorials();
		getEditorial.execute();

		return v;
	}

	public class GetEditorials extends AsyncTask<Void, Void, Editorial> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pDialog = new ProgressDialog(getActivity());
			pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			pDialog.setMessage("Just a moment");
			pDialog.show();
			adapter = null;
		}

		@Override
		protected Editorial doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Editorial editorial = methods.createEditorial(mCategory, mName,
					mUrl);
			return editorial;
		}

		@Override
		protected void onPostExecute(Editorial editorial) {
			// TODO Auto-generated method stub
			super.onPostExecute(editorial);
			if (editorial != null) {
				_editorial = editorial;
				adapter = new UploadImageAdapter(getActivity(), filePaths,
						editorial, onUploadDone);
				pager.setAdapter(adapter);
				pager.setOffscreenPageLimit(2);
			}
			pDialog.dismiss();
		}
	}

	@Override
	public void onImageUploaded(Boolean value) {
		// TODO Auto-generated method stub
		int position = pager.getCurrentItem();
		numberOfUploadedImages = numberOfUploadedImages + 1;
		if (position < adapter.getCount()) {
			pager.setCurrentItem(position + 1);
		} else {
			getActivity().finish();
		}
	}

	@Override
	public void onCoverPhotoSelected(ParseFile file, Editorial editorial) {
		// TODO Auto-generated method stub
		methods.setEditorialCoverPhoto(file, editorial);
		editorial.setPublishedStatus(true);
		editorial.saveInBackground();
	}

}
