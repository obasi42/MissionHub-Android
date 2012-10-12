//package com.missionhub.fragment;
//
//import roboguice.inject.InjectView;
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.missionhub.R;
//import com.missionhub.activity.HostActivity;
//import com.missionhub.application.Application;
//import com.missionhub.application.Session;
//import com.missionhub.application.Session.SessionAccountPickedEvent;
//import com.missionhub.application.Session.SessionFetchingOrganizationsStartEvent;
//import com.missionhub.application.Session.SessionInvalidEvent;
//import com.missionhub.application.Session.SessionNotResumedNoAccountEvent;
//import com.missionhub.application.Session.SessionNotResumedPickAccountEvent;
//import com.missionhub.application.Session.SessionResumedEvent;
//import com.missionhub.application.Session.SessionResumingEvent;
//import com.missionhub.application.Session.SessionValidEvent;
//import com.missionhub.application.Session.SessionValidatingEvent;
//import com.missionhub.application.SettingsManager;
//import com.missionhub.authenticator.AuthenticatorActivity;
//import com.missionhub.util.IntentHelper;
//import com.missionhub.util.U;
//import com.squareup.otto.Subscribe;
//
///**
// * Fragment displayed to unauthenticated users. Proxies authentication actions to the session.
// */
//public class UnauthenticatedFragment extends BaseFragment {
//
//	@InjectView(R.id.loading) ProgressBar mProgress;
//	@InjectView(R.id.logo) ImageView mLogo;
//	@InjectView(R.id.btn_login) Button mLogin;
//	@InjectView(R.id.status) TextView mStatus;
//	@InjectView(R.id.btn_resources) TextView mResources;
//
//	/** request code for authentication */
//	private static final int REQUEST_AUTHENTICATE = 1;
//
//	@Override
//	public void onCreate(final Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setRetainInstance(true);
//	}
//
//	@Override
//	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
//		final View view = inflater.inflate(R.layout.fragment_unauthenticated, container, false);
//		return view;
//	}
//
//	@Override
//	public void onViewCreated(final View view, final Bundle savedInstanceState) {
//		super.onViewCreated(view, savedInstanceState);
//		mLogin.setOnClickListener(loginOnClickListener);
//		mResources.setOnClickListener(resourcesOnClickListener);
//
//		Application.eventBusRegister(this);
//
//		Session.getInstance().resumeSession();
//	}
//
//	@Override
//	public void onDestroyView() {
//		Application.eventBusUnregister(this);
//		super.onDestroyView();
//	}
//
//	/**
//	 * Called when the session begins resuming
//	 * 
//	 * @param event
//	 */
//	@Subscribe
//	public void onSessionResuming(final SessionResumingEvent event) {
//		showLoading("Resuming last session...");
//	}
//
//	/**
//	 * Called when the session is resumed, but not validated
//	 * 
//	 * @param event
//	 */
//	@Subscribe
//	public void onSessionResumed(final SessionResumedEvent event) {
//		Session.getInstance().validate();
//	}
//
//	/**
//	 * Called when the session was not resumed because no missionhub accounts exist
//	 * 
//	 * @param event
//	 */
//	@Subscribe
//	public void onSessionNotResumedNoAccount(final SessionNotResumedNoAccountEvent event) {
//		showLoginButton();
//	}
//
//	/**
//	 * Called when the session was not resumed because the user logged out or the account was delete, but there are
//	 * other accounts available to use
//	 * 
//	 * @param event
//	 */
//	@Subscribe
//	public void onSessionNotResumedPickAccount(final SessionNotResumedPickAccountEvent event) {
//		showLoginButton();
//		Session.getInstance().pickAccount(getActivity());
//	}
//
//	/**
//	 * Called when the resumed session is validating against the MH servers.
//	 * 
//	 * @param event
//	 */
//	@Subscribe
//	public void onSessionValidating(final SessionValidatingEvent event) {
//		showLoading("Logging in...");
//	}
//
//	@Subscribe
//	public void onSessionFetchingOrganizationsStart(final SessionFetchingOrganizationsStartEvent event) {
//		showLoading("Updating your organizations...");
//	}
//
//	/**
//	 * Called when the resumed session is invalidated by the MH servers.
//	 * 
//	 * @param event
//	 */
//	@Subscribe
//	public void onSessionInvalid(final SessionInvalidEvent event) {
//		if (event.throwable != null) {
//			Toast.makeText(getActivity(), event.throwable.getMessage(), Toast.LENGTH_LONG).show();
//		} else {
//			Toast.makeText(getActivity(), "You account has changed, please login again.", Toast.LENGTH_LONG).show();
//		}
//		showLoginButton();
//	}
//
//	/**
//	 * Called when the session is validated against the MH servers.
//	 * 
//	 * @param event
//	 */
//	@Subscribe
//	public void onSessionValid(final SessionValidEvent event) {
//		startActivity(new Intent(getActivity(), HostActivity.class));
//		getActivity().finish();
//	}
//
//	/**
//	 * Called when the user selects an account to use
//	 * 
//	 * @param event
//	 */
//	@Subscribe
//	public void onAccountPicked(final SessionAccountPickedEvent event) {
//		if (event.personId > -1) {
//			SettingsManager.setSessionLastUserId(event.personId);
//			Session.getInstance().resumeSession();
//		} else {
//			startActivityForResult(new Intent(getActivity(), AuthenticatorActivity.class), REQUEST_AUTHENTICATE);
//		}
//	}
//
//	/**
//	 * Shows the progress dialog with an optional status message
//	 * 
//	 * @param status
//	 */
//	private void showLoading(final String status) {
//		mLogin.setVisibility(View.GONE);
//		mProgress.setVisibility(View.VISIBLE);
//		if (!U.isNullEmpty(status)) {
//			mStatus.setText(status);
//			mStatus.setVisibility(View.VISIBLE);
//		} else {
//			mStatus.setVisibility(View.GONE);
//		}
//	}
//
//	/**
//	 * Shows the login button
//	 */
//	private void showLoginButton() {
//		mProgress.setVisibility(View.GONE);
//		mStatus.setVisibility(View.GONE);
//		mLogin.setVisibility(View.VISIBLE);
//	}
//
//	/**
//	 * Click listener for the login button
//	 */
//	public OnClickListener loginOnClickListener = new OnClickListener() {
//		@Override
//		public void onClick(final View v) {
//			if (Session.getInstance().canPickAccount()) {
//				Session.getInstance().pickAccount(getActivity());
//			} else {
//				startActivityForResult(new Intent(getActivity(), AuthenticatorActivity.class), REQUEST_AUTHENTICATE);
//			}
//		}
//	};
//
//	/**
//	 * Called when an activity returns with a result
//	 */
//	@Override
//	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
//		if (requestCode == REQUEST_AUTHENTICATE && resultCode == Activity.RESULT_OK) {
//			Session.getInstance().resumeSession();
//		}
//	}
//
//	/**
//	 * Click listener for the resources button
//	 */
//	public OnClickListener resourcesOnClickListener = new OnClickListener() {
//		@Override
//		public void onClick(final View v) {
//			IntentHelper.openUrl("http://blog.missionhub.com/");
//		}
//	};
//}