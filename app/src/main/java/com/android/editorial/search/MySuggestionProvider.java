package com.android.editorial.search;

import android.content.SearchRecentSuggestionsProvider;

public class MySuggestionProvider extends SearchRecentSuggestionsProvider {

	public final static String AUTHORITY = "com.android.editorial.search.MySuggestionProvider";
	public final static int MODE = DATABASE_MODE_QUERIES;
	
	public MySuggestionProvider() {
		setupSuggestions(AUTHORITY, MODE);
	}
	
	
			
}