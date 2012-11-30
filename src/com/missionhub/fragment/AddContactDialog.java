package com.missionhub.fragment;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.Toast;

import roboguice.util.SafeAsyncTask;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockDialogFragment;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.ApiContact;
import com.missionhub.application.Application;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.model.Person;
import com.missionhub.util.U;

public class AddContactDialog extends RoboSherlockDialogFragment {

	/** the task used to add a contact */
	private SafeAsyncTask<Person> mTask;

	/** the add contact listener interface */
	private WeakReference<AddContactListener> mListener;

	/** contact form view */
	private View mForm;

	/** the progress view */
	private View mProgress;

	/** if the contact should be assigned to the current user */
	private boolean mAssignToMe = false;

	/** the save button */
	private Button mSave;

	/** the contact data holder */
	private ApiContact mContact;

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

	public AddContactDialog() {}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!U.superGetRetainInstance(this)) {
			setRetainInstance(true);
		}

		if (getArguments() != null) {
			mAssignToMe = getArguments().getBoolean("assignToMe", false);
		}
		setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Sherlock_Light_Dialog);
	}

	public static AddContactDialog getInstance(final boolean assignToMe) {
		final AddContactDialog dialog = new AddContactDialog();
		final Bundle args = new Bundle();
		args.putBoolean("assignToMe", assignToMe);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		final View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_contact_dialog, null);
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

		final ArrayAdapter<CharSequence> phoneAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.phone_location_titles, R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		phoneAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		mPhoneLocation.setAdapter(phoneAdapter);

		final ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.state_titles, R.layout.simple_spinner_item);
		stateAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		mAddressState.setAdapter(stateAdapter);

		final ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.country_titles, R.layout.simple_spinner_item);
		countryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		mAddressCountry.setAdapter(countryAdapter);

		if (mContact == null) {
			mContact = new ApiContact();
		} else {
			restoreFromContact(mContact);
		}

		final View title = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_contact_dialog_title, null);
		mSave = (Button) title.findViewById(R.id.save);
		mSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				saveContact();
			}
		});

		mForm = view.findViewById(R.id.form);
		mProgress = view.findViewById(R.id.progress_container);

		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setCustomTitle(title);
		builder.setView(view);
		builder.setOnCancelListener(this);

		if (mTask != null) {
			showProgress();
		}

		return builder.create();
	}

	public void setAddContactListener(final AddContactListener listener) {
		mListener = new WeakReference<AddContactListener>(listener);
	}

	public AddContactListener getAddContactListener() {
		if (mListener != null) {
			return mListener.get();
		}
		return null;
	}

	/**
	 * Override to keep dialog from being dismissed on rotation
	 */
	@Override
	public void onDestroyView() {
		writeToContact(mContact);

		if (getDialog() != null && U.superGetRetainInstance(this)) getDialog().setDismissMessage(null);
		super.onDestroyView();
	}

	public void restoreFromContact(final ApiContact contact) {
		if (mContact != null) {
			mContact = contact;
			mName.setText(mContact.getName());
			if (mContact.gender.equals("male")) {
				mGender.check(R.id.male);
			} else if (mContact.gender.equals("female")) {
				mGender.check(R.id.female);
			}
			mPhone.setText(mContact.phoneNumber);
			mPhoneLocation.setSelection(getIndexOfId(mContact.phoneLocation, R.array.phone_location_ids));
			mEmail.setText(mContact.emailEmail);
			mAddressLine1.setText(mContact.address1);
			mAddressLine2.setText(mContact.address2);
			mAddressCity.setText(mContact.addressCity);
			mAddressState.setSelection(getIndexOfId(mContact.addressState, R.array.state_ids));
			mAddressCountry.setSelection(getIndexOfId(mContact.addressCountry, R.array.country_ids));
			mAddressZip.setText(mContact.addressZip);
		}
	}

	public void writeToContact(final ApiContact contact) {
		if (contact != null) {
			contact.setName(mName.getText().toString());
			final int genderId = mGender.getCheckedRadioButtonId();
			if (genderId == R.id.male) {
				contact.gender = "male";
			} else if (genderId == R.id.female) {
				contact.gender = "female";
			} else {
				contact.gender = "";
			}
			contact.phoneNumber = mPhone.getText().toString();
			contact.phoneLocation = getIdFromTitle(mPhoneLocation.getSelectedItem().toString(), R.array.phone_location_titles, R.array.phone_location_ids);
			contact.emailEmail = mEmail.getText().toString();
			contact.address1 = mAddressLine1.getText().toString();
			contact.address2 = mAddressLine2.getText().toString();
			contact.addressCity = mAddressCity.getText().toString();
			contact.addressState = getIdFromTitle(mAddressState.getSelectedItem().toString(), R.array.state_titles, R.array.state_ids);
			contact.addressCountry = getIdFromTitle(mAddressCountry.getSelectedItem().toString(), R.array.country_titles, R.array.country_ids);
			contact.addressZip = mAddressZip.getText().toString();
		}
	}

	public void postComplete(final Person contact) {
		final Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					if (getAddContactListener() != null) {
						getAddContactListener().onContactAdded(contact);
					}
				} catch (final Exception e) { /* ignore */}
			}
		});
	}

	public void postCanceled() {
		final Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					if (getAddContactListener() != null) {
						getAddContactListener().onAddContactCanceled();
					}
				} catch (final Exception e) { /* ignore */}
			}
		});
	}

	public static interface AddContactListener {
		public void onContactAdded(Person contact);

		public void onAddContactCanceled();
	}

	public static AddContactDialog show(final FragmentManager fm) {
		return show(fm, false);
	}

	private void saveContact() {
		if (mContact == null) return;

		writeToContact(mContact);
		mContact.assignToMe = mAssignToMe;

		if (!mContact.isValid()) {
			Toast.makeText(getActivity(), R.string.add_contact_name_required, Toast.LENGTH_SHORT).show();
			mName.requestFocus();
			return;
		}

		showProgress();

		mTask = new SafeAsyncTask<Person>() {

			@Override
			public Person call() throws Exception {
				return Api.createContact(mContact).get();
			}

			@Override
			public void onSuccess(final Person contact) {
				postComplete(contact);
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
		postCanceled();
		super.onCancel(dialog);
	}

	/**
	 * Creates and shows the add contact dialog
	 * 
	 * @param fm
	 * @param assignToMe
	 * @return
	 */
	public static AddContactDialog show(final FragmentManager fm, final boolean assignToMe) {
		final FragmentTransaction ft = fm.beginTransaction();
		final Fragment prev = fm.findFragmentByTag("add_contact_dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		final AddContactDialog fragment = AddContactDialog.getInstance(assignToMe);
		fragment.show(ft, "add_contact_dialog");
		return fragment;
	}

	public void showProgress() {
		if (U.isNull(mForm, mProgress)) return;
		mForm.setVisibility(View.GONE);
		mSave.setVisibility(View.GONE);
		mProgress.setVisibility(View.VISIBLE);
	}

	public void hideProgress() {
		if (U.isNull(mForm, mProgress)) return;
		mProgress.setVisibility(View.GONE);
		mSave.setVisibility(View.VISIBLE);
		mForm.setVisibility(View.VISIBLE);
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