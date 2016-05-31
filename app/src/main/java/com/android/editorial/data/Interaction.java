package com.android.editorial.data;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("activity")
public class Interaction extends ParseObject {

	public void setFromUser(ParseUser user) {
		put("fromUser", user);
	}

	public ParseUser getFromUser() {

		try {
			return getParseUser("fromUser").fetchIfNeeded();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setToUser(ParseUser user) {
		put("toUser", user);
	}

	public ParseUser getToUser() {
		try {
			return getParseUser("toUser").fetchIfNeeded();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setType(String type) {
		put("type", type);
	}

	public String getType() {
		return getString("type");
	}

	public Editorial getEditorial() {
		try {
			return getParseObject("editorial_image").fetchIfNeeded();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setEditorial(Editorial value) {
		put("editorial_image", value);
	}


}
