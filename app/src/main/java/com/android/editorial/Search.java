package com.android.editorial;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Window;
import android.widget.SearchView;

import com.android.editorial.search.MySuggestionProvider;
import com.android.editorial.search.SearchHolder;
import com.android.editorial.search.SearchingFragment;

public class Search extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.com_editorial_ui_search_fragment);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.search_container, new SearchHolder()).commit();
		}

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) findViewById(R.id.searchView1);
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(false);
		searchView.setSubmitButtonEnabled(true);

		handleIntent(getIntent());

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		// TODO Auto-generated method stub
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			System.out.println(intent.getStringExtra(SearchManager.QUERY));
			String query = intent.getStringExtra(SearchManager.QUERY);
			getFragmentManager()
					.beginTransaction()
					.replace(
							R.id.search_container,
							SearchingFragment.newInstance(intent
									.getStringExtra(SearchManager.QUERY)))
					.commit();
			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
					this, MySuggestionProvider.AUTHORITY,
					MySuggestionProvider.MODE);
			suggestions.saveRecentQuery(query, null);

		}
	}

}
