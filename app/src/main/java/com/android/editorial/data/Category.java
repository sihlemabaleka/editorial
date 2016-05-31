package com.android.editorial.data;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Category")
public class Category extends ParseObject {
	
	public String getCategoryName(){
		return getString("category_name");
	}

}
