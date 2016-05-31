package com.android.editorial.data;

import java.util.List;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Editorial")
public class Editorial extends ParseObject {

	public void setEditorialName(String value) {
		put("editorial_name", value);
	}

	public String getEditorialName() {
		return getString("editorial_name");
	}

	public void setCategory(String mCategory) {
		put("category", mCategory);
	}

	public String getCategory() {
		return getString("category");
	}

	public void setCreatedBy(ParseUser value) {
		put("created_by", value);
	}

	public ParseUser getCreatedBy() {
		try {
			ParseUser user = getParseUser("created_by").fetchIfNeeded();
			return user;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void setEditorialAddress(String value) {
		put("editorial_url_address", value);
	}

	public String getEditorialAddress() {
		return getString("editorial_url_address");
	}

	public void EditorialGeoocation(ParseGeoPoint value) {
		put("editorial_location", value);
	}

	public ParseGeoPoint getEditorialGeolocation() {
		return getParseGeoPoint("editorial_location");
	}

	public void setPublishedStatus(Boolean value) {
		put("publish_status", value);
	}

	public Boolean isPublished() {
		return getBoolean("publish_status");
	}

	public List<ParseUser> getEditorialCollaborators() {
		return getList("editorial_collaborators");
	}

	public void setEditorialCollaborators(List<ParseUser> value) {
		put("editorial_collaborators", value);
	}

	public void setEditorialCoverPhoto(ParseFile piece) {
		put("cover_photo", piece);
	}

	public ParseFile getEditorialCoverPhoto() {
		return getParseFile("cover_photo");
	}

	public void setEditorialCount() {
		// TODO Auto-generated method stub
		increment("editorial_count");
	}

}
