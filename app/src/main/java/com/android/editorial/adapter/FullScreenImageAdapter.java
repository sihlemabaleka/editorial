package com.android.editorial.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.editorial.R;
import com.android.editorial.data.Piece;
import com.android.editorial.views.TouchImageView;
import com.squareup.picasso.Picasso;

public class FullScreenImageAdapter extends PagerAdapter {

	private Activity _activity;
	private List<Piece> pieces;
	private LayoutInflater inflater;

	// constructor
	public FullScreenImageAdapter(Activity activity, List<Piece> editorials) {
		this._activity = activity;
		this.pieces = editorials;
	}

	@Override
	public int getCount() {
		return this.pieces.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		TouchImageView imgDisplay;

		inflater = (LayoutInflater) _activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewLayout = inflater.inflate(
				R.layout.com_editorial_ui_fullscreen_layout, container, false);

		imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);

		Piece piece = pieces.get(position);

		try {
			Picasso.with(_activity).load(piece.getEditorialImage().getUrl())
					.into(imgDisplay);
		} catch (Exception e) {
			e.printStackTrace();
		}


		((ViewPager) container).addView(viewLayout);

		return viewLayout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((RelativeLayout) object);

	}
}