package com.missionhub.exception;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.webkit.WebViewClient;

import com.missionhub.api.ApiException;
import com.missionhub.network.NetworkUnavailableException;

/** Helps display error dialogs from exceptions */
public class ExceptionHelper {

	/** the context in which to display the error */
	private final Context mContext;

	/** the exception that contains the error data */
	private Exception mException;

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
		if (mDialog == null) {
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
			message += "\n\nEmail support@missionhub.com if the problem persists.";
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

	/** returns the exception */
	public Exception getException() {
		return mException;
	}

	/** sets the exception and rebuilds the dialog if needed */
	public void setException(final Exception e) {
		mException = e;

		// calculate the title and message from the exception
		if (e instanceof ExceptionHelperException) {
			setTitle(((ExceptionHelperException) mException).getDialogTitle());
			setMessage(((ExceptionHelperException) mException).getDialogMessage());
			final int icon = ((ExceptionHelperException) mException).getDialogIconId();
			if (icon != 0 && icon != -1) {
				setIcon(icon);
			}
		} else if (e instanceof ApiException) {
			setTitle(((ApiException) mException).getTitle());
			setMessage(mException.getMessage() + "\ncode: " + ((ApiException) mException).getCode());
		} else if (e instanceof WebViewException) {
			setTitle("Network Error");
			final int code = ((WebViewException) mException).getCode();
			switch (code) {
			case WebViewClient.ERROR_CONNECT:
				setMessage("The MissionHub server is currently down. Please try again in a few minutes.");
				break;
			case WebViewClient.ERROR_TIMEOUT:
				setMessage("Either your internet connection is very slow or the MissionHub servers are not currently responding. Please try again in a few minutes.");
				break;
			case WebViewClient.ERROR_HOST_LOOKUP:
				setException(new NetworkUnavailableException());
				return;
			default:
				setMessage(mException.getMessage() + "\nweb view client code: " + ((WebViewException) mException).getCode());
			}
		} else {
			setMessage(mException.getMessage());
		}
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
}