package com.android.editorial.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.editorial.R;
import com.android.editorial.create.OnImageUpload;
import com.android.editorial.data.Editorial;
import com.android.editorial.helpers.OfflineMethods;
import com.android.editorial.views.CropImageView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

public class UploadImageAdapter extends PagerAdapter {

	private Activity _activity;
	private ArrayList<String> editorials;
	private LayoutInflater inflater;
	private Editorial editorial;

	OnImageUpload uploadDone;

	// constructor
	public UploadImageAdapter(Activity activity, ArrayList<String> editorials,
			Editorial editorial, OnImageUpload uploadDone) {
		this._activity = activity;
		this.editorials = editorials;
		this.editorial = editorial;
		this.uploadDone = uploadDone;

	}

	@Override
	public int getCount() {
		return this.editorials.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		final CropImageView imgDisplay;
		Button btnClose;
		final Button btnUpload;
		ImageButton btnRotate;
		final ToggleButton btnIsProfile;
		final ProgressBar pDialog;
		final TextView txtPercentage, txtImageCount;

		final OfflineMethods helper;

		inflater = (LayoutInflater) _activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewLayout = inflater.inflate(R.layout.com_editorial_image_select,
				container, false);

		imgDisplay = (CropImageView) viewLayout.findViewById(R.id.imgDisplay);
		btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
		btnUpload = (Button) viewLayout.findViewById(R.id.btn_upload);
		btnRotate = (ImageButton) viewLayout.findViewById(R.id.rotate);
		pDialog = (ProgressBar) viewLayout.findViewById(R.id.progressBar1);
		btnIsProfile = (ToggleButton) viewLayout
				.findViewById(R.id.is_profile_picture);
		txtPercentage = (TextView) viewLayout.findViewById(R.id.txtPercentage);
		txtImageCount = (TextView) viewLayout
				.findViewById(R.id.upload_image_count);

		pDialog.setProgress(0);
		pDialog.setMax(100);

		txtImageCount.setText((position + 1) + " of " + editorials.size());

		helper = new OfflineMethods(_activity);

		Bitmap image;
		try {
			image = BitmapFactory.decodeFile(editorials.get(position));
		} catch (Exception e) {
			image = decodeFile(editorials.get(position),
					helper.getScreenWidth().x, helper.getScreenWidth().y);
		}
		imgDisplay.setImageBitmap(image);
		imgDisplay.setFixedAspectRatio(false);
		btnRotate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imgDisplay.rotateImage(90);
			}
		});

		btnUpload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btnUpload.setEnabled(false);
				btnUpload.setVisibility(View.GONE);
				new saveInBackground(pDialog, txtPercentage, imgDisplay,
						helper, btnIsProfile).execute();
			}
		});

		// close button click event
		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				_activity.finish();
			}
		});

		((ViewPager) container).addView(viewLayout);

		return viewLayout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((RelativeLayout) object);

	}

	public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
		try {

			File f = new File(filePath);

			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			final int REQUIRED_WIDTH = WIDTH;
			final int REQUIRED_HIGHT = HIGHT;
			int scale = 4;
			while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
					&& o.outHeight / scale / 2 >= REQUIRED_HIGHT)
				scale *= 4;

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	class saveInBackground extends AsyncTask<Void, Void, byte[]> {

		ProgressBar pDialog;
		TextView txtPercentage;
		CropImageView imgDisplay;
		OfflineMethods helper;
		ToggleButton btnIsProfile;

		public saveInBackground(ProgressBar pDialog, TextView txtPercentage,
				CropImageView imgDisplay, OfflineMethods helper,
				ToggleButton btnIsProfile) {
			super();
			this.pDialog = pDialog;
			this.txtPercentage = txtPercentage;
			this.imgDisplay = imgDisplay;
			this.helper = helper;
			this.btnIsProfile = btnIsProfile;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			txtPercentage.setVisibility(View.VISIBLE);
			pDialog.setVisibility(View.VISIBLE);
			btnIsProfile.setVisibility(View.INVISIBLE);
			imgDisplay.setVisibility(View.INVISIBLE);

		}

		@Override
		protected byte[] doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Bitmap bMap = imgDisplay.getCroppedImage();
			return helper.getScaledPhoto(bMap, bMap.getHeight(),
					bMap.getWidth());
		}

		@Override
		protected void onPostExecute(byte[] data) {
			// TODO Auto-generated method stub
			super.onPostExecute(data);
			if (data != null) {
				final ParseFile file = new ParseFile(data);
				file.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						// TODO Auto-generated method stub
						if (e == null) {
							helper.setEditorialPicture(editorial.getCategory(),
									editorial, file, "");
							uploadDone.onImageUploaded(btnIsProfile.isChecked());
							if (btnIsProfile.isChecked()) {
								uploadDone
										.onCoverPhotoSelected(file, editorial);
							}
						} else {
							e.printStackTrace();
						}
					}
				}, new ProgressCallback() {

					@Override
					public void done(Integer value) {
						// TODO Auto-generated method stub
						txtPercentage.setText(value + "%");
						pDialog.setProgress(value);
					}
				});
			}
		}

	}

}