package com.missionhub.ui.item;

import android.accounts.Account;
import android.content.Context;
import android.view.ViewGroup;

import com.missionhub.R;
import com.missionhub.model.Person;
import com.missionhub.ui.itemview.ItemView;

public class AccountItem extends Item {

	public Account account;
	public Person person;

	public AccountItem(final Account account, final Person person) {
		this.account = account;
		this.person = person;
	}

	@Override
	public ItemView newView(final Context context, final ViewGroup parent) {
		return createCellFromXml(context, R.layout.item_account, parent);
	}

	public static class NewAccountItem extends Item {

		@Override
		public ItemView newView(final Context context, final ViewGroup parent) {
			return createCellFromXml(context, R.layout.item_account_new, parent);
		}

	}

}