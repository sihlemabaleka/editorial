package com.android.editorial.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.android.editorial.R;
import com.android.editorial.data.Category;

public class CategoryDialog extends DialogFragment {

	List<String> categoryNames = new ArrayList<>();
	List<Category> mCategory;
	List<String> mCategoryNames = new ArrayList<String>();
	
	public interface categoryDialogListener{
		public void onCategoryClick(int which);
	}
	categoryDialogListener mListner;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try{
			mListner = (categoryDialogListener) getTargetFragment();
		}catch(ClassCastException e){
			throw new ClassCastException(activity.toString() + " must implaement categoryDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		final AlertDialog.Builder dialog = new AlertDialog.Builder(
				getActivity());

		dialog.setItems(R.array.category, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mListner.onCategoryClick(which);
			}
		});

		return dialog.create();

	}

}
