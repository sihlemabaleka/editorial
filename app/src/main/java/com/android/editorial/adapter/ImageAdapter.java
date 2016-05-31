package com.android.editorial.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.editorial.R;
import com.android.editorial.data.Editorial;
import com.squareup.picasso.Picasso;

public class ImageAdapter extends BaseAdapter {

	List<Editorial> feedItems;
	LayoutInflater inflater;
	Activity activity;

	public ImageAdapter(Activity activity, List<Editorial> feedItems) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.inflater = LayoutInflater.from(activity);
		this.feedItems = feedItems;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return feedItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.com_editorial_ui_grid_item,
					parent, false);
		}

		ImageView view = (ImageView) convertView
				.findViewById(R.id.editorial_picture);
		final Editorial post = feedItems.get(position);
		Picasso.with(activity).load(post.getEditorialCoverPhoto().getUrl())
				.into(view);

		return convertView;
	}

}
