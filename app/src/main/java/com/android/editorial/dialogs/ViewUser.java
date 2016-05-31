package com.android.editorial.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.editorial.R;
import com.android.editorial.profile.EditorialView;
import com.android.editorial.views.RoundImageView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

public class ViewUser extends DialogFragment {

	private Button btnEditorials;
	private ImageButton btnOptions;
	private RoundImageView mProfilePicture;
	private TextView mName, mBio, mDisplayname;
	private Picasso picasso;
	private Button btnFriendCount;

	public static ViewUser newInstance(String userId) {
		ViewUser fragment = new ViewUser();
		Bundle args = new Bundle();
		args.putString("objectId", userId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.com_editorial_ui_profile_view);

		picasso = Picasso.with(getActivity());

		mProfilePicture = (RoundImageView) dialog.findViewById(R.id.profilePic);
		mName = (TextView) dialog.findViewById(R.id.name);
		mBio = (TextView) dialog.findViewById(R.id.bio);
		btnOptions = (ImageButton) dialog.findViewById(R.id.options);
		mDisplayname = (TextView) dialog.findViewById(R.id.display_name);

		btnFriendCount = (Button) dialog.findViewById(R.id.friend_count);
		btnEditorials = (Button) dialog.findViewById(R.id.post_count);
		btnEditorials.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) { // TODO Auto-generated method
				getFragmentManager().beginTransaction()
						.replace(R.id.container, new EditorialView())
						.addToBackStack("profile").commit();
			}
		});

		btnOptions.setVisibility(View.GONE);

		getProfileValues();

		return dialog;
	}

	private void getProfileValues() {
		// TODO Auto-generated method stub
		ParseQuery<ParseUser> user_query = ParseUser.getQuery();
		user_query.getInBackground(getArguments().getString("objectId"),
				new GetCallback<ParseUser>() {

					@Override
					public void done(ParseUser user, ParseException e) {
						// TODO Auto-generated method stub
						if (e != null)
							return;

						try {
							mName.setText(user.getString("name"));
							if (!TextUtils.isEmpty(user.getString("bio"))) {
								mBio.setText(user.getString("bio"));
								mBio.setVisibility(View.VISIBLE);
							}

							if (!TextUtils.isEmpty(user
									.getString("display_name"))) {
								mDisplayname.setText(user
										.getString("display_name"));
								mDisplayname.setVisibility(View.VISIBLE);
							}

							btnEditorials.setText(user
									.getInt("editorial_count") + "\nEditorials");
							btnFriendCount.setText(user.getInt("friendCount")
									+ "\nFriends");

							picasso.load(
									user.getParseFile("profile_picture")
											.getUrl()).into(mProfilePicture);
						} catch (Exception ex) {
							ex.printStackTrace();
						}

					}
				});

	}

}
