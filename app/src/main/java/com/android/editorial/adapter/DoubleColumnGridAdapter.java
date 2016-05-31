package com.android.editorial.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.editorial.R;
import com.android.editorial.data.Editorial;
import com.android.editorial.profile.GridEditorialView;
import com.squareup.picasso.Picasso;

public class DoubleColumnGridAdapter extends BaseAdapter {

	private Activity _activity;
	private List<Editorial> editorials = new ArrayList<Editorial>();
	private int imageWidth;
	private GridEditorialView gridEditorialView;

	public DoubleColumnGridAdapter(Activity activity,
			List<Editorial> editorials, int imageWidth,
			GridEditorialView gridEditorialView) {
		this._activity = activity;
		this.editorials = editorials;
		this.imageWidth = imageWidth;
		this.gridEditorialView = gridEditorialView;
	}

	@Override
	public int getCount() {
		return this.editorials.size();
	}

	@Override
	public Object getItem(int position) {
		return this.editorials.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(_activity,
					R.layout.com_editorial_ui_grid_item, null);
		}

		ImageView editroialImageView = (ImageView) convertView
				.findViewById(R.id.editorial_picture);
		TextView editorialName = (TextView) convertView
				.findViewById(R.id.editorial_name);

		editorialName.setText(editorials.get(position).getEditorialName());
		editorialName.setWidth(imageWidth);

		editroialImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		Picasso.with(_activity)
				.load(editorials.get(position).getEditorialCoverPhoto()
						.getUrl()).resize(imageWidth, imageWidth).centerCrop()
				.into(editroialImageView);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gridEditorialView.clickedEditorial(editorials.get(position)
						.getObjectId());
			}
		});

		return convertView;
	}

	public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
		try {

			File f = new File(filePath);

			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			final int REQUIRED_WIDTH = WIDTH;
			final int REQUIRED_HIGHT = HIGHT;
			int scale = 4;
			while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
					&& o.outHeight / scale / 2 >= REQUIRED_HIGHT)
				scale *= 4;

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}