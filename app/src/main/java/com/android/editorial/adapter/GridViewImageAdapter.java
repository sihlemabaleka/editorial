package com.android.editorial.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.editorial.create.ImageSelect;
import com.android.editorial.views.CheckableImageView;

public class GridViewImageAdapter extends BaseAdapter {

	private Activity _activity;
	private ArrayList<String> _filePaths = new ArrayList<String>();
	private int imageWidth;
	private ImageSelect fragment;
	private ArrayList<String> selectedImages = new ArrayList<String>();

	public GridViewImageAdapter(Activity activity, ArrayList<String> filePaths,
			int imageWidth, ImageSelect imageSelect) {
		this._activity = activity;
		this._filePaths = filePaths;
		this.imageWidth = imageWidth;
		this.fragment = imageSelect;
	}

	@Override
	public int getCount() {
		return this._filePaths.size();
	}

	@Override
	public Object getItem(int position) {
		return this._filePaths.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final CheckableImageView imageView;
		if (convertView == null) {
			imageView = new CheckableImageView(_activity);
		} else {
			imageView = (CheckableImageView) convertView;
		}

		// get screen dimensions
		Bitmap image = decodeFile(_filePaths.get(position), imageWidth,
				imageWidth);

		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setPadding(10, 10, 10, 10);
		imageView.setLayoutParams(new GridView.LayoutParams(imageWidth, imageWidth));
		imageView.setImageBitmap(image);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (imageView.isChecked()) {
					imageView.setChecked(false);
				} else {
					imageView.setChecked(true);
				}
				imageClicked(position);
				fragment.itemClicked(selectedImages.size(), _filePaths.get(position));
			}
		});

		return imageView;
	}

	protected void imageClicked(int position) {
		// TODO Auto-generated method stub
		if (selectedImages.size() < 5) {
			if (!selectedImages.contains(_filePaths.get(position))) {
				selectedImages.add(_filePaths.get(position));
			} else {
				selectedImages.remove(_filePaths.get(position));
			}
		} else if (selectedImages.size() == 5) {
			selectedImages.add(_filePaths.get(position));
			System.out.println(selectedImages.get(position));
			fragment.isAllImagesSelected(selectedImages);
		} else {
			fragment.isAllImagesSelected(selectedImages);
		}
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