package com.missionhub.ui.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import greendroid.widget.ItemAdapter;
import greendroid.widget.item.Item;
import greendroid.widget.item.ProgressItem;

import com.missionhub.ContactActivity;
import com.missionhub.R;
import com.missionhub.api.ApiNotifierHandler;
import com.missionhub.api.FollowupComments;
import com.missionhub.api.ApiNotifier.Type;
import com.missionhub.api.model.sql.FollowupComment;
import com.missionhub.api.model.sql.FollowupCommentDao;
import com.missionhub.api.model.sql.FollowupCommentDao.Properties;
import com.missionhub.api.model.sql.Person;
import com.missionhub.ui.ContactHeaderFragment;
import com.missionhub.ui.DisplayError;
import com.missionhub.ui.widget.item.CenteredTextItem;
import com.missionhub.ui.widget.item.ContactStatusItem;

import de.greenrobot.dao.QueryBuilder;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class ContactStatusTab extends LinearLayout {

	private static final String TAG = ContactStatusTab.class.getSimpleName();

	private ContactActivity activity;

	private ContactHeaderFragment mHeader;

	private ListView mListView;
	private ItemAdapter mListAdapter;

	private ProgressItem progressItem;
	private CenteredTextItem noStatusItem;

	private Person person;

	private String deleteTag = this.toString() + "delete";

	public ContactStatusTab(Context context) {
		super(context);
		activity = (ContactActivity) context;
		setup();
	}

	public ContactStatusTab(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (ContactActivity) context;
		setup();
	}

	public void setup() {
		LayoutInflater.from(activity).inflate(R.layout.tab_contact_status, this);

		mListView = (ListView) ((LinearLayout) findViewById(R.id.tab_contact_status)).findViewById(R.id.listview);

		final LinearLayout header = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.tab_contact_status_header, null);
		mListView.addHeaderView(header, null, false);

		mHeader = (ContactHeaderFragment) activity.getSupportFragmentManager().findFragmentById(R.id.fragment_contact_status_header);

		progressItem = new ProgressItem(activity.getString(R.string.progress_loading), true);
		noStatusItem = new CenteredTextItem(activity.getString(R.string.contact_tab_status_no_comments));

		mListAdapter = new ItemAdapter(activity);
		mListAdapter.add(progressItem);
		mListView.setAdapter(mListAdapter);

		mListView.setOnItemLongClickListener(itemLongClickListener);

		activity.getApp()
				.getApiNotifier()
				.subscribe(this, notifierHandler, Type.JSON_FOLLOWUP_COMMENTS_DELETE_ON_START, Type.JSON_FOLLOWUP_COMMENTS_DELETE_ON_FINISH,
						Type.JSON_FOLLOWUP_COMMENTS_DELETE_ON_SUCCESS, Type.JSON_FOLLOWUP_COMMENTS_DELETE_ON_FAILURE);
	}

	public void setPerson(Person person) {
		this.person = person;
		mHeader.setPerson(person);
	}

	public void update(final boolean initial) {
		if (person == null)
			return;

		activity.showProgress(this.toString());

		new Thread(new Runnable() {
			@Override
			public void run() {
				ListItems li = new ListItems();

				FollowupCommentDao fcd = activity.getApp().getDbSession().getFollowupCommentDao();
				QueryBuilder<FollowupComment> qb = fcd.queryBuilder();
				List<FollowupComment> comments = qb.where(Properties.Contact_id.eq(person.get_id()), Properties.Deleted_at.isNull()).orderDesc(Properties.Updated_at).list();

				Iterator<FollowupComment> itr = comments.iterator();
				while (itr.hasNext()) {
					final FollowupComment comment = itr.next();
					li.items.add(new ContactStatusItem(comment, comment.getRejoicables()));
				}

				if (!initial) {
					if (li.items.isEmpty()) {
						li.items.add(noStatusItem);
					}
				}

				final Message msg = updateHandler.obtainMessage();

				if (initial)
					msg.what = 0;
				else
					msg.what = 1;
				msg.obj = li;
				updateHandler.sendMessage(msg);
			}
		}).start();
	}

	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			ListItems li = (ListItems) msg.obj;
			Iterator<Item> itr = li.items.iterator();
			mListAdapter.setNotifyOnChange(false);
			mListAdapter.clear();
			if (msg.what == 0) {
				mListAdapter.add(progressItem);
			}
			while (itr.hasNext()) {
				mListAdapter.add(itr.next());
			}
			mListAdapter.notifyDataSetChanged();
			activity.hideProgress(ContactStatusTab.this.toString());
		}
	};

	public void setUpdating() {
		mListAdapter.setNotifyOnChange(false);
		mListAdapter.remove(noStatusItem);
		mListAdapter.insert(progressItem, 0);
		mListAdapter.notifyDataSetChanged();
	}

	private class ListItems {
		public ArrayList<Item> items = new ArrayList<Item>();
	}

	private HashMap<String, Integer> deleteRequests = new HashMap<String, Integer>();

	private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			try {
				final ContactStatusItem item = (ContactStatusItem) mListAdapter.getItem(position - 1);
				final FollowupComment comment = item.comment;

				boolean canDelete = false;
				if (activity.getUser().hasRole("admin")) {
					canDelete = true;
				} else if (activity.getUser().hasRole("leader")) {
					if (comment.getCommenter_id() == activity.getUser().getId()) {
						canDelete = true;
					}
				}

				if (canDelete) {
					final CharSequence[] items = { activity.getString(R.string.action_delete) };
					AlertDialog.Builder builder = new AlertDialog.Builder(activity);
					builder.setIcon(R.drawable.ic_menu_start_conversation);
					builder.setTitle(R.string.contact_tab_status_comment_actions);
					builder.setItems(items, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int item) {
							if (item == 0) {
								FollowupComments.delete(activity, comment.get_id(), deleteTag + comment.get_id());
								deleteRequests.put(deleteTag + comment.get_id(), comment.get_id());
							}
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				} else {
					Toast.makeText(activity, R.string.contact_tab_status_no_permissions, Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				Log.w(TAG, e.getMessage(), e);
			}
			return false;
		}
	};

	private ApiNotifierHandler notifierHandler = new ApiNotifierHandler() {
		@Override
		public void handleMessage(Type type, final String tag, Bundle bundle, Throwable t, long rowId) {
			switch (type) {
			case JSON_FOLLOWUP_COMMENTS_DELETE_ON_START:
				activity.showProgress(deleteTag);
				break;
			case JSON_FOLLOWUP_COMMENTS_DELETE_ON_FINISH:
				activity.hideProgress(deleteTag);
				break;
			case JSON_FOLLOWUP_COMMENTS_DELETE_ON_SUCCESS:
				activity.updateStatus(true);
				break;
			case JSON_FOLLOWUP_COMMENTS_DELETE_ON_FAILURE:
				DisplayError.displayWithRetry(activity, t, new DisplayError.Retry() {
					@Override
					public void run() {
						if (tag != null && deleteRequests.containsKey(tag)) {
							int commentId = deleteRequests.get(tag);
							FollowupComments.delete(activity, commentId, deleteTag);
						}
					}
				}).show();
				break;
			}
		}
	};
}