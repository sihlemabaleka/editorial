package com.android.editorial.adapter;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.editorial.FullScreenActivity;
import com.android.editorial.R;
import com.android.editorial.data.Editorial;
import com.android.editorial.dialogs.CommentsDialog;
import com.android.editorial.dialogs.ViewUser;
import com.android.editorial.helpers.OfflineMethods;
import com.android.editorial.views.RoundImageView;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

public class BasicAdapter extends BaseAdapter {

	private List<Editorial> items;
	private Activity activity;

	public BasicAdapter(Activity activity, List<Editorial> items) {
		this.items = items;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		final Editorial item = items.get(position);
		final OfflineMethods helper = new OfflineMethods(activity);
		if (convertView == null) {
			convertView = View.inflate(activity,
					R.layout.com_editorial_ui_feed_item, null);

			holder = new ViewHolder();

			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.timestamp = (TextView) convertView
					.findViewById(R.id.timestamp);
			holder.statusMsg = (TextView) convertView
					.findViewById(R.id.txtStatusMsg);
			holder.profilePic = (RoundImageView) convertView
					.findViewById(R.id.profilePic);
			holder.feedImageView = (ImageView) convertView
					.findViewById(R.id.editorial_picture);
			holder.txtCommentCount = (TextView) convertView
					.findViewById(R.id.commentCount);
			holder.btnComment = (Button) convertView
					.findViewById(R.id.comment_button);
			holder.isLiked = (ToggleButton) convertView.findViewById(R.id.like);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (!holder.isLiked.isShown()) {
			new ImageDownloaderTask(holder, item, helper, position).execute();
		} else {
			holder.isLiked.setChecked(holder.isEditorialLiked);
		}

		holder.name.setText(item.getCreatedBy().getString("name"));
		Picasso.with(activity)
				.load(item.getCreatedBy().getParseFile("profile_picture")
						.getUrl()).fit().into(holder.profilePic);
		holder.timestamp.setText(setTimestamp(item.getCreatedAt()));
		holder.statusMsg.setText(item.getEditorialName());
		holder.statusMsg.setVisibility(View.VISIBLE);
		holder.name.setText(item.getCreatedBy().getString("name"));
		Picasso.with(activity).load(item.getEditorialCoverPhoto().getUrl())
				.into(holder.feedImageView);
		setCommentCount(holder.txtCommentCount, item.getInt("commentCount"));

		holder.profilePic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ViewUser userDialog = ViewUser.newInstance(item.getCreatedBy()
						.getObjectId());
				userDialog.show(activity.getFragmentManager(), "home");
			}
		});

		holder.feedImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(activity, FullScreenActivity.class);
				i.putExtra("objectId", item.getObjectId());
				activity.startActivity(i);
			}
		});

		holder.btnComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CommentsDialog dialog = CommentsDialog.newInstance(item
						.getObjectId());
				dialog.show(activity.getFragmentManager(), "comments");
			}
		});

		return convertView;
	}

	static class ViewHolder {
		TextView name, timestamp, statusMsg, txtCommentCount;
		RoundImageView profilePic;
		ImageView feedImageView;
		Button btnComment;
		ToggleButton isLiked;
		int position;
		Boolean isEditorialLiked;
	}

	class ImageDownloaderTask extends AsyncTask<Void, Void, Boolean> {
		private final ViewHolder holder;
		private Editorial editorial;
		OfflineMethods helper;
		int position;

		public ImageDownloaderTask(ViewHolder holder, Editorial editorial,
				OfflineMethods helper, int position) {
			this.holder = holder;
			this.editorial = editorial;
			this.helper = helper;
			this.position = position;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			return helper.isLiked(ParseUser.getCurrentUser(), editorial);
		}

		@Override
		protected void onPostExecute(Boolean isLiked) {
			if (isCancelled()) {
				isLiked = false;
			}

			holder.isEditorialLiked = isLiked;

			if (holder != null) {
				final ToggleButton btnLike = holder.isLiked;
				if (btnLike != null) {
					if (isLiked != null) {
						btnLike.setChecked(isLiked);
					} else {
						btnLike.setChecked(false);
					}
					btnLike.setVisibility(View.VISIBLE);
					btnLike.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (btnLike.isChecked()) {
								helper.likeEditorial(
										ParseUser.getCurrentUser(), editorial);
							} else {
								helper.unlikePost(ParseUser.getCurrentUser(),
										editorial);
							}
						}
					});
				}
			}
		}
	}

	private CharSequence setTimestamp(Date createdAt) {
		// TODO Auto-generated method stub
		return DateUtils.getRelativeTimeSpanString(createdAt.getTime(),
				System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
	}

	protected void setCommentCount(TextView view, int i) {
		// TODO Auto-generated method stub
		if (i > 0) {
			view.setVisibility(View.VISIBLE);
			if (i == 1) {
				view.setText(i + " comment");
			} else {
				view.setText(i + " comments");
			}
		} else {
			view.setVisibility(View.GONE);
		}
	}

}
