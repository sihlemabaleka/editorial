package com.android.editorial.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.android.editorial.R;
import com.android.editorial.data.Comment;
import com.android.editorial.data.Editorial;
import com.android.editorial.dialogs.ViewUser;
import com.android.editorial.views.RoundImageView;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.Priority;

public class CommentAdapter extends ParseQueryAdapter<Comment> {

	private Activity activity;

	public CommentAdapter(Activity activity,
			com.parse.ParseQueryAdapter.QueryFactory<Comment> queryFactory) {
		super(activity, queryFactory);
		// TODO Auto-generated constructor stub
		this.activity = activity;
	}

	@Override
	public void setObjectsPerPage(int objectsPerPage) {
		// TODO Auto-generated method stub
		setObjectsPerPage(10);
	}

	@Override
	public void setPaginationEnabled(boolean paginationEnabled) {
		// TODO Auto-generated method stub
		setPaginationEnabled(true);
	}

	@Override
	public View getItemView(final Comment item, View convertView,
			ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = View.inflate(getContext(),
					R.layout.com_editorial_ui_comment_item, null);
		}

		super.getItemView(item, convertView, viewGroup);

		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView timestamp = (TextView) convertView
				.findViewById(R.id.timestamp);
		TextView commentTextView = (TextView) convertView
				.findViewById(R.id.comment);
		RoundImageView profilePic = (RoundImageView) convertView
				.findViewById(R.id.profilePic);
		LinearLayout mLinearLayout = (LinearLayout) convertView
				.findViewById(R.id.comment_layout);

		if (item == null)
			return null;

		final Editorial editorial = item.getEditorial();

		Picasso picasso = Picasso.with(activity);

		name.setText(item.getCreatedBy().getString("name"));

		// Converting timestamp into x ago format
		CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(item
				.getCreatedAt().getTime(), System.currentTimeMillis(),
				DateUtils.MINUTE_IN_MILLIS);
		timestamp.setText(timeAgo);

		// Chcek for empty status message
		if (!TextUtils.isEmpty(item.getComment())) {
			commentTextView.setText(item.getComment());
			commentTextView.setVisibility(View.VISIBLE);
		} else {
			// status is empty, remove from view
			commentTextView.setVisibility(View.GONE);
		}

		String profilePictureUrl = null;

		try {
			profilePictureUrl = item.getCreatedBy()
					.getParseFile("profile_picture").getUrl();
			name.setText(item.getCreatedBy().getString("name"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// user profile pic
		if (profilePictureUrl != null)
			picasso.setIndicatorsEnabled(true);
		picasso.load(profilePictureUrl).fit().priority(Priority.NORMAL)
				.into(profilePic);

		if (ParseUser.getCurrentUser().equals(item.getCreatedBy())) {
			mLinearLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showPopUpMenu(v);
				}

				private void showPopUpMenu(View v) {
					// TODO Auto-generated method stub
					PopupMenu popup = new PopupMenu(activity, v);
					MenuInflater inflater = popup.getMenuInflater();
					// if
					// (ParseUser.getCurrentUser().equals(postObject.getUser()))
					inflater.inflate(R.menu.comment_options, popup.getMenu());
					// else
					// inflater.inflate(R.menu.post_settings, popup.getMenu());
					popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {

						@Override
						public boolean onMenuItemClick(MenuItem _item) {
							// TODO Auto-generated method stub
							switch (_item.getItemId()) {
							case R.id.delete_comment:
								item.deleteEventually(new DeleteCallback() {

									@Override
									public void done(ParseException arg0) {
										// TODO Auto-generated method stub
										loadObjects();
										editorial.put(
												"commentCount",
												(item.getInt("commentCount") - 1));
										editorial
												.saveEventually(new SaveCallback() {

													@Override
													public void done(
															ParseException arg0) {
														// TODO Auto-generated
														// method stub
													}
												});
									}
								});
								return true;
							default:
								return true;
							}
						}
					});
					popup.show();
				}
			});
		}

		profilePic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ViewUser userDialog = ViewUser.newInstance(item.getCreatedBy()
						.getObjectId());
				userDialog.show(activity.getFragmentManager(), "home");
			}
		});

		return convertView;
	}

	@Override
	public View getNextPageView(View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		return super.getNextPageView(v, parent);
	}

}
