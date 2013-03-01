package com.missionhub.android.fragment.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import com.missionhub.android.R;
import com.missionhub.android.api.Api;
import com.missionhub.android.api.Api.Include;
import com.missionhub.android.api.ApiOptions;
import com.missionhub.android.application.Application;
import com.missionhub.android.exception.ExceptionHelper;
import com.missionhub.android.model.Person;
import com.missionhub.android.model.gson.GAddress;
import com.missionhub.android.model.gson.GEmailAddress;
import com.missionhub.android.model.gson.GPerson;
import com.missionhub.android.model.gson.GPhoneNumber;
import com.missionhub.android.util.SafeAsyncTask;
import com.missionhub.android.util.U;
import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.Toast;

import java.util.Arrays;

public class EditContactDialogFragment extends BaseDialogFragment {

    /**
     * the task used to add a contact
     */
    private SafeAsyncTask<Person> mTask;

    /**
     * contact form view
     */
    private View mForm;

    /**
     * the progress view
     */
    private View mProgress;

    private Button mSaveButton;

    /**
     * if the contact should be assigned to the current user
     */
    private boolean mAssignToMe = false;

    /**
     * the person data holder
     */
    private GPerson mPerson;

    /* the data views */
    private EditText mName;
    private RadioGroup mGender;
    private EditText mPhone;
    private Spinner mPhoneLocation;
    private EditText mEmail;
    private EditText mAddressLine1;
    private EditText mAddressLine2;
    private EditText mAddressCity;
    private Spinner mAddressState;
    private Spinner mAddressCountry;
    private EditText mAddressZip;

    public EditContactDialogFragment() {
    }

    public static EditContactDialogFragment show(Activity activity, FragmentManager fm, final boolean assignToMe) {
        return showForResult(activity, fm, assignToMe, null);
    }

    public static EditContactDialogFragment showForResult(Activity activity, FragmentManager fm, final boolean assignToMe, Integer requestCode) {
        final Bundle args = new Bundle();
        args.putBoolean("assignToMe", assignToMe);
        return EditContactDialogFragment.show(EditContactDialogFragment.class, activity, fm, args, requestCode);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mAssignToMe = getArguments().getBoolean("assignToMe", false);
        }
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final View view = getSupportActivity().getLayoutInflater().inflate(R.layout.fragment_edit_contact_dialog, null);
        mName = (EditText) view.findViewById(R.id.name);
        mGender = (RadioGroup) view.findViewById(R.id.gender);
        mPhone = (EditText) view.findViewById(R.id.phone);
        mPhoneLocation = (Spinner) view.findViewById(R.id.phone_location);
        mEmail = (EditText) view.findViewById(R.id.email);
        mAddressLine1 = (EditText) view.findViewById(R.id.address_line1);
        mAddressLine2 = (EditText) view.findViewById(R.id.address_line2);
        mAddressCity = (EditText) view.findViewById(R.id.address_city);
        mAddressState = (Spinner) view.findViewById(R.id.address_state);
        mAddressCountry = (Spinner) view.findViewById(R.id.address_country);
        mAddressZip = (EditText) view.findViewById(R.id.address_zip);

