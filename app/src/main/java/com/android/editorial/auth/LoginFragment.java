package com.android.editorial.auth;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class LoginFragment extends Fragment implements OnClickListener {

	private EditText mEmail, mPassword;
	private Button btnLogin;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.com_editorial_ui_log_in_fragment,
				container, false);

		mEmail = (EditText) v.findViewById(R.id.email);
		mPassword = (EditText) v.findViewById(R.id.password);

		btnLogin = (Button) v.findViewById(R.id.login);

		mEmail.addTextChangedListener(mTextWatcher);
		mPassword.addTextChangedListener(mTextWatcher);

		btnLogin.setOnClickListener(this);

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
			btnLogin.setEnabled(false);
		} else if ((TextUtils.getTrimmedLength(text2) < 6)) {
			btnLogin.setEnabled(false);
		} else {
			btnLogin.setEnabled(true);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login:
			new LoginUser().execute();
			break;
		}
	}

	class LoginUser extends AsyncTask<Void, Void, ParseUser> {

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
				ParseUser user = ParseUser.logIn(mEmail.getText().toString()
						.trim(), mPassword.getText().toString().trim());
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
				getActivity().finish();
				Intent i = new Intent(getActivity(), MainActivity.class);
				startActivity(i);
			} else {
				Toast.makeText(getActivity(),
						"Invalid username/password combination",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

}
