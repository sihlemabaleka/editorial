package com.android.editorial;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class OptionsDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Dialog d = super.onCreateDialog(savedInstanceState);
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.setContentView(R.layout.com_editorial_ui_options_menu);

		Window window = d.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();

		wlp.gravity = Gravity.CENTER;
		wlp.copyFrom(d.getWindow().getAttributes());
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(wlp);

		return d;
	}

}
