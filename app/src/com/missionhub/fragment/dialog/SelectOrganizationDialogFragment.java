package com.missionhub.fragment.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.model.Organization;
import com.missionhub.ui.drilldown.DrillDownAdapter;
import com.missionhub.ui.drilldown.DrillDownItem;
import com.missionhub.ui.drilldown.DrillDownView;
import com.missionhub.util.SafeAsyncTask;
import com.missionhub.util.TreeDataStructure;
import org.holoeverywhere.app.AlertDialog;

public class SelectOrganizationDialogFragment extends RefreshableDialogFragment {

    private DrillDownView mView;
    private DrillDownAdapter mAdapter;

    private SafeAsyncTask<Void> mTask;
    private OrganizationDrillDownItem mCurrentItem;
    private OrganizationDrillDownItem mPrimaryItem;

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

        return builder;
    }

    private synchronized void rebuildAdapter() {
        mAdapter.setNotifyOnChange(false);
        mAdapter.clear();

        mCurrentItem = null;
        mPrimaryItem = null;

        try {
            rebuildAdapterR(Session.getInstance().getPerson().getOrganizationHierarchy(), null);
        } catch (final Session.NoPersonException e) {
            /** shouldn't be possible to get here */
        }

        if (mCurrentItem == null && mPrimaryItem != null) {
            mCurrentItem = mPrimaryItem;
        }
        if (mCurrentItem != null) {
            // move one up so we can see the selected item
            mCurrentItem.getParent();
        }
        mAdapter.setCurrentItem(mCurrentItem.getParent());
    }

    private synchronized void rebuildAdapterR(final TreeDataStructure<Long> tree, final OrganizationDrillDownItem parent) throws Session.NoPersonException {
        for (final TreeDataStructure<Long> subTree : tree.getSubTrees()) {
            final long organizationId = subTree.getHead();
            final Organization org = Application.getDb().getOrganizationDao().load(organizationId);
            if (org != null) {
                OrganizationDrillDownItem item = new OrganizationDrillDownItem(org.getId(), org.getName(), parent);
                if (parent == null) {
                    mAdapter.addRootItem(item);
                }
                if (org.getId() == Session.getInstance().getOrganizationId()) {
                    mPrimaryItem = item;
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

    public static class OrganizationDrillDownAdapter extends DrillDownAdapter {

        public OrganizationDrillDownAdapter(Context context) {
            super(context);
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