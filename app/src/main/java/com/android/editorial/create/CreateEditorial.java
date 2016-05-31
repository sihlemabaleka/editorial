package com.android.editorial.create;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.editorial.R;
import com.android.editorial.dialogs.CategoryDialog;
import com.android.editorial.dialogs.CategoryDialog.categoryDialogListener;

public class CreateEditorial extends Fragment implements
		categoryDialogListener, OnClickListener {

	private Button btnCategory;
	private ImageButton btnCancel, btnSave;
	private EditText eName, eURL;
	private String mCategory = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(
				R.layout.com_editorial_ui_editorial_create_fragment, container,
				false);

		btnCategory = (Button) v.findViewById(R.id.category);
		btnSave = (ImageButton) v.findViewById(R.id.next);
		btnCancel = (ImageButton) v.findViewById(R.id.back);
		eName = (EditText) v.findViewById(R.id.name);
		eURL = (EditText) v.findViewById(R.id.url_address);

		btnCategory.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		return v;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			getActivity().finish();
			break;
		case R.id.category:
			CategoryDialog dialog = new CategoryDialog();
			dialog.setTargetFragment(this, 0);
			dialog.show(getFragmentManager(), getTag());
			break;
		case R.id.next:
			if ((mCategory != null)
					&& (!TextUtils.isEmpty(eName.getText().toString()))) {
				getFragmentManager()
						.beginTransaction()
						.replace(
								R.id.container,
								ImageSelect.newInstance(mCategory, eName
										.getText().toString(), eURL.getText()
										.toString())).addToBackStack("create")
						.commit();
			} else {
				Toast.makeText(getActivity(), "Please select a category",
						Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onCategoryClick(int which) {
		// TODO Auto-generated method stub
		String[] _list = getResources().getStringArray(R.array.category);
		mCategory = _list[which].toString().trim();
		btnCategory.setText(mCategory);
	}

}
