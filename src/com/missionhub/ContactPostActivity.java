package com.missionhub;

import java.util.ArrayList;
import java.util.Iterator;

import com.missionhub.api.ApiNotifierHandler;
import com.missionhub.api.FollowupComments;
import com.missionhub.api.ApiNotifier.Type;
import com.missionhub.api.model.sql.Person;
import com.missionhub.api.model.sql.PersonDao;
import com.missionhub.helper.Helper;
import com.missionhub.helper.U;
import com.missionhub.ui.DisplayError;
import com.missionhub.ui.Rejoicable;
import com.missionhub.ui.RejoicableAdapter;

import greendroid.widget.ActionBar;
import greendroid.widget.ActionBarItem;
import greendroid.widget.NormalActionBarItem;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class ContactPostActivity extends Activity {

	public static final String TAG = ContactPostActivity.class.getSimpleName();

	private static final int DIALOG_REJOICABLES = 0;

	private int personId = -1;
	private String status;
	private Spinner mSpinner;
	private Button mRejoicable;
	private EditText mComment;

	private ListView rejoicableListView;
	ArrayList<Rejoicable> selectedRejoicables = new ArrayList<Rejoicable>();

	private final String tag = ContactPostActivity.this.toString();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		personId = getIntent().getIntExtra("personId", -1);
		status = getIntent().getStringExtra("status");
		if (personId < 0 || U.nullOrEmpty(status)) {
			Toast.makeText(getApplicationContext(), R.string.contact_post_invalid_person, Toast.LENGTH_LONG).show();
			finish();
		}

		this.setTitle("Add Comment / Update Status"); //TODO:

		setActionBarContentView(R.layout.activity_contact_post);
		getActionBar().setType(ActionBar.Type.Empty);

		addActionBarItem(getActionBar().newActionBarItem(NormalActionBarItem.class).setDrawable(R.drawable.action_bar_save).setContentDescription(R.string.action_save),
				R.id.action_bar_save);

		mComment = (EditText) findViewById(R.id.comment);

		setupSpinner();
		setupRejoicables();

		getApiNotifier().subscribe(this, postHandler, Type.JSON_FOLLOWUP_COMMENTS_POST_ON_START, Type.JSON_FOLLOWUP_COMMENTS_POST_ON_FINISH,
				Type.JSON_FOLLOWUP_COMMENTS_POST_ON_FAILURE, Type.JSON_FOLLOWUP_COMMENTS_POST_ON_SUCCESS);
	}

	public void setupSpinner() {
		mSpinner = (Spinner) findViewById(R.id.status);

		ArrayList<String> statusList = new ArrayList<String>();
		Iterator<String> itr = Helper.statusList.iterator();

		int defaultStatus = 0;
		int i = 0;
		while (itr.hasNext()) {
			final String s = itr.next();
			statusList.add(getString(Helper.statusMap.get(s)));
			if (status.equalsIgnoreCase(s)) {
				defaultStatus = i;
			}
			i++;
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statusList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(adapter);
		mSpinner.setSelection(defaultStatus);
	}

	public void setupRejoicables() {
		mRejoicable = (Button) findViewById(R.id.rejoicable);
		mRejoicable.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ContactPostActivity.this.showDialog(DIALOG_REJOICABLES);
			}
		});
	}

	@Override
	public void finish() {
		this.setResult(RESULT_CANCELED);
		super.finish();
	}

	public void finishOK() {
		this.setResult(RESULT_OK);
		super.finish();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_REJOICABLES:
			final ArrayList<Rejoicable> validRejoicables = new ArrayList<Rejoicable>();
			Iterator<Rejoicable> itr = Helper.rejoicableMap.values().iterator();
			while (itr.hasNext()) {
				validRejoicables.add(itr.next());
			}
			AlertDialog dialog = new AlertDialog.Builder(this).setIcon(R.drawable.rejoicable_icon).setTitle(R.string.contact_post_attach_rejoicables)
					.setAdapter(new RejoicableAdapter(this, android.R.layout.simple_spinner_dropdown_item, validRejoicables), null).setNeutralButton(R.string.action_ok, null)
					.create();
			rejoicableListView = dialog.getListView();
			rejoicableListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

			dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					selectedRejoicables.clear();
					if (rejoicableListView != null) {
						long[] checkedRejoicables = rejoicableListView.getCheckItemIds();
						for (long pos : checkedRejoicables) {
							selectedRejoicables.add(validRejoicables.get((int) pos));
						}
					}
				}
			});

			return dialog;
		}
		return null;
	}

	@Override
	public boolean onHandleActionBarItemClick(ActionBarItem item, int position) {
		switch (item.getItemId()) {
		case R.id.action_bar_save:
			clickSave();
			break;
		default:
			return super.onHandleActionBarItemClick(item, position);
		}
		return true;
	}

	private void clickSave() {
		if (hasProgress(tag))
			return;

		boolean canSave = false;

		String commentStr = "";
		if (mComment.getText() != null) {
			commentStr = mComment.getText().toString();
		}
		if (!commentStr.equals("")) {
			canSave = true;
		}

		if (selectedRejoicables.size() > 0) {
			canSave = true;
		}

		mSpinner.getSelectedItemPosition();

		final String stat = Helper.statusList.get(mSpinner.getSelectedItemPosition());
		if (!stat.equals(status)) {
			canSave = true;
		}

		if (canSave) {
			showProgress(tag);
			status = stat;
			FollowupComments.Comment comment = new FollowupComments.Comment(personId, getUser().getId(), getUser().getOrganizationID(), status, commentStr, selectedRejoicables);
			FollowupComments.post(this, comment, tag);
		} else {
			Toast.makeText(this, R.string.contact_post_cant_save, Toast.LENGTH_LONG).show();
		}
	}

	private Handler postHandler = new ApiNotifierHandler(tag) {
		@Override
		public void handleMessage(Type type, String tag, Bundle bundle, Throwable t, long rowId) {
			switch (type) {
			case JSON_FOLLOWUP_COMMENTS_POST_ON_FINISH:
				hideProgress(tag);
				break;
			case JSON_FOLLOWUP_COMMENTS_POST_ON_FAILURE:
				DisplayError.displayWithRetry(ContactPostActivity.this, t, new DisplayError.Retry() {
					@Override
					public void run() {
						clickSave();
					}
				}).show();
				break;
			case JSON_FOLLOWUP_COMMENTS_POST_ON_SUCCESS:
				PersonDao pd = ContactPostActivity.this.getApp().getDbSession().getPersonDao();
				Person p = pd.load(personId);
				if (p != null) {
					p.setStatus(status);
					pd.update(p);
				}
				finishOK();
				break;
			}
		}
	};
}