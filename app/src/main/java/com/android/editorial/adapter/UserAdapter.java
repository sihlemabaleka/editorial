package com.android.editorial.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.editorial.R;
import com.android.editorial.dialogs.ViewUser;
import com.android.editorial.helpers.OfflineMethods;
import com.android.editorial.views.RoundImageView;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

public class UserAdapter extends BaseAdapter {

	private Activity activity;
	private LayoutInflater inflater;
	private List<ParseUser> feedItems;

	public UserAdapter(Activity activity, List<ParseUser> feedItems2) {
		this.activity = activity;
		this.feedItems = feedItems2;
	}

	@Override
	public int getCount() {
		return feedItems.size();
	}

	@Override
	public Object getItem(int location) {
		return feedItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(
					R.layout.com_editorial_ui_select_user, null);

		TextView username = (TextView) convertView.findViewById(R.id.name);
		TextView mBio = (TextView) convertView.findViewById(R.id.bio);
		final ToggleButton toggle = (ToggleButton) convertView
				.findViewById(R.id.btnFollowing);
		RoundImageView userprofilePicture = (RoundImageView) convertView
				.findViewById(R.id.profilePic);
		final ProgressBar pBar = (ProgressBar) convertView
				.findViewById(R.id.progressBar1);

		final ParseUser user = feedItems.get(position);

		username.setText(user.getString("name"));
		mBio.setText(user.getString("bio"));
		Picasso.with(activity)
				.load(user.getParseFile("profile_picture").getUrl()).fit()
				.into(userprofilePicture);

		if (user.equals(ParseUser.getCurrentUser())) {
			pBar.setVisibility(View.INVISIBLE);
			toggle.setVisibility(View.INVISIBLE);
		} else {
			pBar.setVisibility(View.INVISIBLE);
			toggle.setVisibility(View.VISIBLE);
		}

		final ParseUser currentUser = ParseUser.getCurrentUser();

		final OfflineMethods networking = new OfflineMethods(activity);
		if (networking.isFollowing(user, currentUser)) {
			toggle.setChecked(true);
		} else {
			toggle.setChecked(false);
		}

		userprofilePicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ViewUser userDialog = ViewUser.newInstance(user.getObjectId());
				userDialog.show(activity.getFragmentManager(), "search");
			}
		});

		toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (toggle.isShown()) {
					if (isChecked) {
						networking.FollowUser(user, currentUser);
					} else {
						networking.unFollowUser(user, currentUser);
					}
				}
			}
		});

		return convertView;
	}

}