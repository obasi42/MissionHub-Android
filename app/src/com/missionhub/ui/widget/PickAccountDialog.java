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
import com.missionhub.application.Session;
import com.missionhub.application.SettingsManager;
import com.missionhub.authenticator.Authenticator;
import com.missionhub.fragment.dialog.BaseDialogFragment;
import com.missionhub.model.Organization;
import com.missionhub.model.Person;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.util.ObjectUtils;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

public class PickAccountDialog extends BaseDialogFragment implements OnItemClickListener, OnAccountsUpdateListener {

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
        builder.setTitle(getString(R.string.session_choose_account));
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
                if (aitem.account != null) {
                    holder.name.setText(aitem.account.name);

                    long personId = Session.getInstance().getAccountPersonId(aitem.account);
                    final Organization org = Application.getDb().getOrganizationDao().load(SettingsManager.getSessionOrganizationId(personId));
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

        Session.getInstance().getAccountManager().addOnAccountsUpdatedListener(this, null, false);

        refreshAccounts();
    }

    @Override
    public void onPause() {
        Session.getInstance().getAccountManager().removeOnAccountsUpdatedListener(this);

        super.onPause();
    }

    private void refreshAccounts() {
        mAdapter.setNotifyOnChange(false);
        mAdapter.clear();

        final Account[] accounts = Session.getInstance().getAllAccounts();
        for (final Account account : accounts) {
            mAdapter.add(new AccountItem(account));
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
            Application.postEvent(new AccountPickedEvent(((AccountItem) item).account));
        } else if (item instanceof NewAccountItem) {
            Application.postEvent(new AccountPickedEvent(null));
        }
        dismiss();
    }

    private static class AccountItem {
        public final Account account;

        public AccountItem(final Account account) {
            this.account = account;
        }
    }

    private static class NewAccountItem {
    }

    public static class AccountPickedEvent {
        public final Account account;

        public AccountPickedEvent(final Account account) {
            this.account = account;
        }
    }
}