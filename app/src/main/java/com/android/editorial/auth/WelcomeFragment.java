package com.android.editorial.auth;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.editorial.R;

public class WelcomeFragment extends Fragment implements OnClickListener {

	private Button btnSignUp, btnLogin;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.com_editorial_ui_welcome_screen,
				container, false);

		btnSignUp = (Button) v.findViewById(R.id.sign_up);
		btnLogin = (Button) v.findViewById(R.id.login);

		btnSignUp.setOnClickListener(this);
		btnLogin.setOnClickListener(this);

		return v;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sign_up:
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new SignUpFragment())
					.addToBackStack("signup").commit();
			break;
		case R.id.login:
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new LoginFragment())
					.addToBackStack("login").commit();
			break;
		}
	}
}
