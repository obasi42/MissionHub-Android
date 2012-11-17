package com.missionhub.ui.widget;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.application.SettingsManager;
import com.missionhub.authenticator.Authenticator;
import com.missionhub.model.Organization;
import com.missionhub.model.Person;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.util.U;

public class PickAccountDialog extends DialogFragment implements OnItemClickListener, OnAccountsUpdateListener {

	private AccountManager mAccountManager;
	private ListView mListView;
	private ObjectArrayAdapter mAdapter;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    mAdapter = new PickAccountArrayAdapter(getActivity());
	    
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    View view = inflater.inflate(R.layout.dialog_pick_account, null);
	    
	    mListView = (ListView) view.findViewById(R.id.listview);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	    
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.init_choose_account));
	    builder.setView(view); 
	    return builder.create();
	}
	
	private class PickAccountArrayAdapter extends ObjectArrayAdapter {

		public PickAccountArrayAdapter(Context context) {
			super(context, 2);
		}

		@Override
		public View getSupportView(int position, View convertView, ViewGroup parent) {
			final Object item = getItem(position);
			View view = convertView;
			
			ViewHolder holder = null;
			if (view == null) {
				final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				holder = new ViewHolder();
				if (item instanceof AccountItem) {
					view = inflater.inflate(R.layout.item_account, null);
					holder.name = (TextView) view.findViewById(R.id.name);
					holder.organization = (TextView) view.findViewById(R.id.organization);
					view.setTag(holder);
				} else if (item instanceof NewAccountItem){
					view = inflater.inflate(R.layout.item_account_new, null);
				}
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			if (item instanceof AccountItem) {
				AccountItem aitem = (AccountItem) item;
				if (aitem.person != null) {
					holder.name.setText(aitem.person.getName());
					
					final Organization org = Application.getDb().getOrganizationDao().load(SettingsManager.getSessionOrganizationId(aitem.person.getId()));
					if (org != null) {
						holder.organization.setText(org.getName());
						holder.organization.setVisibility(View.VISIBLE);
					} else {
						holder.organization.setVisibility(View.GONE);
					}
				}
			}
			
			return view;
		}

		@Override
		public View getSupportDropDownView(int position, View convertView, ViewGroup parent) {
			return getSupportView(position, convertView, parent);
		}
		
		public class ViewHolder {
			TextView name;
			TextView organization;
		}
		
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
				mAdapter.add(new AccountItem(person));
			}
		}
		mAdapter.add(new NewAccountItem());
		
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onAccountsUpdated(Account[] accounts) {
		refreshAccounts();
	}
	
	@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		final Object item = parent.getItemAtPosition(position);
		if (item instanceof AccountItem) {
			Application.postEvent(new AccountPickedEvent(((AccountItem) item).person.getId()));
		} else if (item instanceof NewAccountItem) {
			Application.postEvent(new AccountPickedEvent(-1));
		}
		dismiss();
	}
	
	private static class AccountItem {
		public final Person person;
		
		public AccountItem(Person person) {
			this.person = person;
		}
	}
	
	private static class NewAccountItem {}

	public static class AccountPickedEvent {
		public long personId = -1;

		public AccountPickedEvent(final long personId) {
			this.personId = personId;
		}
	}
}