package com.android.editorial.auth;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.editorial.MainActivity;
import com.android.editorial.R;
import com.parse.ParseException;
import com.parse.ParseUser;

public class SignUpFragment extends Fragment implements OnClickListener {

	private EditText mEmail, mPassword, mName;
	private Button btnSignUp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.com_editorial_ui_sign_up_fragment,
				container, false);

		mEmail = (EditText) v.findViewById(R.id.email);
		mPassword = (EditText) v.findViewById(R.id.password);
		mName = (EditText) v.findViewById(R.id.name);

		btnSignUp = (Button) v.findViewById(R.id.sign_up);

		mEmail.addTextChangedListener(mTextWatcher);
		mPassword.addTextChangedListener(mTextWatcher);

		btnSignUp.setOnClickListener(this);

		checkFieldsForEmptyValues();

		return v;
	}

	private TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

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
		String text1 = mEmail.getText().toString().trim();
		String text2 = mPassword.getText().toString().trim();

		if ((TextUtils.isEmpty(text1)) || (TextUtils.isEmpty(text2))) {
			btnSignUp.setEnabled(false);
		} else if ((TextUtils.getTrimmedLength(text2) < 6)) {
			btnSignUp.setEnabled(false);
		} else {
			btnSignUp.setEnabled(true);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sign_up:
			new SignUpUser().execute();
			break;
		}
	}

	class SignUpUser extends AsyncTask<Void, Void, ParseUser> {

		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pDialog = new ProgressDialog(getActivity());
			pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			pDialog.setMessage("Just a moment");
			pDialog.show();
		}

		@Override
		protected ParseUser doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				ParseUser user = new ParseUser();
				user.setEmail(mEmail.getText().toString().trim());
				user.setPassword(mPassword.getText().toString().trim());
				user.setUsername(mEmail.getText().toString().trim());
				user.put("name", mName.getText().toString().trim());
				user.signUp();
				return user;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(ParseUser user) {
			// TODO Auto-generated method stub
			super.onPostExecute(user);
			pDialog.dismiss();
			if (user != null) {
				if (user.isNew()) {
					pickPhoto();
				} else {
					getActivity().finish();
					Intent i = new Intent(getActivity(), MainActivity.class);
					startActivity(i);
				}
			} else {
				Toast.makeText(
						getActivity(),
						"Email already exists. Please try to retrieve it or enter a different email address",
						Toast.LENGTH_SHORT).show();
				mEmail.setError("Email already exists.");
				mEmail.requestFocus();
			}
		}

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
					FIrstTimeUser fragment = FIrstTimeUser
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
