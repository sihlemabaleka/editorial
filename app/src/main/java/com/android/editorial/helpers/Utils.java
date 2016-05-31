package com.android.editorial.helpers;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.Display;
import android.view.WindowManager;

public class Utils {

	private Context _context;

	// constructor
	public Utils(Context context) {
		this._context = context;
	}

	// Reading file paths from SDCard
	public ArrayList<String> getFilePaths() {
		ArrayList<String> filePaths = new ArrayList<String>();
		String PathOfImage = null;
		Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		String[] projection = {MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
		Cursor cursor = _context.getContentResolver().query(uri, projection, null, null, MediaStore.Images.Media.DATE_MODIFIED + " DESC");
		int column_index_data = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		while(cursor.moveToNext()){
			PathOfImage = cursor.getString(column_index_data);
			if(IsSupportedFile(PathOfImage)){
				filePaths.add(PathOfImage);
			}
		}
		return filePaths;
	}

	// Check supported file extensions
	private boolean IsSupportedFile(String filePath) {
		String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
				filePath.length());

		if (AppConstant.FILE_EXTN
				.contains(ext.toLowerCase(Locale.getDefault())))
			return true;
		else
			return false;

	}

	/*
	 * getting screen width
	 */
	@SuppressWarnings("deprecation")
	public int getScreenWidth() {
		int columnWidth;
		WindowManager wm = (WindowManager) _context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		final Point point = new Point();
		try {
			display.getSize(point);
		} catch (java.lang.NoSuchMethodError ignore) { // Older device
			point.x = display.getWidth();
			point.y = display.getHeight();
		}
		columnWidth = point.x;
		return columnWidth;
	}
}