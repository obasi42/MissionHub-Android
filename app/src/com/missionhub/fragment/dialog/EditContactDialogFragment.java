package com.missionhub.fragment.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.Api.Include;
import com.missionhub.api.ApiOptions;
import com.missionhub.application.Application;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.model.Person;
import com.missionhub.model.gson.GAddress;
import com.missionhub.model.gson.GEmailAddress;
import com.missionhub.model.gson.GPerson;
import com.missionhub.model.gson.GPhoneNumber;
import com.missionhub.util.ObjectUtils;
import com.missionhub.util.SafeAsyncTask;
import com.missionhub.util.TaskUtils;

import org.apache.commons.lang3.StringUtils;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.ArrayAdapter;
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

    /**
     * the dialog save button
     */
    private Button mSaveButton;

    /**
     * the dialog cancel button
     */
    private Button mCancelButton;

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
    private Spinner mAddressType;

    public EditContactDialogFragment() {
    }

    public static EditContactDialogFragment showForResult(FragmentManager fm, Integer requestCode) {
        return EditContactDialogFragment.show(EditContactDialogFragment.class, fm, null, requestCode);
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
        mAddressType = (Spinner) view.findViewById(R.id.address_type);

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

        final ArrayAdapter<CharSequence> addressTypeAdapter = ArrayAdapter.createFromResource(getSupportActivity(), R.array.address_types_titles, R.layout.simple_spinner_item);
        addressTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mAddressType.setAdapter(addressTypeAdapter);

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
        builder.setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveContact();
            }
        });
        builder.setNeutralButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancel();
            }
        });
        builder.setBlockDismiss(true);

        updateUI();

        return builder.create();
    }

    /**
     * Override to keep dialog from being dismissed on rotation
     */
    @Override
    public void onDestroyView() {
        writeToPerson(mPerson);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() != null) {
            mSaveButton = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
            mCancelButton = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_NEUTRAL);
            updateUI();
        }
    }

    public void restoreFromPerson(final GPerson person) {
        if (mPerson != null) {
            mPerson = person;
            mName.setText(mPerson.getName());
            if (StringUtils.isNotEmpty(mPerson.gender)) {
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

            if (mPerson.addresses != null && mPerson.addresses.length > 0) {
                final GAddress address = mPerson.addresses[0];
                if (StringUtils.isNotEmpty(address.address1)) {
                    mAddressLine1.setText(address.address1);
                } else {
                    mAddressLine1.setText("");
                }
                if (StringUtils.isNotEmpty(address.address2)) {
                    mAddressLine2.setText(address.address2);
                } else {
                    mAddressLine2.setText("");
                }
                if (StringUtils.isNotEmpty(address.city)) {
                    mAddressCity.setText(address.city);
                } else {
                    mAddressCity.setText("");
                }
                if (StringUtils.isNotEmpty(address.state)) {
                    mAddressState.setSelection(getIndexOfId(address.state, R.array.state_ids));
                } else {
                    mAddressState.setSelection(0);
                }
                if (address.country != null) {
                    mAddressCountry.setSelection(getIndexOfId(address.country, R.array.country_ids));
                } else {
                    mAddressCountry.setSelection(0);
                }
                if (StringUtils.isNotEmpty(address.zip)) {
                    mAddressZip.setText(address.zip);
                } else {
                    mAddressZip.setText("");
                }
                if (address.address_type != null) {
                    mAddressType.setSelection(getIndexOfId(address.address_type, R.array.address_types_ids));
                } else {
                    mAddressType.setSelection(0);
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
            person.addresses = new GAddress[]{address};
            address.address_type = getIdFromTitle(mAddressType.getSelectedItem().toString(), R.array.address_types_titles, R.array.address_types_ids);
        }
    }

    private void saveContact() {
        if (mPerson == null) return;

        writeToPerson(mPerson);

        if (!mPerson.isValid()) {
            Toast.makeText(getSupportActivity(), R.string.add_contact_name_required, Toast.LENGTH_SHORT).show();
            mName.requestFocus();
            return;
        }

        hideKeyboard();

        mTask = new SafeAsyncTask<Person>() {

            @Override
            public Person call() throws Exception {
                return Api.createPerson(mPerson, ApiOptions.builder() //
                        .include(Include.contact_assignments) //
                        .include(Include.addresses) //
                        .include(Include.email_addresses) //
                        .include(Include.phone_numbers) //
                        .include(Include.organizational_permission) //
                        .include(Include.organizational_labels) //
                        .build()).get();
            }

            @Override
            public void onSuccess(final Person contact) {
                setResult(RESULT_OK, contact.getId());
                dismiss();
            }

            @Override
            public void onFinally() {
                mTask = null;
                updateUI();
            }

            @Override
            public void onException(final Exception e) {
                final ExceptionHelper eh = new ExceptionHelper(Application.getContext(), e);
                eh.makeToast(R.string.add_contact_failed);
            }

            @Override
            public void onInterrupted(final Exception e) {

            }

        };
        updateUI();
        Application.getExecutor().execute(mTask.future());
    }

    @Override
    public void onDestroy() {
        TaskUtils.cancel(mTask);
        super.onDestroy();
    }

    public void showProgress() {
        if (ObjectUtils.isNull(mForm, mProgress)) return;
        mForm.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);

        if (mSaveButton != null && mCancelButton != null) {
            mSaveButton.setEnabled(false);
            mCancelButton.setEnabled(false);
        }
    }

    public void hideProgress() {
        if (ObjectUtils.isNull(mForm, mProgress)) return;
        mProgress.setVisibility(View.GONE);
        mForm.setVisibility(View.VISIBLE);

        if (mSaveButton != null && mCancelButton != null) {
            mSaveButton.setEnabled(true);
            mCancelButton.setEnabled(true);
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

    private void hideKeyboard() {
        getSupportActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void updateUI() {
        if (mTask != null) {
            setCancelable(false);
            showProgress();
        } else {
            setCancelable(true);
            hideProgress();
        }
    }
}