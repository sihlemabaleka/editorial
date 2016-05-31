package com.android.editorial;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.android.editorial.adapter.FullScreenImageAdapter;
import com.android.editorial.data.Piece;
import com.android.editorial.helpers.OfflineMethods;
import com.parse.ParseUser;

public class FullScreenActivity extends Activity {

	OfflineMethods methods;
	ParseUser user;
	Button btnClose;

	FullScreenImageAdapter adapter;
	ViewPager pager;

	ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.com_editorial_ui_view_pager);

		methods = new OfflineMethods(this);

		pager = (ViewPager) findViewById(R.id.pager);
		btnClose = (Button) findViewById(R.id.close);
		btnClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		new GetEditorials().execute();

	}

	public class GetEditorials extends AsyncTask<Void, Void, List<Piece>> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pDialog = new ProgressDialog(FullScreenActivity.this);
			pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			pDialog.setMessage("Just a moment");
			pDialog.show();
		}

		@Override
		protected List<Piece> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Intent i = getIntent();
			i.getExtras();
			String objectId = i.getStringExtra("objectId");
			return methods.getEditorialPictures(objectId);
		}

		@Override
		protected void onPostExecute(List<Piece> editorials) {
			// TODO Auto-generated method stub
			super.onPostExecute(editorials);
			if (editorials != null) {
				if (editorials.size() > 0) {
					adapter = new FullScreenImageAdapter(
							FullScreenActivity.this, editorials);
					pager.setAdapter(adapter);
					pager.setOffscreenPageLimit(2);
				}
			} else {
				System.out.println("No Editorials");
			}
			pDialog.dismiss();
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (pDialog != null)
			pDialog.dismiss();
	}

}
