package com.android.editorial.create;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.editorial.R;
import com.android.editorial.adapter.ImageCropGridViewImageAdapter;
import com.android.editorial.data.Editorial;
import com.android.editorial.helpers.AppConstant;
import com.android.editorial.helpers.OfflineMethods;
import com.android.editorial.helpers.Utils;
import com.android.editorial.views.CropImageView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

public class ImageUpload extends Fragment implements OnClickListener {

	private CropImageView imageView;
	private ImageButton btnNext, btnBack, btnRotate;
	private ProgressDialog pDialog;

	private GridView gridView;
	private int columnWidth;
	private Utils utils;
	private ImageCropGridViewImageAdapter adapter;
	private ArrayList<String> array;
	private List<byte[]> imageBytes;

	private OfflineMethods helper;

	public static ImageUpload newInstance(String category, String name,
			String url, ArrayList<String> filepaths) {
		// TODO Auto-generated method stub
		ImageUpload fragment = new ImageUpload();
		Bundle args = new Bundle();
		args.putString("name", name);
		args.putString("category", category);
		args.putString("url", url);
		args.putStringArrayList("filepaths", filepaths);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(
				R.layout.com_editorial_ui_crop_editorial_image, parent, false);

		helper = new OfflineMethods(getActivity());

		imageBytes = new ArrayList<byte[]>();

		gridView = (GridView) v.findViewById(R.id.grid_view);
		imageView = (CropImageView) v.findViewById(R.id.cropImageView1);
		btnNext = (ImageButton) v.findViewById(R.id.next);
		btnBack = (ImageButton) v.findViewById(R.id.back);
		btnRotate = (ImageButton) v.findViewById(R.id.rotate);

		array = getArguments().getStringArrayList("filepaths");

		utils = new Utils(getActivity());

		// Initilizing Grid View
		InitilizeGridLayout();

		btnNext.setOnClickListener(this);
		btnBack.setOnClickListener(this);

		// Gridview adapter
		adapter = new ImageCropGridViewImageAdapter(getActivity(), array,
				columnWidth, this);

		// setting grid view adapter
		gridView.setAdapter(adapter);

		setUpCropWindow(0);

		return v;
	}

	private void InitilizeGridLayout() {
		Resources r = getResources();
		float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				AppConstant.NUM_5_GRID_PADDING, r.getDisplayMetrics());

		columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_5 + 5) * padding)) / AppConstant.NUM_5);

		gridView.setNumColumns(AppConstant.NUM_5);
		gridView.setColumnWidth(columnWidth);
		gridView.setStretchMode(GridView.NO_STRETCH);
	}

	private void setUpCropWindow(int position) {
		// TODO Auto-generated method stub

		try {
			Bitmap croppedImage = BitmapFactory.decodeFile(array.get(position));
			imageView.setImageBitmap(croppedImage);
			imageView.setAspectRatio(100, 100);
			imageView.setFixedAspectRatio(true);
			btnRotate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					imageView.rotateImage(90);
				}
			});
		} catch (Exception ex) {
			Toast.makeText(getActivity().getApplicationContext(),
					"An Error Occured", Toast.LENGTH_LONG).show();
			ex.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			getActivity().onBackPressed();
			break;
		case R.id.next:
			new GetCroppedData().execute();
			break;
		}
	}

	public void launchCamera(View view) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, 100);
	}

	class GetCroppedData extends AsyncTask<Void, Void, List<byte[]>> {

		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(getActivity());
			pDialog.setTitle("Preparing Images");
			pDialog.setMessage("Just a moment...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected List<byte[]> doInBackground(Void... args) {
			// TODO Auto-generated method stub
			for (String url : array) {
				Bitmap bMap = BitmapFactory.decodeFile(url);
				byte[] data = helper.getScaledPhoto(bMap, bMap.getHeight(),
						bMap.getWidth());
				imageBytes.add(data);
			}
			return imageBytes;
		}

		protected void onPostExecute(List<byte[]> images) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();
			if (images.size() > 0) {
				final Editorial editorial = helper.createEditorial(
						getArguments().getString("category"), getArguments()
								.getString("name"),
						getArguments().getString("url"));
				for (byte[] data : images) {
					final ParseFile file = new ParseFile(data);
					file.saveInBackground(new SaveCallback() {

						@Override
						public void done(ParseException e) {
							// TODO Auto-generated method stub
							if (e == null) {
								helper.setEditorialPicture(getArguments()
										.getString("category"), editorial,
										file, "This is a caption");
								helper.setEditorialCoverPhoto(file, editorial);
							} else {
								e.printStackTrace();
							}
						}
					}, new ProgressCallback() {

						@Override
						public void done(Integer value) {
							// TODO Auto-generated method stub
							System.out.println(value);
						}
					});
				}
			}

			getActivity().finish();

		}
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

	public void itemClicked(int position) {
		// TODO Auto-generated method stub
		setUpCropWindow(position);
	}

}