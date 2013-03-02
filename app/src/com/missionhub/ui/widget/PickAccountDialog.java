package com.missionhub.ui.widget;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.application.SettingsManager;
import com.missionhub.authenticator.Authenticator;
import com.missionhub.fragment.dialog.BaseDialogFragment;
import com.missionhub.model.Organization;
import com.missionhub.model.Person;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.util.U;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

public class PickAccountDialog extends BaseDialogFragment implements OnItemClickListener, OnAccountsUpdateListener {

    private AccountManager mAccountManager;
    private ListView mListView;
    private ObjectArrayAdapter mAdapter;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        mAdapter = new PickAccountArrayAdapter(getSupportActivity());

        final View view = LayoutInflater.from(getSupportActivity()).inflate(R.layout.dialog_pick_account, null);

        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActivity());
        builder.setTitle(getString(R.string.init_choose_account));
        builder.setView(view);
        return builder.create();
    }

    private class PickAccountArrayAdapter extends ObjectArrayAdapter {

        public PickAccountArrayAdapter(final Context context) {
            super(context, 2);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final Object item = getItem(position);
            View view = convertView;

            ViewHolder holder;
            if (view == null) {
                final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                holder = new ViewHolder();
                if (item instanceof AccountItem) {
                    view = inflater.inflate(R.layout.item_account, null);
                    holder.name = (TextView) view.findViewById(android.R.id.text1);
                    holder.organization = (TextView) view.findViewById(android.R.id.text2);
                    view.setTag(holder);
                } else if (item instanceof NewAccountItem) {
                    view = inflater.inflate(R.layout.item_account_new, null);
                }
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (item instanceof AccountItem) {
                final AccountItem aitem = (AccountItem) item;
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
        public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        public class ViewHolder {
            TextView name;
            TextView organization;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        mAccountManager = AccountManager.get(getSupportActivity());
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
    public void onAccountsUpdated(final Account[] accounts) {
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

        public AccountItem(final Person person) {
            this.person = person;
        }
    }

    private static class NewAccountItem {
    }

    public static class AccountPickedEvent {
        public long personId = -1;

        public AccountPickedEvent(final long personId) {
            this.personId = personId;
        }
    }
}