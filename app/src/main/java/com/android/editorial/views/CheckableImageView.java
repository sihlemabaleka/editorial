package com.android.editorial.views;

import android.content.Context;
import android.widget.Checkable;
import android.widget.ImageView;

import com.android.editorial.R;

public class CheckableImageView extends ImageView implements Checkable {

	private Boolean mChecked = false;

	public CheckableImageView(Context context) {
		super(context);
	}

	@Override
	public void setChecked(boolean checked) {
		// TODO Auto-generated method stub
		mChecked = checked;
		if (mChecked) {
			setBackgroundColor(getResources().getColor(R.color.ink));
		} else {
			setBackgroundColor(getResources().getColor(
					android.R.color.transparent));
		}
	}

	@Override
	public boolean isChecked() {
		// TODO Auto-generated method stub
		return mChecked;
	}

	@Override
	public void toggle() {
		// TODO Auto-generated method stub
		setChecked(mChecked ? false : true);
	}

}
