package com.android.editorial.create;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.editorial.R;
import com.android.editorial.adapter.GridViewImageAdapter;
import com.android.editorial.helpers.AppConstant;
import com.android.editorial.helpers.Utils;

public class ImageSelect extends Fragment implements OnClickListener {

	private Utils utils;
	private ArrayList<String> imagePaths = new ArrayList<String>();
	private GridViewImageAdapter adapter;
	private ArrayList<String> selectImagePaths = new ArrayList<String>();
	private GridView gridView;
	private int columnWidth;
	private TextView textImageCount;

	private ImageButton btnNext, btnBack;

	public static ImageSelect newInstance(String mCategory, String mName,
			String mUrl) {
		// TODO Auto-generated method stub
		ImageSelect fragment = new ImageSelect();
		Bundle bundle = new Bundle();
		bundle.putString("category", mCategory);
		bundle.putString("name", mName);
		bundle.putString("url", mUrl);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(
				R.layout.com_editorial_ui_editorial_image_select, container,
				false);

		gridView = (GridView) v.findViewById(R.id.grid_view);
		textImageCount = (TextView) v.findViewById(R.id.image_count);
		btnBack = (ImageButton) v.findViewById(R.id.back);
		btnNext = (ImageButton) v.findViewById(R.id.next);
		btnBack.setOnClickListener(this);
		btnNext.setOnClickListener(this);

		utils = new Utils(getActivity());

		// Initilizing Grid View
		InitilizeGridLayout();

		// loading all image paths from SD card
		imagePaths = utils.getFilePaths();

		// Gridview adapter
		adapter = new GridViewImageAdapter(getActivity(), imagePaths,
				columnWidth, this);

		// setting grid view adapter
		gridView.setAdapter(adapter);

		return v;
	}

	public void itemClicked(int count, String mUrl) {
		textImageCount.setText(count + " of 5");

		if (count < 5) {
			if (!selectImagePaths.contains(mUrl)) {
				selectImagePaths.add(mUrl);
			} else {
				selectImagePaths.remove(mUrl);
			}
		} else if (selectImagePaths.size() == 5) {
			selectImagePaths.add(mUrl);
			System.out.println(mUrl);
			isAllImagesSelected(selectImagePaths);
		} else {
			isAllImagesSelected(selectImagePaths);
		}
	}

	public void isAllImagesSelected(ArrayList<String> filePaths) {
		getFragmentManager()
				.beginTransaction()
				.replace(
						R.id.container,
						ImageCrop.newInstance(
								getArguments().getString("category"),
								getArguments().getString("name"),
								getArguments().getString("url"), filePaths))
				.addToBackStack("profile").commit();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.next:
			getFragmentManager()
					.beginTransaction()
					.replace(
							R.id.container,
							ImageCrop.newInstance(
									getArguments().getString("category"),
									getArguments().getString("name"),
									getArguments().getString("url"),
									selectImagePaths))
					.addToBackStack("profile").commit();
			break;
		case R.id.back:
			getActivity().onBackPressed();
			break;
		default:
			break;
		}
	}

	private void InitilizeGridLayout() {
		Resources r = getResources();
		float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				AppConstant.GRID_PADDING, r.getDisplayMetrics());

		columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 2) * padding)) / AppConstant.NUM_OF_COLUMNS);

		gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
		gridView.setColumnWidth(columnWidth);
		gridView.setStretchMode(GridView.NO_STRETCH);
		gridView.setPadding((int) padding, (int) padding, (int) padding,
				(int) padding);
		gridView.setHorizontalSpacing((int) padding);
		gridView.setVerticalSpacing((int) padding);
	}

	public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
		try {

			File f = new File(filePath);

			int scale = 4;

			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
