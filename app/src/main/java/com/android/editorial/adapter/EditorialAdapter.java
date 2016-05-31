package com.android.editorial.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by PantheraS on 2016-05-26.
 */
public class EditorialAdapter extends RecyclerView.Adapter<EditorialAdapter.MyViewHolder> {

    private List<Editorial> items;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, timestamp, statusMsg, txtCommentCount;
        public RoundImageView profilePic;
        public ImageView feedImageView;
        public Button btnComment;
        public ToggleButton isLiked;
        public Boolean isEditorialLiked;
        public ProgressBar pBar;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            timestamp = (TextView) view
                    .findViewById(R.id.timestamp);
            statusMsg = (TextView) view
                    .findViewById(R.id.txtStatusMsg);
            profilePic = (RoundImageView) view
                    .findViewById(R.id.profilePic);
            feedImageView = (ImageView) view
                    .findViewById(R.id.editorial_picture);
            txtCommentCount = (TextView) view
                    .findViewById(R.id.commentCount);
            btnComment = (Button) view
                    .findViewById(R.id.comment_button);
            isLiked = (ToggleButton) view.findViewById(R.id.like);
            pBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }

    public EditorialAdapter(Activity activity, List<Editorial> items) {
        this.items = items;
        this.activity = activity;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final OfflineMethods helper = new OfflineMethods(activity);
        final Editorial item = items.get(position);
        holder.name.setText(item.getCreatedBy().getString("name"));
        Picasso.with(activity)
                .load(item.getCreatedBy().getParseFile("profile_picture")
                        .getUrl()).fit().into(holder.profilePic);
        holder.timestamp.setText(setTimestamp(item.getCreatedAt()));
        holder.statusMsg.setText(item.getEditorialName());
        holder.statusMsg.setVisibility(View.VISIBLE);
        holder.name.setText(item.getCreatedBy().getString("name"));
        Picasso.with(activity).load(item.getEditorialCoverPhoto().getUrl())
                .into(holder.feedImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.pBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.pBar.setVisibility(View.GONE);
                    }
                });
        setCommentCount(holder.txtCommentCount, item.getInt("commentCount"));

        if(!holder.isLiked.isShown()){
            holder.isLiked.setChecked(helper.isLiked(ParseUser.getCurrentUser(), item));
            holder.isLiked.setVisibility(View.VISIBLE);
        } else {
            holder.isLiked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        helper.unlikePost(ParseUser.getCurrentUser(),
                                item);
                    } else {
                        helper.likeEditorial(
                                ParseUser.getCurrentUser(), item);
                    }
                }
            });
        }

        holder.profilePic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ViewUser userDialog = ViewUser.newInstance(item.getCreatedBy()
                        .getObjectId());
                userDialog.show(activity.getFragmentManager(), "home");
            }
        });

        holder.feedImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(activity, FullScreenActivity.class);
                i.putExtra("objectId", item.getObjectId());
                activity.startActivity(i);
            }
        });

        holder.btnComment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CommentsDialog dialog = CommentsDialog.newInstance(item
                        .getObjectId());
                dialog.show(activity.getFragmentManager(), "comments");
            }
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.com_editorial_ui_feed_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return items.size();
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
