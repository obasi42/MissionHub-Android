package com.missionhub.fragment.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.ApiOptions;
import com.missionhub.api.PeopleListOptions;
import com.missionhub.application.Application;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.model.Person;
import com.missionhub.util.SafeAsyncTask;

import org.apache.commons.lang3.ArrayUtils;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class DeletePeopleDialogFragment extends BaseDialogFragment {

    private HashSet mPeopleIds;
    private PeopleListOptions mFilters;
    private TextView mMessage;
    private ProgressBar mProgress;
    private AlertDialog mDialog;
    private SafeAsyncTask<Void> mTask;

    public static void showForResult(FragmentManager fragmentManager, long[] checkedItemIds, int action_delete) {
        showForResult(fragmentManager, Arrays.asList(ArrayUtils.toObject(checkedItemIds)), action_delete);
    }

    public static void showForResult(FragmentManager fm, Collection<Long> peopleIds, int requestCode) {
        Bundle args = new Bundle();
        args.putSerializable("peopleIds", new HashSet<Long>(peopleIds));
        showForResult(DeletePeopleDialogFragment.class, fm, args, requestCode);
    }

    public static void showForResult(FragmentManager fm, PeopleListOptions filters, int requestCode) {
        Bundle args = new Bundle();
        args.putSerializable("filters", filters);
        showForResult(DeletePeopleDialogFragment.class, fm, args, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPeopleIds = (HashSet) getArguments().getSerializable("peopleIds");
            mFilters = (PeopleListOptions) getArguments().getSerializable("filters");
        }

        checkPeople();
        ensureData();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_fragment_basic_action);
        mMessage = (TextView) view.findViewById(R.id.message);
        mProgress = (ProgressBar) view.findViewById(R.id.progress);

        AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActivity());
        builder.setButtonBehavior(0);
        builder.setTitle(R.string.delete_people_dialog_title);
        builder.setIcon(R.drawable.ic_action_delete);
        builder.setView(view);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                doDelete();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cancel();
            }
        });

        mDialog = builder.create();
        return mDialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        mDialog = (AlertDialog) getDialog();
        updateView();
    }

    private void ensureData() {
        if ((mPeopleIds == null || mPeopleIds.isEmpty()) && mFilters == null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Application.showToast("No People Selected For Deletion", Toast.LENGTH_LONG);
                    dismiss();
                }
            });
        }
    }

    private void checkPeople() {
        if (mPeopleIds != null) {
            List<Long> invalidIds = new ArrayList<Long>();
            Iterator itr = mPeopleIds.iterator();
            while (itr.hasNext()) {
                Long id = (Long) itr.next();
                Person person = Application.getDb().getPersonDao().load(id);
                if (person == null || person.isAdmin()) {
                    invalidIds.add(id);
                }
            }
            mPeopleIds.removeAll(invalidIds);
        }
    }

    private void doDelete() {
        if (mTask != null) return;

        mTask = new SafeAsyncTask<Void>() {

            @Override
            public Void call() throws Exception {
                if (mFilters != null) {
                    mPeopleIds = new HashSet<Long>();
                    mFilters.removeOffset();
                    mFilters.removeLimit();

                    List<Person> people = Api.listPeople(mFilters, ApiOptions.builder().include(Api.Include.organizational_permission).build()).get();
                    for (Person person : people) {
                        if (!person.isAdmin()) {
                            mPeopleIds.add(person.getId());
                        }
                    }
                }
                ensureData();

                for (Long mPeopleId : (Iterable<Long>) mPeopleIds) {
                    Api.deletePerson(mPeopleId).get();
                }
                return null;
            }

            @Override
            protected void onSuccess(Void aVoid) throws Exception {
                Application.showToast(R.string.delete_people_dialog_deleted, Toast.LENGTH_SHORT);
                setResult(RESULT_OK, mPeopleIds);
                dismiss();
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
                eh.makeToast();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                mTask = null;
                updateView();
            }
        };
        updateView();
        Application.getExecutor().execute(mTask.future());
    }

    private void updateView() {
        if (mTask == null) {
            mMessage.setText(R.string.delete_people_dialog_confirm);
            mProgress.setVisibility(View.GONE);
            if (mDialog != null && mDialog.getButton(DialogInterface.BUTTON_POSITIVE) != null) {
                mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(true);
            }
        } else {
            mMessage.setText(R.string.delete_people_dialog_deleting);
            mProgress.setVisibility(View.VISIBLE);
            if (mDialog != null && mDialog.getButton(DialogInterface.BUTTON_POSITIVE) != null) {
                mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(false);
            }
        }
    }
}
