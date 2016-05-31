package com.android.editorial.dialogs;

import java.io.ByteArrayOutputStream;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.android.editorial.R;
import com.android.editorial.views.CropImageView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class CropWindowDialog extends DialogFragment {

	private CropImageView profilePictureCrop;
	private Bitmap croppedImage;
	private Button btnCrop;

	public static CropWindowDialog newInstance(String data) {
		// TODO Auto-generated method stub
		CropWindowDialog fragment = new CropWindowDialog();
		Bundle args = new Bundle();
		args.putString("data", data);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.com_editorial_ui_move_and_scale_layout);
		profilePictureCrop = (CropImageView) dialog
				.findViewById(R.id.cropImageView1);
		btnCrop = (Button) dialog.findViewById(R.id.done);

		String filePath = getArguments().getString("data");
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);

		profilePictureCrop.setImageBitmap(bitmap);
		profilePictureCrop.setAspectRatio(100, 100);
		profilePictureCrop.setFixedAspectRatio(true);

		btnCrop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new SaveProfilePhoto().execute();
			}
		});
		return dialog;
	}

	class SaveProfilePhoto extends AsyncTask<Void, Integer, byte[]> {

		@Override
		protected byte[] doInBackground(Void... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				croppedImage = profilePictureCrop.getCroppedImage();
				Bitmap bMap;
				if (!getArguments().getBoolean("isCover")) {
					bMap = Bitmap.createScaledBitmap(croppedImage, 120, 120,
							true);
				} else {
					bMap = Bitmap.createScaledBitmap(croppedImage, 640, 480,
							true);
				}
				bMap.compress(CompressFormat.PNG, 100, bos);
				byte[] data = bos.toByteArray();

				return data;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(byte[] data) {
			// dismiss the dialog once product deleted
			dismiss();
			if (data != null) {
				final ParseFile file = new ParseFile(data);
				file.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException arg0) {
						// TODO Auto-generated method stub
						ParseUser user = ParseUser.getCurrentUser();
						user.put("profile_picture", file);
						user.saveEventually();
					}
				});

			}
		}
	}

}
