package com.android.editorial.data;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Piece")
public class Piece extends ParseObject {

	public void setPieceCaption(String value) {
		put("editorial_image_caption", value);
	}

	public String getPieceCaption() {
		return getString("editorial_image_caption");
	}

	public void setEditorialImage(ParseFile value) {
		put("editorial_image", value);
	}

	public ParseFile getEditorialImage() {
		return getParseFile("editorial_image");
	}

	public void setEditorial(Editorial value) {
		put("editorials", value);
	}

	public Editorial getEditorial() {
		return (Editorial) getParseObject("editorials");
	}

	public void setCategory(String value) {
		put("category", value);
	}

	public String getCategory() {
		return getString("category");
	}

	public void setCreatedBy(ParseUser value) {
		put("created_by", value);
	}

	public ParseUser getCreatedBy() {
		return getParseUser("created_by");
	}

}
