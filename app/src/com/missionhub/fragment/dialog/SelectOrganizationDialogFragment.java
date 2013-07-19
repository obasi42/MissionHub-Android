package com.missionhub.fragment.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
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
import com.missionhub.util.ResourceUtils;
import com.missionhub.util.SafeAsyncTask;
import com.missionhub.util.TaskUtils;
import com.missionhub.util.TreeDataStructure;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.FutureTask;

public class SelectOrganizationDialogFragment extends RefreshableDialogFragment implements DialogInterface.OnKeyListener {

    private DrillDownView mDrilldown;
    private DrillDownAdapter mAdapter;

    private View mAction;
    private TextView mActionText;

    private SafeAsyncTask<Void> mTask;
    private OrganizationDrillDownItem mCurrentItem;
    private SafeAsyncTask<Void> mSetTask;
    private SafeAsyncTask<List<DrillDownItem>> mBuildTask;

    public SelectOrganizationDialogFragment() {
    }

    public static SelectOrganizationDialogFragment showForResult(FragmentManager fm, Integer requestCode) {
        return SelectOrganizationDialogFragment.show(SelectOrganizationDialogFragment.class, fm, null, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
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

        View view = getLayoutInflater().inflate(R.layout.dialog_fragment_select_organization);
        mDrilldown = (DrillDownView) view.findViewById(R.id.drilldown);
        mAction = view.findViewById(R.id.action);
        mActionText = (TextView) view.findViewById(R.id.action_text);
        if (mAdapter == null) {
            mAdapter = new OrganizationDrillDownAdapter(getSupportActivity());
            rebuildAdapter();
        } else {
            mAdapter.setContext(getSupportActivity());
        }
        mDrilldown.setAdapter(mAdapter);
        mDrilldown.setOnDrillDownItemClickListener(new DrillDownView.OnDrillDownItemClickListener() {
            @Override
            public void onItemClicked(DrillDownAdapter adapter, DrillDownItem item) {
                setOrganization(item.getId());
            }
        });
        mDrilldown.setOnDrillDownItemLongClickListener(new DrillDownView.OnDrillDownItemLongClickListener() {
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

        builder.setView(view);
        builder.setCancelable(false);
        builder.setOnKeyListener(this);

        updateUIState();

        return builder;
    }

    private void rebuildAdapter() {
        if (isDetached() || isRemoving()) return;

        try {
            mBuildTask.cancel(true);
        } catch (Exception e) { /* ignore */ }

        mBuildTask = new SafeAsyncTask<List<DrillDownItem>> () {

            @Override
            public List<DrillDownItem> call() throws Exception {
                List<DrillDownItem> roots = new ArrayList<DrillDownItem>();

                List<Long> favorites = getPrivilegedFavorites();
                if (favorites.size() > 0) {
                    DrillDownItem favoritesHeader = new HeaderDrillDownItem(getString(R.string.select_organization_favorites));
                    roots.add(favoritesHeader);

                    for (Long favorite : favorites) {
                        Organization org = Application.getDb().getOrganizationDao().load(favorite);
                        if (org != null) {
                            roots.add(new OrganizationDrillDownItem(org.getId(), org.getName(), null));
                        }
                    }
                    DrillDownItem favoritesDivider = new HeaderDrillDownItem(getString(R.string.select_organization_all_organizations));
                    roots.add(favoritesDivider);
                }

                mCurrentItem = null;

                rebuildAdapterR(roots, Session.getInstance().getPerson().getOrganizationHierarchy(), null);

                return roots;
            }

            @Override
            protected void onSuccess(List<DrillDownItem> roots) throws Exception {
                mAdapter.setNotifyOnChange(false);
                mAdapter.clear();
                mAdapter.addRootItems(roots);
                mAdapter.notifyDataSetChanged();
                mAdapter.setCurrentItem(mCurrentItem);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                mBuildTask = null;
                updateUIState();
            }
        };
        updateUIState();
        mBuildTask.execute();
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
            Application.trackException(Thread.currentThread().getName(), e, false);
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

    private void rebuildAdapterR(List<DrillDownItem> roots, final TreeDataStructure<Long> tree, final OrganizationDrillDownItem parent) {
        for (final TreeDataStructure<Long> subTree : tree.getSubTrees()) {
            final long organizationId = subTree.getHead();
            final Organization org = Application.getDb().getOrganizationDao().load(organizationId);
            if (org != null) {
                OrganizationDrillDownItem item = new OrganizationDrillDownItem(org.getId(), org.getName(), parent);
                if (parent == null) {
                    roots.add(item);
                }
                if (mAdapter.getCurrentItem() != null && mAdapter.getCurrentItem().getId() == item.getId()) {
                    mCurrentItem = item;
                }
                rebuildAdapterR(roots, subTree, item);
            } else {
                rebuildAdapterR(roots, subTree, parent);
            }
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (mDrilldown != null && keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (!mDrilldown.pageBackward(true)) {
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

        mTask = new SafeAsyncTask<Void>() {
            public FutureTask<Person> task;

            @Override
            public Void call() throws Exception {
                task = Session.getInstance().updatePerson(true);
                task.get();
                return null;
            }

            @Override
            public void onSuccess(final Void _) {
                if (isDetached() || isRemoving()) return;

                rebuildAdapter();
            }

            @Override
            public void onFinally() {
                TaskUtils.cancel(task);
                mTask = null;
                if (!isDetached() && !isRemoving()) {
                    stopRefreshAnimation();
                }
            }

            @Override
            public void onException(final Exception e) {
                final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
                eh.makeToast(R.string.select_organization_update_failed);
            }
        };

        startRefreshAnimation();
        Application.getExecutor().execute(mTask.future());
    }

    @Override
    public void onDestroy() {
        TaskUtils.cancel(mBuildTask, mSetTask, mTask);
        super.onDestroy();
    }

    private void setOrganization(final long organizationId) {
        if (mSetTask != null) return;

        if (organizationId == Session.getInstance().getOrganizationId()) {
            Application.showToast(ResourceUtils.getString(R.string.select_organization_already_current), Toast.LENGTH_SHORT);
            return;
        }

        TaskUtils.cancel(mTask);

        mSetTask = new SafeAsyncTask<Void>() {

            public FutureTask<Void> task;

            @Override
            public Void call() throws Exception {
                task = Session.getInstance().updateOrganization(organizationId, true);
                task.get();
                return null;
            }

            @Override
            protected void onSuccess(Void aVoid) throws Exception {
                if (!isCanceled()) {
                    Session.getInstance().setOrganizationId(organizationId, false, false);
                    dismiss();
                }
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                ExceptionHelper eh = new ExceptionHelper(e);
                eh.makeToast();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                if (task != null) {
                    TaskUtils.cancel(task);
                }
                mSetTask = null;
                updateUIState();
            }
        };
        updateUIState();
        mSetTask.execute();
    }

    private void updateUIState() {
        if (mSetTask != null || (mBuildTask != null && (mAdapter == null || mAdapter.isEmpty()))) {
            mDrilldown.setVisibility(View.GONE);
            mAction.setVisibility(View.VISIBLE);
            hideRefresh();

            if (mSetTask != null) {
                mActionText.setText(ResourceUtils.getString(R.string.select_organization_loading_organization));
            } else {
                mActionText.setText(ResourceUtils.getString(R.string.select_organization_loading_organizations));
            }
        } else {
            mDrilldown.setVisibility(View.VISIBLE);
            mAction.setVisibility(View.GONE);
            mActionText.setText(R.string.loading);
            showRefresh();
        }
    }

    @Override
    public void cancel() {
        TaskUtils.cancel(mSetTask);
        super.cancel();
    }
}