package com.missionhub.exception;

import java.util.concurrent.ExecutionException;

import org.holoeverywhere.widget.Toast;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.webkit.WebViewClient;

import com.missionhub.R;
import com.missionhub.api.ApiException;
import com.missionhub.application.Application;
import com.missionhub.network.NetworkUnavailableException;

/** Helps display error dialogs from exceptions */
public class ExceptionHelper {

	/** the context in which to display the error */
	private final Context mContext;

	/** the throwable that contains the error data */
	private Throwable mThrowable;

	/** the title of the error */
	private String mTitle = "Error";

	/** the message of the error */
	private String mMessage;

	/** the alert dialog */
	private AlertDialog mDialog;

	/** the positive button interface */
	private DialogButton mPositive;

	/** the negative button interface */
	private DialogButton mNeutral;

	/** the negative button interface */
	private DialogButton mNegative;

	/** if the dialog is cancelable */
	private boolean mCancelable = false;

	/** the icon drawable */
	private Drawable mIcon;

	/** append the missionhub support information to the error message */
	private boolean mAppendSupport = true;

	/** Creates a new ExceptionHelper object */
	public ExceptionHelper(final Context context, final Exception exception) {
		mContext = context;
		setException(exception);
	}

	/** Shows the alert dialog */
	public AlertDialog show() {
		final AlertDialog dialog = getDialog();

		if (dialog != null && !dialog.isShowing()) dialog.show();

		return dialog;
	}

	/** Builds and returns the dialog for more modification before showing */
	public AlertDialog getDialog() {
		if (mDialog == null && mContext != null) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

			builder.setTitle(mTitle);
			builder.setMessage(mMessage);

			if (mIcon != null) {
				builder.setIcon(mIcon);
			}

			if (mPositive != null) {
				builder.setPositiveButton(mPositive.getTitle(), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int whichButton) {
						mPositive.onClick(dialog, whichButton);
					}
				});
			}

			if (mNeutral != null) {
				builder.setNeutralButton(mNeutral.getTitle(), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int whichButton) {
						mNeutral.onClick(dialog, whichButton);
					}
				});
			}

			if (mNegative != null) {
				builder.setNegativeButton(mNegative.getTitle(), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int whichButton) {
						mNegative.onClick(dialog, whichButton);
					}
				});
			}

			builder.setCancelable(mCancelable);

			mDialog = builder.create();
		}

		return mDialog;
	}

	/** sets the title of the dialog. safe to call after the dialog is showing */
	public void setTitle(final String title) {
		if (mDialog != null) {
			mDialog.setTitle(title);
		}
		mTitle = title;
	}

	/** sets the message of the dialog. safe to call after the dialog is showing */
	public void setMessage(String message) {
		if (mAppendSupport) {
			message += "\n\n" + getString(R.string.exception_helper_support);
		}
		if (mDialog != null) {
			mDialog.setMessage(message);
		}
		mMessage = message;
	}

	/** returns the dialog title */
	public String getTitle() {
		return mTitle;
	}

	/** returns the dialog message */
	public String getMessage() {
		return mMessage;
	}

	/** returns the throwable */
	public Throwable getThrowable() {
		return mThrowable;
	}

	/** sets the exception and rebuilds the dialog if needed */
	public void setException(final Throwable throwable) {

		Log.e("ExceptionHelper", throwable.getMessage(), throwable);

		mThrowable = throwable;

		// find the initial issue if it was wrapped in an ExecutionException
		if (mThrowable instanceof ExecutionException) {
			final Throwable it = ((ExecutionException) mThrowable).getCause();
			if (it != null) {
				mThrowable = it;
			}
		}

		// calculate the title and message from the exception
		if (mThrowable instanceof ExceptionHelperException) {
			setTitle(((ExceptionHelperException) mThrowable).getDialogTitle());
			setMessage(((ExceptionHelperException) mThrowable).getDialogMessage());
			final int icon = ((ExceptionHelperException) mThrowable).getDialogIconId();
			if (icon != 0 && icon != -1) {
				setIcon(icon);
			}
		} else if (mThrowable instanceof ApiException) {
			setTitle(((ApiException) mThrowable).getTitle());
			setMessage(mThrowable.getMessage() + "\ncode: " + ((ApiException) mThrowable).getCode());
		} else if (mThrowable instanceof WebViewException) {
			setTitle(getString(R.string.exception_helper_network_error));
			final int code = ((WebViewException) mThrowable).getCode();
			switch (code) {
			case WebViewClient.ERROR_CONNECT:
				setMessage(getString(R.string.exception_helper_mh_down));
				break;
			case WebViewClient.ERROR_TIMEOUT:
				setMessage(getString(R.string.exception_helper_mh_not_responding));
				break;
			case WebViewClient.ERROR_HOST_LOOKUP:
				setException(new NetworkUnavailableException());
				return;
			default:
				setMessage(mThrowable.getMessage() + "\nweb view client code: " + ((WebViewException) mThrowable).getCode());
			}
		} else {
			setMessage(mThrowable.getMessage());
		}
	}

	private String getString(final int resId) {
		return Application.getContext().getString(resId);
	}

	/** sets the positive button */
	public void setPositiveButton(final DialogButton button) {
		mPositive = button;
	}

	/** sets the neutral button */
	public void setNeutralButton(final DialogButton button) {
		mNeutral = button;
	}

	/** sets the negative button */
	public void setNegativeButton(final DialogButton button) {
		mNegative = button;
	}

	/**
	 * Interface represents a button title and click action
	 */
	public interface DialogButton {

		abstract String getTitle();

		abstract void onClick(DialogInterface dialog, int whichButton);

	}

	/**
	 * Interface to help automate the display of an exception type
	 */
	public interface ExceptionHelperException {

		public String getDialogTitle();

		public String getDialogMessage();

		public int getDialogIconId();

	}

	/**
	 * Sets whether or not the dialog is cancelable with the back button.
	 * 
	 * @param cancelable
	 */
	public void setCancelable(final boolean cancelable) {
		if (mDialog != null) {
			mDialog.setCancelable(cancelable);
		}
		mCancelable = cancelable;
	}

	/** sets the dialog icon resource id */
	public void setIcon(final int resource) {
		if (resource == 0 || resource == -1) setIcon(null);

		setIcon(mContext.getResources().getDrawable(resource));
	}

	/** sets the dialog icon */
	public void setIcon(final Drawable icon) {
		if (mDialog != null) {
			mDialog.setIcon(icon);
		}
		mIcon = icon;
	}

	/** set whether or not the missionhub support information is appended to the message */
	public void setAppendSupport(final boolean appendSupport) {
		mAppendSupport = appendSupport;
	}

	/**
	 * Shows a toast with the data from the exception
	 */
	public void makeToast() {
		if (mContext != null) Toast.makeText(mContext, getTitle() + "\n\n" + getMessage(), Toast.LENGTH_LONG).show();
	}

	/**
	 * Shows a toast with the data from the exception
	 * 
	 * @param failure
	 *            a failure message to be displayed with the exception data.
	 */
	public void makeToast(final String failure) {
		if (mContext != null) Toast.makeText(mContext, failure + "\n" + getTitle() + "\n\n" + getMessage(), Toast.LENGTH_LONG).show();
	}

	/**
	 * Shows a toast with the data from the exception
	 * 
	 * @param failure
	 *            a failure message to be displayed before the exception data.
	 */
	public void makeToast(final int failure) {
		if (mContext != null) makeToast(mContext.getString(failure));
	}
}