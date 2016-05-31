package com.android.editorial.helpers;

import android.graphics.Bitmap;

public class Helper {
	
	public int getDominantColor(Bitmap bitmap){
		if(bitmap == null)
			throw new NullPointerException();
		
		Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, false);
		int color = bmp.getPixel(0, 0);
		return color;
	}

}
