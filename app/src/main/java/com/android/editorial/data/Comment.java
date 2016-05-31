package com.android.editorial.data;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comments")
public class Comment extends ParseObject {

	public void setCreatedBy(ParseUser value) {
		put("created_by", value);
	}

	public void setEditorial(Editorial post) {
		put("editorials", post);
	}

	public void setComment(String comment) {
		put("content", comment);
	}

	public void setLocation(ParseGeoPoint point) {
		put("location", point);
	}

	public ParseUser getCreatedBy() {
		try{
			ParseUser user = getParseUser("created_by").fetchIfNeeded();
			return user;
		}catch(Exception e){
			e.printStackTrace();
		}
		return getParseUser("created_by");
	}

	public Editorial getEditorial() {
		return (Editorial) getParseObject("editorials");
	}

	public String getComment() {
		return getString("content");
	}

	public ParseGeoPoint getLocation() {
		return getParseGeoPoint("location");
	}

}