        final ArrayAdapter<CharSequence> phoneAdapter = ArrayAdapter.createFromResource(getSupportActivity(), R.array.phone_location_titles, R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        phoneAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mPhoneLocation.setAdapter(phoneAdapter);

        final ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(getSupportActivity(), R.array.state_titles, R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mAddressState.setAdapter(stateAdapter);

        final ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter.createFromResource(getSupportActivity(), R.array.country_titles, R.layout.simple_spinner_item);
        countryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mAddressCountry.setAdapter(countryAdapter);

        if (mPerson == null) {
            mPerson = new GPerson();
        } else {
            restoreFromPerson(mPerson);
        }

        mForm = view.findViewById(R.id.form);
        mProgress = view.findViewById(R.id.progress_container);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getSupportActivity());
        builder.setTitle(R.string.action_add_contact);
        builder.setView(view);
        builder.setOnCancelListener(this);

        builder.setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do nothing here
                // the on click is assigned directly to the button to prevent the dialog from dismissing when pressed.
            }
        });
        builder.setNeutralButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        if (mTask != null) {
            showProgress();
        }

        final Dialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mSaveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                mSaveButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveContact();
                    }
                });
            }
        });

        return dialog;
    }

    /**
     * Override to keep dialog from being dismissed on rotation
     */
    @Override
    public void onDestroyView() {
        writeToPerson(mPerson);
        super.onDestroyView();
    }

    public void restoreFromPerson(final GPerson person) {
        if (mPerson != null) {
            mPerson = person;
            mName.setText(mPerson.getName());
            if (!U.isNullEmpty(mPerson.gender)) {
                if (mPerson.gender.equals("male")) {
                    mGender.check(R.id.male);
                } else if (mPerson.gender.equals("female")) {
                    mGender.check(R.id.female);
                }
            } else {
                mGender.clearCheck();
            }
            if (mPerson.phone_numbers != null && mPerson.phone_numbers.length > 0) {
                final GPhoneNumber number = mPerson.phone_numbers[0];
                mPhone.setText(number.number);
                mPhoneLocation.setSelection(getIndexOfId(number.location, R.array.phone_location_ids));
            } else {
                mPhone.setText("");
                mPhoneLocation.setSelection(0);
            }
            if (mPerson.email_addresses != null && mPerson.email_addresses.length > 0) {
                final GEmailAddress email = mPerson.email_addresses[0];
                mEmail.setText(email.email);
            } else {
                mEmail.setText("");
            }
            if (mPerson.current_address != null) {
                final GAddress address = mPerson.current_address;
                if (!U.isNullEmpty(address.address1)) {
                    mAddressLine1.setText(address.address1);
                } else {
                    mAddressLine1.setText("");
                }
                if (!U.isNullEmpty(address.address2)) {
                    mAddressLine2.setText(address.address2);
                } else {
                    mAddressLine2.setText("");
                }
                if (!U.isNullEmpty(address.city)) {
                    mAddressCity.setText(address.city);
                } else {
                    mAddressCity.setText("");
                }
                if (!U.isNullEmpty(address.state)) {
                    mAddressState.setSelection(getIndexOfId(address.state, R.array.state_ids));
                } else {
                    mAddressState.setSelection(0);
                }
                if (!U.isNull(address.country)) {
                    mAddressCountry.setSelection(getIndexOfId(address.country, R.array.country_ids));
                } else {
                    mAddressCountry.setSelection(0);
                }
                if (!U.isNullEmpty(address.zip)) {
                    mAddressZip.setText(address.zip);
                } else {
                    mAddressZip.setText("");
                }
            }
        }
    }

    public void writeToPerson(final GPerson person) {
        if (person != null) {
            person.setName(mName.getText().toString());
            final int genderId = mGender.getCheckedRadioButtonId();
            if (genderId == R.id.male) {
                person.gender = "male";
            } else if (genderId == R.id.female) {
                person.gender = "female";
            } else {
                person.gender = "";
            }

            final GPhoneNumber phone = new GPhoneNumber();
            phone.number = mPhone.getText().toString();
            phone.location = getIdFromTitle(mPhoneLocation.getSelectedItem().toString(), R.array.phone_location_titles, R.array.phone_location_ids);
            person.phone_numbers = new GPhoneNumber[]{phone};

            final GEmailAddress email = new GEmailAddress();
            email.email = mEmail.getText().toString();
            person.email_addresses = new GEmailAddress[]{email};

            final GAddress address = new GAddress();
            address.address1 = mAddressLine1.getText().toString();
            address.address2 = mAddressLine2.getText().toString();
            address.city = mAddressCity.getText().toString();
            address.state = getIdFromTitle(mAddressState.getSelectedItem().toString(), R.array.state_titles, R.array.state_ids);
            address.country = getIdFromTitle(mAddressCountry.getSelectedItem().toString(), R.array.country_titles, R.array.country_ids);
            address.zip = mAddressZip.getText().toString();
            person.current_address = address;
        }
    }

    private void saveContact() {
        if (mPerson == null) return;

        writeToPerson(mPerson);
        mPerson._assignToMe = mAssignToMe;

        if (!mPerson.isValid()) {
            Toast.makeText(getSupportActivity(), R.string.add_contact_name_required, Toast.LENGTH_SHORT).show();
            mName.requestFocus();
            return;
        }

        showProgress();

        mTask = new SafeAsyncTask<Person>() {

            @Override
            public Person call() throws Exception {
                return Api.createPerson(mPerson, ApiOptions.builder() //
                        .include(Include.contact_assignments) //
                        .include(Include.current_address) //
                        .include(Include.email_addresses) //
                        .include(Include.phone_numbers) //
                        .include(Include.organizational_roles) //
                        .build()).get();
            }

            @Override
            public void onSuccess(final Person contact) {
                setResult(RESULT_OK, contact);
                dismiss();
            }

            @Override
            public void onFinally() {
                mTask = null;
            }

            @Override
            public void onException(final Exception e) {
                final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
                eh.makeToast(R.string.add_contact_failed);

                hideProgress();
            }

            @Override
            public void onInterrupted(final Exception e) {

            }

        };
        Application.getExecutor().execute(mTask.future());
    }

    @Override
    public void onCancel(final DialogInterface dialog) {
        if (mTask != null) {
            mTask.cancel(true);
        }
        super.onCancel(dialog);
    }

    public void showProgress() {
        if (U.isNull(mForm, mProgress)) return;
        mForm.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
        if (mSaveButton != null) {
            mSaveButton.setEnabled(false);
        }
    }

    public void hideProgress() {
        if (U.isNull(mForm, mProgress)) return;
        mProgress.setVisibility(View.GONE);
        mForm.setVisibility(View.VISIBLE);
        if (mSaveButton != null) {
            mSaveButton.setEnabled(true);
        }
    }

    public String getIdFromTitle(final String title, final int titlesResource, final int idsResource) {
        final String[] ids = getResources().getStringArray(idsResource);
        return ids[getIndexOfTitle(title, titlesResource)];
    }

    public String getTitleFromId(final String id, final int idsResource, final int titlesResource) {
        final String[] titles = getResources().getStringArray(titlesResource);
        return titles[getIndexOfId(id, idsResource)];
    }

    public int getIndexOfId(final String id, final int idsResource) {
        final String[] ids = getResources().getStringArray(idsResource);
        return Arrays.asList(ids).indexOf(id);
    }

    public int getIndexOfTitle(final String title, final int titleResources) {
        final String[] ids = getResources().getStringArray(titleResources);
        return Arrays.asList(ids).indexOf(title);
    }
}