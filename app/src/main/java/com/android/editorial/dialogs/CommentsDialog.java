package com.android.editorial.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.editorial.R;
import com.android.editorial.adapter.CommentAdapter;
import com.android.editorial.data.Comment;
import com.android.editorial.data.Editorial;
import com.android.editorial.helpers.OfflineMethods;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter.QueryFactory;
import com.parse.ParseUser;

public class CommentsDialog extends DialogFragment implements OnClickListener {

	private ListView mListView;
	private Button btnDone, btnSend;
	private EditText eCommentField;
	private OfflineMethods methods;
	private CommentAdapter adapter;
	private Editorial editorial;

	OfflineMethods parseMethods;

	public static CommentsDialog newInstance(String ObjectId) {
		CommentsDialog fragment = new CommentsDialog();
		Bundle args = new Bundle();
		args.putString("objectId", ObjectId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.com_editorial_ui_comments);

		methods = new OfflineMethods(getActivity());

		Window window = dialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();

		wlp.gravity = Gravity.BOTTOM;
		wlp.copyFrom(dialog.getWindow().getAttributes());
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);

		mListView = (ListView) dialog.findViewById(R.id.list);
		btnDone = (Button) dialog.findViewById(R.id.done);
		btnSend = (Button) dialog.findViewById(R.id.send);
		eCommentField = (EditText) dialog.findViewById(R.id.comment);

		eCommentField.addTextChangedListener(mTextWatcher);
		btnSend.setOnClickListener(this);
		btnDone.setOnClickListener(this);

		checkFieldsForEmptyValues();

		editorial = methods.getEditorialLocalDataStore(getArguments()
				.getString("objectId"));

		if (savedInstanceState == null) {
			startRefreshing();
		}

		return dialog;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.send:
			methods.postComment(ParseUser.getCurrentUser(), editorial,
					eCommentField.getText().toString().trim());
			adapter.loadObjects();
			eCommentField.setText("");
			break;
		case R.id.done:
			dismiss();
			break;
		}
	}

	private TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			checkFieldsForEmptyValues();
		}
	};

	protected void checkFieldsForEmptyValues() {
		// TODO Auto-generated method stub
		String text1 = eCommentField.getText().toString().trim();

		if (TextUtils.isEmpty(text1)) {
			btnSend.setEnabled(false);
		} else {
			btnSend.setEnabled(true);
		}
	}

	public void startRefreshing() {
		try {

			adapter = new CommentAdapter(getActivity(),
					new QueryFactory<Comment>() {

						@Override
						public ParseQuery<Comment> create() {
							// TODO Auto-generated method stub
							ParseQuery<Comment> query = ParseQuery
									.getQuery(Comment.class);
							query.whereEqualTo("editorials", editorial);
							query.addAscendingOrder("createdAt");

							return query;
						}
					});

			mListView.setAdapter(adapter);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
