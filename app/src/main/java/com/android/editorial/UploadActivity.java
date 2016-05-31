package com.android.editorial;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.android.editorial.create.CreateEditorial;

public class UploadActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.container);
		if (savedInstanceState == null)
			getFragmentManager().beginTransaction()
					.add(R.id.container, new CreateEditorial()).commit();
	}

}
