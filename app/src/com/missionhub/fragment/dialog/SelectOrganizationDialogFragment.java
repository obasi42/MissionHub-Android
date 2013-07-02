package com.missionhub.fragment.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.application.SettingsManager;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.model.Organization;
import com.missionhub.model.Person;
import com.missionhub.ui.drilldown.DrillDownAdapter;
import com.missionhub.ui.drilldown.DrillDownItem;
import com.missionhub.ui.drilldown.DrillDownView;
import com.missionhub.util.SafeAsyncTask;
import com.missionhub.util.TreeDataStructure;

import org.apache.commons.lang3.StringUtils;
import org.holoeverywhere.app.AlertDialog;
import org.apache.commons.lang3.ArrayUtils;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import java.util.*;

public class SelectOrganizationDialogFragment extends RefreshableDialogFragment implements DialogInterface.OnKeyListener {

    private DrillDownView mView;
    private DrillDownAdapter mAdapter;

    private SafeAsyncTask<Void> mTask;
    private OrganizationDrillDownItem mCurrentItem;

    public SelectOrganizationDialogFragment() {
    }

    public static SelectOrganizationDialogFragment showForResult(FragmentManager fm, Integer requestCode) {
        return SelectOrganizationDialogFragment.show(SelectOrganizationDialogFragment.class, fm, null, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onRefresh();
    }

    @Override
    public void onCreateDialogTitle(DialogTitle title) {
        title.setTitle(getString(R.string.select_organization_title));
    }

    @Override
    public AlertDialog.Builder onCreateRefreshableDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActivity());
        builder.setNeutralButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancel();
            }
        });
        mView = new DrillDownView(getSupportActivity());
        if (mAdapter == null) {
            mAdapter = new OrganizationDrillDownAdapter(getSupportActivity());
            rebuildAdapter();
        } else {
            mAdapter.setContext(getSupportActivity());
        }
        mView.setAdapter(mAdapter);
        mView.setOnDrillDownItemClickListener(new DrillDownView.OnDrillDownItemClickListener() {
            @Override
            public void onItemClicked(DrillDownAdapter adapter, DrillDownItem item) {
                Session.getInstance().setOrganizationId(item.getId());
                dismiss();
            }
        });
        mView.setOnDrillDownItemLongClickListener(new DrillDownView.OnDrillDownItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(DrillDownAdapter adapter, DrillDownItem item) {
                long[] favorites = getFavorites();
                if (ArrayUtils.contains(favorites, item.getId())) {
                    removeFavoriteOrganization(item.getId());
                    Application.showToast(getString(R.string.select_organization_removed), Toast.LENGTH_SHORT);
                    rebuildAdapter();
                } else {
                    addFavoriteOrganization(item.getId());
                    Application.showToast(getString(R.string.select_organization_added), Toast.LENGTH_SHORT);
                    rebuildAdapter();
                }
                return true;
            }
        });
        builder.setView(mView);
        builder.setCancelable(false);
        builder.setOnKeyListener(this);

        return builder;
    }

    private synchronized void rebuildAdapter() {
        mAdapter.setNotifyOnChange(false);
        mAdapter.clear();

        List<Long> favorites = getPrivilegedFavorites();
        if (favorites.size() > 0) {
            DrillDownItem favoritesHeader = new HeaderDrillDownItem(getString(R.string.select_organization_favorites));
            mAdapter.addRootItem(favoritesHeader);

            for (Long favorite : favorites) {
                Organization org = Application.getDb().getOrganizationDao().load(favorite);
                if (org != null) {
                    mAdapter.addRootItem(new OrganizationDrillDownItem(org.getId(), org.getName(), null));
                }
            }
            DrillDownItem favoritesDivider = new HeaderDrillDownItem(getString(R.string.select_organization_all_organizations));
            mAdapter.addRootItem(favoritesDivider);
        }

        mCurrentItem = null;

        rebuildAdapterR(Session.getInstance().getPerson().getOrganizationHierarchy(), null);

        mAdapter.setCurrentItem(mCurrentItem);
    }

    private List<Long> getPrivilegedFavorites() {
        final List<Long> privilegedFavorites = new LinkedList<Long>();
        final long[] favorites = getFavorites();

        try {
            Person currentPerson = Session.getInstance().getPerson();
            for (Long favorite : favorites) {
                if (currentPerson.isAdminOrUser(favorite)) {
                    privilegedFavorites.add(favorite);
                }
            }
        } catch (Exception e) {
            Log.e("SelectOrg", e.getMessage(), e);
        }
        return privilegedFavorites;
    }

    private long[] getFavorites() {
        return SettingsManager.getInstance().getUserSetting(Session.getInstance().getPersonId(), "favorite_organizations", new long[]{Session.getInstance().getPrimaryOrganizationId()});
    }

    private void addFavoriteOrganization(long organizationId) {
        long[] favorites = getFavorites();
        if (!ArrayUtils.contains(favorites, organizationId)) {
            favorites = ArrayUtils.add(favorites, organizationId);
            SettingsManager.getInstance().setUserSetting(Session.getInstance().getPersonId(), "favorite_organizations", favorites);
        }
    }

    private void removeFavoriteOrganization(long organizationId) {
        long[] favorites = getFavorites();
        if (ArrayUtils.contains(favorites, organizationId)) {
            favorites = ArrayUtils.removeElement(favorites, organizationId);
            SettingsManager.getInstance().setUserSetting(Session.getInstance().getPersonId(), "favorite_organizations", favorites);
        }
    }

    private synchronized void rebuildAdapterR(final TreeDataStructure<Long> tree, final OrganizationDrillDownItem parent) {
        for (final TreeDataStructure<Long> subTree : tree.getSubTrees()) {
            final long organizationId = subTree.getHead();
            final Organization org = Application.getDb().getOrganizationDao().load(organizationId);
            if (org != null) {
                OrganizationDrillDownItem item = new OrganizationDrillDownItem(org.getId(), org.getName(), parent);
                if (parent == null) {
                    mAdapter.addRootItem(item);
                }
                if (mAdapter.getCurrentItem() != null && mAdapter.getCurrentItem().getId() == item.getId()) {
                    mCurrentItem = item;
                }
                rebuildAdapterR(subTree, item);
            } else {
                rebuildAdapterR(subTree, parent);
            }
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (mView != null && keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (!mView.pageBackward(true)) {
                cancel();
            }
            return true;
        }
        return getSupportActivity().dispatchKeyEvent(event);
    }

    public static class OrganizationDrillDownAdapter extends DrillDownAdapter {

        public OrganizationDrillDownAdapter(Context context) {
            super(context);
        }

        public View createItemView(final DrillDownItem item, View convertView) {
            View view = convertView;
            if (item instanceof HeaderDrillDownItem) {
                HeaderViewHolder holder;
                if (view == null) {
                    holder = new HeaderViewHolder();
                    view = getLayoutInflater().inflate(R.layout.item_list_divider, null);
                    holder.text1 = (TextView) view.findViewById(android.R.id.text1);
                    view.setTag(holder);
                } else {
                    holder = (HeaderViewHolder) view.getTag();
                }
                if (StringUtils.isNotEmpty(item.getText())) {
                    holder.text1.setText(item.getText());
                    holder.text1.setVisibility(View.VISIBLE);
                } else {
                    holder.text1.setVisibility(View.GONE);
                }
            } else {
                view = super.createItemView(item, convertView);
            }
            return view;
        }

        public static class HeaderViewHolder {
            public TextView text1;
        }

    }

    public static class OrganizationDrillDownItem extends DrillDownItem {
        private final long mOrganizationId;

        public OrganizationDrillDownItem(long organizationId, CharSequence text, DrillDownItem parent) {
            super(text, parent);
            mOrganizationId = organizationId;
        }

        @Override
        public int getId() {
            return (int) mOrganizationId;
        }
    }

    public static class HeaderDrillDownItem extends DrillDownItem {
        public HeaderDrillDownItem(CharSequence text) {
            super(text);
            setEnabled(false);
        }
    }

    @Override
    public void onRefresh() {
        try {
            mTask.cancel(true);
        } catch (Exception e) {
            /* ignore */
        }

        startRefreshAnimation();

        mTask = new SafeAsyncTask<Void>() {
            @Override
            public Void call() throws Exception {
                Session.getInstance().updatePerson().get();
                return null;
            }

            @Override
            public void onSuccess(final Void _) {
                if (!isDetached()) {
                    rebuildAdapter();
                }
            }

            @Override
            public void onFinally() {
                mTask = null;
                if (!isDetached()) {
                    stopRefreshAnimation();
                }
            }

            @Override
            public void onException(final Exception e) {
                final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
                eh.makeToast(R.string.select_organization_update_failed);
            }

            @Override
            public void onInterrupted(final Exception e) {
            }
        };

        Application.getExecutor().execute(mTask.future());
    }
}