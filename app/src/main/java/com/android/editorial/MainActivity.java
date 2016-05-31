package com.android.editorial;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.android.editorial.data.Editorial;
import com.android.editorial.data.Interaction;
import com.android.editorial.discover.DiscoverFragment;
import com.android.editorial.feed.HomeFragment;
import com.android.editorial.feed.UserFragment;
import com.android.editorial.helpers.AppConstant;
import com.android.editorial.helpers.OfflineMethods;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MainActivity extends Activity implements OnClickListener {

	private ImageButton btnHome, btnNotifications, btnSearch, btnUserProfile;

	OfflineMethods helpers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (ParseUser.getCurrentUser() == null) {
			finish();
			startActivity(new Intent(getApplicationContext(),
					AuthenticationActivity.class));
			return;
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		helpers = new OfflineMethods(this);
		if (helpers.hasConnectivity()) {
			setContentView(R.layout.com_editorial_ui_container);
		} else {
			setContentView(R.layout.com_editorial_ui_error_loading);
			return;
		}

		try {
			syncWithCloud();
		} catch (Exception e) {
			e.printStackTrace();
		}

		btnHome = (ImageButton) findViewById(R.id.home);
		btnNotifications = (ImageButton) findViewById(R.id.notifications);
		btnUserProfile = (ImageButton) findViewById(R.id.profile);
		btnSearch = (ImageButton) findViewById(R.id.search);

		btnHome.setOnClickListener(this);
		// btnNotifications.setOnClickListener(this);
		btnUserProfile.setOnClickListener(this);
		btnSearch.setOnClickListener(this);

		if (savedInstanceState == null) {
			syncWithCloud();
			getFragmentManager().beginTransaction()
					.add(R.id.container, new HomeFragment()).commit();
			btnHome.setBackgroundResource(R.drawable.icon_active_background);
		}
	}

	private void syncWithCloud() {
		// TODO Auto-generated method stub
		ParseQuery<Editorial> query = ParseQuery.getQuery(Editorial.class);
		query.whereEqualTo("created_by", ParseUser.getCurrentUser());

		ParseQuery<Interaction> friendsQuery = ParseQuery
				.getQuery(Interaction.class);
		friendsQuery.whereEqualTo("type", "follow");
		friendsQuery.whereEqualTo("fromUser", ParseUser.getCurrentUser());

		ParseQuery<Editorial> friendPosts = ParseQuery
				.getQuery(Editorial.class);
		friendPosts
				.whereMatchesKeyInQuery("created_by", "toUser", friendsQuery);

		List<ParseQuery<Editorial>> queries = new ArrayList<ParseQuery<Editorial>>();
		queries.add(query);
		queries.add(friendPosts);

		ParseQuery<Editorial> mainQuery = ParseQuery.or(queries);
		mainQuery.addDescendingOrder("createdAt");
		mainQuery.findInBackground(new FindCallback<Editorial>() {

			@Override
			public void done(final List<Editorial> list, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					Editorial.unpinAllInBackground(
							AppConstant.MAIN_EDITORIAL_FEED,
							new DeleteCallback() {

								@Override
								public void done(ParseException e) {
									// TODO Auto-generated method stub
									if (e == null) {
										Editorial
												.pinAllInBackground(
														AppConstant.MAIN_EDITORIAL_FEED,
														list);
									}
								}
							});
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (!helpers.hasConnectivity())
			return;
		try {
			if (getFragmentManager().getBackStackEntryCount() > 0) {
				FragmentManager.BackStackEntry backEntry = getFragmentManager()
						.getBackStackEntryAt(
								getFragmentManager().getBackStackEntryCount() - 1);
				String mEntry = backEntry.getName();
				switch (mEntry) {
				case "home":
					btnHome.setBackgroundResource(R.drawable.icon_active_background);
					btnHome.setEnabled(false);
					btnUserProfile.setEnabled(true);
					btnNotifications.setEnabled(true);
					btnSearch.setEnabled(true);
					btnSearch
							.setBackgroundResource(android.R.color.transparent);
					btnNotifications
							.setBackgroundResource(android.R.color.transparent);
					btnUserProfile
							.setBackgroundResource(android.R.color.transparent);
					break;
				case "profile":
					btnUserProfile
							.setBackgroundResource(R.drawable.icon_active_background);
					btnUserProfile.setEnabled(false);
					btnHome.setEnabled(true);
					btnNotifications.setEnabled(true);
					btnSearch.setEnabled(true);
					btnHome.setBackgroundResource(android.R.color.transparent);
					btnNotifications
							.setBackgroundResource(android.R.color.transparent);
					btnSearch
							.setBackgroundResource(android.R.color.transparent);
					break;
				case "notifications":
					btnNotifications
							.setBackgroundResource(R.drawable.icon_active_background);
					btnNotifications.setEnabled(false);
					btnUserProfile.setEnabled(true);
					btnHome.setEnabled(true);
					btnSearch.setEnabled(true);
					btnUserProfile
							.setBackgroundResource(android.R.color.transparent);
					btnHome.setBackgroundResource(android.R.color.transparent);
					btnSearch
							.setBackgroundResource(android.R.color.transparent);
					break;
				case "search":
					btnSearch
							.setBackgroundResource(R.drawable.icon_active_background);
					btnHome.setEnabled(true);
					btnUserProfile.setEnabled(true);
					btnNotifications.setEnabled(true);
					btnSearch.setEnabled(false);
					btnHome.setBackgroundResource(android.R.color.transparent);
					btnUserProfile
							.setBackgroundResource(android.R.color.transparent);
					btnNotifications
							.setBackgroundResource(android.R.color.transparent);
					break;
				}
			} else {
				btnHome.setBackgroundResource(R.drawable.icon_active_background);
				btnHome.setEnabled(false);
				btnUserProfile.setEnabled(true);
				btnNotifications.setEnabled(true);
				btnSearch.setEnabled(true);
				btnSearch.setBackgroundResource(android.R.color.transparent);
				btnNotifications
						.setBackgroundResource(android.R.color.transparent);
				btnUserProfile
						.setBackgroundResource(android.R.color.transparent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.home:
			btnHome.setBackgroundResource(R.drawable.icon_active_background);
			getFragmentManager().popBackStack();
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new HomeFragment())
					.addToBackStack("home").commit();
			btnUserProfile.setBackgroundResource(android.R.color.transparent);
			btnNotifications.setBackgroundResource(android.R.color.transparent);
			btnSearch.setBackgroundResource(android.R.color.transparent);
			btnHome.setEnabled(false);
			btnUserProfile.setEnabled(true);
			btnNotifications.setEnabled(true);
			btnSearch.setEnabled(true);
			break;
		case R.id.profile:
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new UserFragment())
					.addToBackStack("profile").commit();
			btnUserProfile
					.setBackgroundResource(R.drawable.icon_active_background);
			btnHome.setBackgroundResource(android.R.color.transparent);
			btnNotifications.setBackgroundResource(android.R.color.transparent);
			btnSearch.setBackgroundResource(android.R.color.transparent);
			btnUserProfile.setEnabled(false);
			btnHome.setEnabled(true);
			btnNotifications.setEnabled(true);
			btnSearch.setEnabled(true);
			break;
		case R.id.search:
			getFragmentManager().beginTransaction()
					.replace(R.id.container, new DiscoverFragment())
					.addToBackStack("search").commit();
			btnSearch.setBackgroundResource(R.drawable.icon_active_background);
			btnHome.setBackgroundResource(android.R.color.transparent);
			btnNotifications.setBackgroundResource(android.R.color.transparent);
			btnUserProfile.setBackgroundResource(android.R.color.transparent);
			btnSearch.setEnabled(false);
			btnHome.setEnabled(true);
			btnNotifications.setEnabled(true);
			btnSearch.setEnabled(true);
			break;
		}
	}
}
