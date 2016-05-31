package com.android.editorial.profile;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.editorial.R;
import com.android.editorial.dialogs.CropWindowDialog;
import com.android.editorial.helpers.GPSTracker;
import com.android.editorial.views.RoundImageView;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

public class EditProfile extends Fragment implements OnClickListener {

	private EditText mName, mDisplayName, mBio, mUrl;
	private TextView btnDone, btnCancel;
	private TextView textCount;

	private RoundImageView mProfilePicture;

	private ParseUser user;
	private GPSTracker gps;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.com_editorial_ui_edit_profile,
				container, false);

		user = ParseUser.getCurrentUser();
		gps = new GPSTracker(getActivity());

		mProfilePicture = (RoundImageView) v.findViewById(R.id.profilePic);

		mName = (EditText) v.findViewById(R.id.name);
		mDisplayName = (EditText) v.findViewById(R.id.username);
		mBio = (EditText) v.findViewById(R.id.bio);
		mUrl = (EditText) v.findViewById(R.id.website);
		textCount = (TextView) v.findViewById(R.id.biotextcount);

		mName.addTextChangedListener(mTextWatcher);
		mDisplayName.addTextChangedListener(mTextWatcher);
		mBio.addTextChangedListener(mTextWatcher);
		mUrl.addTextChangedListener(mTextWatcher);

		btnDone = (TextView) v.findViewById(R.id.done);
		btnCancel = (TextView) v.findViewById(R.id.cancel);

		btnDone.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		mProfilePicture.setOnClickListener(this);

		checkFieldsForEmptyValues();

		return v;
	}

	private TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			int charCount = 0;
			try {
				charCount = user.getString("bio").length();
			} catch (Exception e) {
				e.printStackTrace();
			}
			textCount.setText(charCount + count + "/160");

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			checkFieldsForEmptyValues();
		}
	};

	protected void checkFieldsForEmptyValues() {
		// TODO Auto-generated method stub
		String text1 = mName.getText().toString().trim();
		String text2 = mBio.getText().toString().trim();
		String text3 = mUrl.getText().toString().trim();
		String text4 = mDisplayName.getText().toString().trim();

		if ((TextUtils.isEmpty(text1)) || (TextUtils.isEmpty(text2))
				|| (TextUtils.isEmpty(text3)) || (TextUtils.isEmpty(text4))) {
			btnDone.setEnabled(false);
		} else if ((TextUtils.getTrimmedLength(text2) > 160)) {
			btnDone.setEnabled(false);
		} else {
			btnDone.setEnabled(true);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.done:
			if (gps.canGetLocation()) {
				user.put(
						"location",
						new ParseGeoPoint(gps.getLatitude(), gps.getLongitude()));
			}
			user.put("bio", mBio.getText().toString().trim());
			user.put("website", mUrl.getText().toString().trim());
			user.put("name", mName.getText().toString().trim());
			user.put("display_name", mDisplayName.getText().toString().trim());
			user.saveEventually();
			getFragmentManager().popBackStackImmediate();
			break;
		case R.id.cancel:
			getActivity().onBackPressed();
			break;
		case R.id.profilePic:
			pickPhoto();
			break;
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			setProfileValues();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setProfileValues() {
		// TODO Auto-generated method stub
		mName.setText(user.getString("name"));
		mDisplayName.setText(user.getString("display_name"));
		mBio.setText(user.getString("bio"));
		mUrl.setText(user.getString("website"));
		textCount.setText(user.getString("bio").length() + "/160");
		Picasso.with(getActivity())
				.load(user.getParseFile("profile_picture").getUrl())
				.into(mProfilePicture);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			getActivity();
			if (resultCode == Activity.RESULT_OK) {
				try {
					String filePath = getPath(data.getData());
					DialogFragment fragment = CropWindowDialog
							.newInstance(filePath);
					fragment.show(getFragmentManager(),
							"Profile Picture Crop Fragment");
				} catch (Exception ex) {
					Toast.makeText(getActivity().getApplicationContext(),
							"An Error Occured", Toast.LENGTH_LONG).show();
					ex.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private String getPath(Uri uri) {
		// TODO Auto-generated method stub
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().managedQuery(uri, projection, null, null,
				null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String filePath = cursor.getString(column_index);
		return filePath;
	}

	public void pickPhoto() {
		Intent i = new Intent();
		i.setType("image/*");
		i.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(i, "Select Picture"), 0);
	}

}
