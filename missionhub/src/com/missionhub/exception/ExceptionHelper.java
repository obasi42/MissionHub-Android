package com.missionhub.exception;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.webkit.WebViewClient;

import com.github.kevinsawicki.http.HttpRequest;
import com.missionhub.R;
import com.missionhub.application.Application;

import org.apache.commons.lang3.StringUtils;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.Toast;

import java.lang.reflect.Type;
import java.net.UnknownHostException;

/**
 * Helps display error dialogs from exceptions
 */
public class ExceptionHelper {

    /**
     * the context in which to display the error
     */
    private Context mContext;

    /**
     * the throwable that contains the error data
     */
    private Throwable mThrowable;

    /**
     * the title of the error
     */
    private String mTitle;

    /**
     * the message of the error
     */
    private String mMessage;

    /**
     * the alert dialog
     */
    private AlertDialog mDialog;

    /**
     * the positive button interface
     */
    private DialogButton mPositive;

    /**
     * the negative button interface
     */
    private DialogButton mNeutral;

    /**
     * the negative button interface
     */
    private DialogButton mNegative;

    /**
     * if the dialog is cancelable
     */
    private boolean mCancelable = false;

    /**
     * the icon drawable
     */
    private Drawable mIcon;

    /**
     * append the missionhub support information to the error message
     */
    private boolean mAppendSupport = true;

    public ExceptionHelper(final Throwable throwable) {
        this(Application.getContext(), throwable);
    }

    /**
     * Creates a new ExceptionHelper object
     */
    public ExceptionHelper(final Context context, final Throwable throwable) {
        mContext = context;
        mTitle = getContext().getString(R.string.exception_helper_error);
        setThrowable(throwable);
    }

    /**
     * Shows the alert dialog
     */
    public AlertDialog show() {
        if (!isApplicationContext()) {
            final AlertDialog dialog = getDialog();

            try {
                if (dialog != null && !dialog.isShowing()) {
                    dialog.show();
                    return dialog;
                }
            } catch (Exception e) { /* ignore */ }
        }

        makeToast();
        return null;
    }

    /**
     * Builds and returns the dialog for more modification before showing
     */
    public AlertDialog getDialog() {
        if (isApplicationContext()) {
            return null;
        }

        if (mDialog != null && (mDialog.isShowing() || mDialog.getContext() != getContext())) {
            try {
                mDialog.dismiss();
            } catch (Exception e) { /* ignore */ }
            mDialog = null;
        }

        if (!isApplicationContext() && mDialog == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

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

    /**
     * sets the title of the dialog. safe to call after the dialog is showing
     */
    public void setTitle(final String title) {
        if (mDialog != null) {
            mDialog.setTitle(title);
        }
        mTitle = title;
    }

    /**
     * sets the message of the dialog. safe to call after the dialog is showing
     */
    public void setMessage(String message) {
        if (mAppendSupport) {
            message += "\n\n" + getString(R.string.exception_helper_support);
        }
        if (mDialog != null) {
            mDialog.setMessage(message);
        }
        mMessage = message;
    }

    /**
     * returns the dialog title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * returns the dialog message
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * returns the throwable
     */
    public Throwable getThrowable() {
        return mThrowable;
    }

    /**
     * sets the exception and rebuilds the dialog if needed
     */
    public void setThrowable(final Throwable throwable) {
        Log.e("ExceptionHelper", throwable.getMessage(), throwable);

        if (!isIgnoredThrowable(throwable)) {
            Application.trackException(Thread.currentThread().getName(), throwable, false);
        }

        mThrowable = throwable;

        // unwrap the exception if possible
        while (!(mThrowable instanceof ExceptionHelperException) && mThrowable.getCause() != null) {
            mThrowable = mThrowable.getCause();
        }

        // Exception types
        if (mThrowable instanceof ExceptionHelperException) {
            setTitle(((ExceptionHelperException) mThrowable).getDialogTitle());
            setMessage(((ExceptionHelperException) mThrowable).getDialogMessage());
            final int icon = ((ExceptionHelperException) mThrowable).getDialogIconId();
            if (icon != 0 && icon != -1) {
                setIcon(icon);
            }
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
                    setTitle(getString(R.string.network_unavailable_exception_title));
                    setMessage(getString(R.string.network_unavailable_exception));
                    break;
                default:
                    setMessage(mThrowable.getMessage() + "\nweb view client code: " + ((WebViewException) mThrowable).getCode());
            }
        } else if (mThrowable instanceof UnknownHostException) {
            setTitle(getString(R.string.network_unavailable_exception_title));
            setMessage(getString(R.string.network_unavailable_exception));
        } else if (mThrowable.getMessage().contains("ECONNREFUSED")) {
            setTitle(getString(R.string.exception_helper_network_error));
            setMessage(getString(R.string.network_server_unavailable));
        } else {
            setMessage(mThrowable.getMessage());
        }
    }

    private String getString(final int resId) {
        return Application.getContext().getString(resId);
    }

    /**
     * sets the positive button
     */
    public void setPositiveButton(final DialogButton button) {
        mPositive = button;
    }

    /**
     * sets the neutral button
     */
    public void setNeutralButton(final DialogButton button) {
        mNeutral = button;
    }

    /**
     * sets the negative button
     */
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

    /**
     * sets the dialog icon resource id
     */
    public void setIcon(final int resource) {
        if (resource == 0 || resource == -1) setIcon(null);

        setIcon(getContext().getResources().getDrawable(resource));
    }

    /**
     * sets the dialog icon
     */
    public void setIcon(final Drawable icon) {
        if (mDialog != null) {
            mDialog.setIcon(icon);
        }
        mIcon = icon;
    }

    /**
     * set whether or not the missionhub support information is appended to the message
     */
    public void setAppendSupport(final boolean appendSupport) {
        mAppendSupport = appendSupport;
    }

    /**
     * Shows a toast with the data from the exception
     */
    public void makeToast() {
        makeToast(null);
    }

    /**
     * Shows a toast with the data from the exception
     *
     * @param failure a failure message to be displayed with the exception data.
     */
    public void makeToast(final String failure) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(failure)) {
            sb.append(failure).append("\n");
        }
        if (StringUtils.isNotEmpty(getTitle())) {
            sb.append(getTitle()).append("\n\n");
        }
        if (StringUtils.isNotEmpty(getMessage())) {
            sb.append(getMessage());
        }

        Toast.makeText(getContext(), sb.toString(), Toast.LENGTH_LONG).show();
    }

    /**
     * Shows a toast with the data from the exception
     *
     * @param failure a failure message to be displayed before the exception data.
     */
    public void makeToast(final int failure) {
        makeToast(getContext().getString(failure));
    }

    public boolean isApplicationContext() {
        return getContext() == Application.getContext();
    }

    public Context getContext() {
        if (mContext == null) {
            mContext = Application.getContext();
        }
        return mContext;
    }

    public boolean isIgnoredThrowable(Throwable t) {
        for(Type type : sIgnoredThrowableTypes) {
            if (t.getClass() == type) {
                return true;
            }
        }
        return false;
    }

    public static final Type[] sIgnoredThrowableTypes = new Type[] {
            com.facebook.FacebookOperationCanceledException.class,
            HttpRequest.HttpRequestException.class,
            com.missionhub.exception.WebViewException.class,
    };

}