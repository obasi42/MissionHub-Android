package com.missionhub.ui.widget;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
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

public class PickAccountDialog extends DialogFragment implements OnItemClickListener, OnAccountsUpdateListener {

	private AccountManager mAccountManager;
	private ListView mListView;
	private ItemAdapter mAdapter;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    mAdapter = new ItemAdapter(getActivity());
	    
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    View view = inflater.inflate(R.layout.dialog_pick_account, null);
	    
	    mListView = (ListView) view.findViewById(R.id.listview);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	    
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Choose account...");
	    builder.setView(view); 
	    return builder.create();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		mAccountManager = AccountManager.get(getActivity());
		mAccountManager.addOnAccountsUpdatedListener(this, null, false);
		
		refreshAccounts();
	}
	
	@Override
	public void onPause() {
		mAccountManager.removeOnAccountsUpdatedListener(this);
		
		super.onPause();
	}
	
	private void refreshAccounts() {
		mAdapter.setNotifyOnChange(false);
		mAdapter.clear();
		
		final Account[] accounts = mAccountManager.getAccountsByType(Authenticator.ACCOUNT_TYPE);
		for (final Account account : accounts) {
			final Long personId = Long.parseLong(mAccountManager.getUserData(account, Authenticator.KEY_PERSON_ID));
			final Person person = Application.getDb().getPersonDao().load(personId);
			if (!U.isNull(account, person)) {
				mAdapter.add(new AccountItem(account, person));
			}
		}
		mAdapter.add(new AccountItem.NewAccountItem());
		
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onAccountsUpdated(Account[] accounts) {
		refreshAccounts();
	}
	
	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		final Item item = (Item) mAdapter.getItem(position);
		if (item instanceof AccountItem) {
			Application.postEvent(new AccountPickedEvent(((AccountItem) item).person.getId()));
		} else if (item instanceof AccountItem.NewAccountItem) {
			Application.postEvent(new AccountPickedEvent(-1));
		}
		dismiss();
	}

	public static class AccountPickedEvent {
		public long personId = -1;

		public AccountPickedEvent(final long personId) {
			this.personId = personId;
		}
	}
}