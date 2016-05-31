package com.android.editorial.create;

import com.android.editorial.data.Editorial;
import com.parse.ParseFile;

public interface OnImageUpload {

	public void onImageUploaded(Boolean value);
	
	public void onCoverPhotoSelected(ParseFile piece, Editorial editorial);

}
