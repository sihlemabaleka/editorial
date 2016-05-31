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

import com.android.editorial.create.ImageUpload;

public class ImageCropGridViewImageAdapter extends BaseAdapter {

	private Activity _activity;
	private ArrayList<String> _filePaths = new ArrayList<String>();
	private int imageWidth;
	private ImageUpload fragment;

	public ImageCropGridViewImageAdapter(Activity activity, ArrayList<String> filePaths,
			int imageWidth, ImageUpload imageUpload) {
		this._activity = activity;
		this._filePaths = filePaths;
		this.imageWidth = imageWidth;
		this.fragment = imageUpload;
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
		final ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(_activity);
		} else {
			imageView = (ImageView) convertView;
		}

		// get screen dimensions
		Bitmap image = decodeFile(_filePaths.get(position), imageWidth,
				imageWidth);

		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setPadding(10, 10, 10, 10);
		imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,
				imageWidth));
		imageView.setImageBitmap(image);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fragment.itemClicked(position);
			}
		});

		return imageView;
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