package com.android.editorial.feed;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.editorial.AuthenticationActivity;
import com.android.editorial.R;
import com.android.editorial.UploadActivity;
import com.android.editorial.data.Editorial;
import com.android.editorial.data.Piece;
import com.android.editorial.profile.UserProfile;
import com.android.editorial.views.RoundImageView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

public class UserFragment extends Fragment implements OnClickListener {

	private RelativeLayout mProfile;
	private RoundImageView mProfilePicture;
	private TextView mName, mBio;
	private Button btnNewEditorial, btnLogOut;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.com_editorial_ui_options, container,
				false);

		mProfilePicture = (RoundImageView) v.findViewById(R.id.profilePic);
		mName = (TextView) v.findViewById(R.id.name);
		mBio = (TextView) v.findViewById(R.id.bio);

		mProfile = (RelativeLayout) v.findViewById(R.id.profile);
		mProfile.setOnClickListener(this);

		btnNewEditorial = (Button) v.findViewById(R.id.new_editorial);
		btnNewEditorial.setOnClickListener(this);

		btnLogOut = (Button) v.findViewById(R.id.log_out);
		btnLogOut.setOnClickListener(this);

		setProfile();

		return v;
	}

	private void setProfile() {
		// TODO Auto-generated method stub
		ParseUser user = ParseUser.getCurrentUser();
		mName.setText(user.getString("name"));
		mBio.setText(user.getString("bio"));
		Picasso.with(getActivity())
				.load(user.getParseFile("profile_picture").getUrl())
				.into(mProfilePicture);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.profile:
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new UserProfile())
					.addToBackStack("profile").commit();
			break;
		case R.id.new_editorial:
			Intent i = new Intent(getActivity(), UploadActivity.class);
			startActivity(i);
			break;
		case R.id.log_out:
			ParseUser.logOutInBackground(new LogOutCallback() {

				@Override
				public void done(ParseException e) {
					// TODO Auto-generated method stub
					if (e == null) {
						Piece.unpinAllInBackground();
						Editorial.unpinAllInBackground();

						getActivity().finish();
						startActivity(new Intent(getActivity(),
								AuthenticationActivity.class));
					}
				}
			});
			break;

		default:
			break;
		}
	}

}
