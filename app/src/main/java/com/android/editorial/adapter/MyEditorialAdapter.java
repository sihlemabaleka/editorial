package com.android.editorial.adapter;

import android.app.LauncherActivity;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.editorial.R;
import com.android.editorial.data.Editorial;
import com.android.editorial.views.RoundImageView;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by PantheraS on 2016-05-27.
 */
public class MyEditorialAdapter extends RecyclerView.Adapter<MyEditorialAdapter.MyViewHolder> {

    private Context mContext;
    private List<Editorial> editorials;

    public MyEditorialAdapter(Context mContext, List<Editorial> editorials) {
        this.mContext = mContext;
        this.editorials = editorials;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_collapsible_top, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Editorial editorial = editorials.get(position);
            Picasso.with(mContext).load(editorial.getEditorialCoverPhoto().getUrl()).into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return editorials.size();
    }

   /* class VHHeader extends RecyclerView.ViewHolder {
        TextView txtTitle;
        private Button btnEditorials;
        private RoundImageView mProfilePicture;
        private TextView mName, mBio;
        private Button btnFriendCount;

        public VHHeader(View v) {
            super(v);
            this.mProfilePicture = (RoundImageView) v.findViewById(R.id.profilePic);
            this.mName = (TextView) v.findViewById(R.id.name);
            this.mBio = (TextView) v.findViewById(R.id.bio);
            this.btnFriendCount = (Button) v.findViewById(R.id.friend_count);
            this.btnEditorials = (Button) v.findViewById(R.id.post_count);
        }
    }*/

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.editorial_picture);
        }
    }
}