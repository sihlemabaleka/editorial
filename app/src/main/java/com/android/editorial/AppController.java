package com.android.editorial;

import android.app.Application;

import com.android.editorial.data.Category;
import com.android.editorial.data.Comment;
import com.android.editorial.data.Editorial;
import com.android.editorial.data.Interaction;
import com.android.editorial.data.Piece;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;

public class AppController extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		ParseObject.registerSubclass(Category.class);
		ParseObject.registerSubclass(Editorial.class);
		ParseObject.registerSubclass(Piece.class);
		ParseObject.registerSubclass(Interaction.class);
		ParseObject.registerSubclass(Comment.class);

		Parse.initialize(new Parse.Configuration.Builder(this)
				.applicationId("HgYoX4cEmfApkwE374pxEt5tAiPN9XEHeBvV3403")
				.server("https://apprise-server.appspot.com/parse/")
				.enableLocalDataStore()
				.build()
		);

		//ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		// Optionally enable public read access.
		defaultACL.setPublicReadAccess(true);
		defaultACL.setPublicWriteAccess(true);
		ParseACL.setDefaultACL(defaultACL, true);
	}
}
