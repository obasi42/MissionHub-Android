package com.missionhub.fragment.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SelectOrganizationDialogFragment extends RefreshableDialogFragment implements DialogInterface.OnKeyListener {

    private DrillDownView mView;
    private DrillDownAdapter mAdapter;

    private SafeAsyncTask<Void> mTask;
    private OrganizationDrillDownItem mCurrentItem;

    public SelectOrganizationDialogFragment() {}

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
        title.setTitle("Select Organization");
    }

    @Override
    public AlertDialog.Builder onCreateRefreshableDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActivity());
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
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
                try {
                    Session.getInstance().setOrganizationId(item.getId());
                } catch (Session.NoPersonException e) {
                    /* ignore */
                }
                dismiss();
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

//        DrillDownItem favoritesHeader = new HeaderDrillDownItem("Favorites");
//        mAdapter.addRootItem(favoritesHeader);
//
//        SettingsManager.getInstance().getUserSetting(get)
//
//
//
//
//        DrillDownItem favoritesDivider = new HeaderDrillDownItem(null);
//        mAdapter.addRootItem(favoritesDivider);

        mCurrentItem = null;

        try {
            rebuildAdapterR(Session.getInstance().getPerson().getOrganizationHierarchy(), null);
        } catch (final Session.NoPersonException e) {
            /** shouldn't be possible to get here */
        }

        mAdapter.setCurrentItem(mCurrentItem);
    }

//    private List<Long> getFavoriteOrganizations() {
//        List<Long> orgs = new ArrayList<Long>();
//        try {
//            long currentPersonId = Session.getInstance().getPersonId();
//            long primaryOrganizationId = Session.getInstance().getPrimaryOrganizationId();
//            Person currentPerson = Session.getInstance().getPerson();
//
//            String orgSetting = SettingsManager.getInstance().getUserSetting(Session.getInstance().getPersonId(), "favorite_organizations", String.valueOf(primaryOrganizationId));
//            String[] orgSettings = orgSetting.split(",");
//
//            for(String org : orgSettings) {
//                long orgId = Long.valueOf(org);
//                if (currentPerson.isAdminOrLeader(orgId)) {
//                    orgs.add(orgId);
//                }
//            }
//        } catch (Exception e) {
//            Log.e("SelectOrg", e.getMessage(), e);
//        }
//        return orgs;
//    }
//
//    private void addFavoriteOrganization(long organization) {
//        String orgSetting = SettingsManager.getInstance().getUserSetting(Session.getInstance().getPersonId(), "favorite_organizations", "");
//        String[] orgSettings = orgSetting.split(",");
//
//        orgSettings.
//
//        //todo
//
//
//    }
//
//    private void removeFavoriteOrganization(long organization) {
//
//        //todo
//
//    }

    private synchronized void rebuildAdapterR(final TreeDataStructure<Long> tree, final OrganizationDrillDownItem parent) throws Session.NoPersonException {
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
            if (item instanceof OrganizationDrillDownItem) {
                return super.createItemView(item, convertView);
            }

            TextView tv = new TextView(getContext());
            tv.setText(item.getText());
            return tv;
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
                eh.makeToast("Updating Organizations Failed");
            }

            @Override
            public void onInterrupted(final Exception e) {}
        };

        Application.getExecutor().execute(mTask.future());
    }
}