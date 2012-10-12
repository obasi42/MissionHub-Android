package com.missionhub.ui.widget;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.authenticator.Authenticator;
import com.missionhub.model.Person;
import com.missionhub.ui.ItemAdapter;
import com.missionhub.ui.item.AccountItem;
import com.missionhub.ui.item.Item;
import com.missionhub.util.U;

public class PickAccountDialog extends Dialog implements OnItemClickListener {

	private final ListView mListView;
	private final ItemAdapter mAdapter;
	private List<Item> mItems;

	public PickAccountDialog(final Context context) {
		super(context);
		setTitle("Choose a MissionHub account");
		setContentView(R.layout.dialog_pick_account);

		mListView = (ListView) findViewById(R.id.listview);

		mAdapter = new ItemAdapter(context);
		if (mItems == null) {
			mItems = new ArrayList<Item>();
			addAccounts();
		}

		for (final Item item : mItems) {
			mAdapter.add(item);
		}

		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	private void addAccounts() {
		mAdapter.setNotifyOnChange(false);

		final AccountManager am = AccountManager.get(Application.getContext());
		final Account[] accounts = am.getAccountsByType(Authenticator.ACCOUNT_TYPE);
		for (final Account account : accounts) {
			final Long personId = Long.parseLong(am.getUserData(account, Authenticator.KEY_PERSON_ID));
			final Person person = Application.getDb().getPersonDao().load(personId);
			if (!U.isNull(account, person)) {
				mItems.add(new AccountItem(account, person));
			}
		}
		mItems.add(new AccountItem.NewAccountItem());

		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		final Item item = (Item) mAdapter.getItem(position);
		if (item instanceof AccountItem) {
			// Application.postEvent(new SessionAccountPickedEvent(((AccountItem) item).person.getId()));
		} else if (item instanceof AccountItem.NewAccountItem) {
			// Application.postEvent(new SessionAccountPickedEvent(-1));
		}
		dismiss();
	}
}