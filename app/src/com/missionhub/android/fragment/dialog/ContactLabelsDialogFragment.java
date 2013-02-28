package com.missionhub.android.fragment.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.missionhub.android.R;
import com.missionhub.android.api.Api;
import com.missionhub.android.application.Application;
import com.missionhub.android.exception.ExceptionHelper;
import com.missionhub.android.model.Person;
import com.missionhub.android.ui.ObjectArrayAdapter;
import com.missionhub.android.ui.widget.SelectableListView;
import com.missionhub.android.util.SafeAsyncTask;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class ContactLabelsDialogFragment extends BaseDialogFragment {

    /**
     * the people being acted on
     */
    private HashSet<Person> mPeople;

    /**
     * the task used to save labels
     */
    private SafeAsyncTask<Person> mSaveTask;

    /**
     * the task used to refresh labels
     */
    private SafeAsyncTask<Void> mRefreshTask;

    /**
     * the add contact listener interface
     */
    private WeakReference<LabelDialogListner> mListener;

    /**
     * the list view
     */
    private SelectableListView mList;

    /**
     * the list adapter
     */
    private ObjectArrayAdapter mAdapter;

    /**
     * the progress view
     */
    private View mProgress;

    /**
     * the refresh button
     */
    private ImageView mRefresh;

    /**
     * the refresh animation
     */
    private Animation mRefreshAnimation;

    public ContactLabelsDialogFragment() {
    }

    public static ContactLabelsDialogFragment getInstance(final Person person) {
        final HashSet<Person> people = new HashSet<Person>();
        people.add(person);
        return getInstance(people);
    }

    public static ContactLabelsDialogFragment getInstance(final Set<Person> people) {
        final ContactLabelsDialogFragment dialog = new ContactLabelsDialogFragment();
        final Bundle args = new Bundle();
        final HashSet<Person> copyList = new HashSet<Person>(people);
        args.putSerializable("people", copyList);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            @SuppressWarnings("unchecked") final HashSet<Person> people = (HashSet<Person>) getArguments().getSerializable("people");
            if (people != null) {
                mPeople = new HashSet<Person>(people);
            }
        }
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final View view = getSupportActivity().getLayoutInflater().inflate(R.layout.fragment_labels_dialog, null);
        mProgress = view.findViewById(R.id.progress_container);
        mList = (SelectableListView) view.findViewById(android.R.id.list);

        if (mAdapter == null) {
            mAdapter = new LabelsAdapter(getSupportActivity());
            buildAdapter();
        } else {
            mAdapter.setContext(getSupportActivity());
        }
        mList.setAdapter(mAdapter);

        final View title = getSupportActivity().getLayoutInflater().inflate(R.layout.fragment_refresh_dialog_title, null);
        mRefresh = (ImageView) title.findViewById(R.id.action_refresh);
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLabels();
            }
        });
        mRefreshAnimation = AnimationUtils.loadAnimation(getSupportActivity(), R.anim.clockwise_refresh);
        mRefreshAnimation.setRepeatCount(Animation.INFINITE);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActivity());
        builder.setCustomTitle(title);
        builder.setView(view);
        builder.setPositiveButton("Save", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save();
            }
        });
        builder.setNeutralButton("Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setOnCancelListener(this);

        updateState();

        return builder.create();
    }

    private void buildAdapter() {
        if (mAdapter == null || mPeople == null) return;
        // TODO:
    }

    private void updateState() {
        if (mRefresh != null && mRefreshAnimation != null) {
            if (mRefreshTask != null) {
                mRefresh.setEnabled(false);
                mRefresh.startAnimation(mRefreshAnimation);
            } else {
                mRefresh.setEnabled(true);
                mRefresh.clearAnimation();
            }
        }
        if (mList != null && mProgress != null) {
            if (mSaveTask != null) {
                mProgress.setVisibility(View.VISIBLE);
                mList.setVisibility(View.GONE);
            } else {
                mProgress.setVisibility(View.GONE);
                mList.setVisibility(View.VISIBLE);
            }
        }
    }

    private static class LabelsAdapter extends ObjectArrayAdapter {
        public LabelsAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    public static interface LabelDialogListner {

    }

    private void save() {

    }

    private void refreshLabels() {
        try {
            mRefreshTask.cancel(true);
        } catch (Exception e) {
            /* ignore */
        }

        mRefreshTask = new SafeAsyncTask<Void>() {

            @Override
            public Void call() throws Exception {
                Api.listRoles().get();
                return null;
            }

            @Override
            public void onSuccess(final Void _) {
                buildAdapter();
            }

            @Override
            public void onFinally() {
                mRefreshTask = null;
                updateState();
            }

            @Override
            public void onException(final Exception e) {
                final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
                eh.makeToast("Refresh Failed");
            }

            @Override
            public void onInterrupted(final Exception e) {

            }
        };

        updateState();
        Application.getExecutor().submit(mRefreshTask.future());
    }

    @Override
    public void onDestroy() {
        try {
            mRefreshTask.cancel(true);
        } catch (final Exception e) {
			/* ignore */
        }
        super.onDestroy();
    }
}