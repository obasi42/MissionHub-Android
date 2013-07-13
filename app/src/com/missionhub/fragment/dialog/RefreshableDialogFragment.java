package com.missionhub.fragment.dialog;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.missionhub.R;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;

/**
 * Dialog with a refresh button in the title
 */
public abstract class RefreshableDialogFragment extends BaseDialogFragment {

    /**
     * the refresh button
     */
    private ImageView mRefresh;

    /**
     * the refresh animation
     */
    private Animation mRefreshAnimation;

    /**
     * True when the dialog is refreshing
     */
    private boolean mRefreshing = false;

    /**
     * Holds the dialog title data
     */
    private DialogTitle mDialogTitle = new DialogTitle();

    /**
     * The Title text view
     */
    private TextView mTitle;

    /**
     * The Icon ImageView
     */
    private ImageView mIcon;

    /**
     * The created alert dialog
     */
    private AlertDialog mDialog;

    public RefreshableDialogFragment() {
    }

    /**
     * Called when the dialog title is set up
     *
     * @param title
     * @return
     */
    public abstract void onCreateDialogTitle(DialogTitle title);

    /**
     * Called when the refresh button is clicked
     */
    public abstract void onRefresh();

    /**
     * Override to create the dialog from an alert dialog builder
     *
     * @param savedInstanceState
     * @return
     */
    public AlertDialog.Builder onCreateRefreshableDialog(final Bundle savedInstanceState) {
        return null;
    }

    @Override
    public final Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = onCreateRefreshableDialog(savedInstanceState);
        if (builder == null) {
            setStyle(DialogFragment.STYLE_NO_TITLE, getTheme());
            return super.onCreateDialog(savedInstanceState);
        }

        builder.setCustomTitle(createTitleView(LayoutInflater.from(getSupportActivity())));
        mDialog = builder.create();
        mDialog.setButtonBehavior(0);

        return mDialog;
    }

    /**
     * Override to create the dialog from a custom view
     *
     * @param inflater
     * @param savedInstanceState
     * @return
     */
    public View onCreateRefreshableView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = onCreateRefreshableView(inflater, container, savedInstanceState);
        if (view == null) return super.onCreateView(inflater, container, savedInstanceState);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout layout = new LinearLayout(inflater.getContext());
        layout.setLayoutParams(lp);
        layout.setOrientation(LinearLayout.VERTICAL);

        final View titleView = createTitleView(inflater);
        titleView.findViewById(R.id.titleDivider).setVisibility(View.VISIBLE);

        layout.addView(titleView);
        layout.addView(view);

        return layout;
    }

    private View createTitleView(final LayoutInflater inflater) {
        if (!mDialogTitle.created) {
            onCreateDialogTitle(mDialogTitle);
            mDialogTitle.created = true;
        }

        View titleView = inflater.inflate(R.layout.fragment_refreshable_dialog_title, null);
        mTitle = (TextView) titleView.findViewById(R.id.alertTitle);
        mIcon = (ImageView) titleView.findViewById(R.id.icon);
        updateTitleView();

        mRefresh = (ImageView) titleView.findViewById(R.id.action_refresh);
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
        mRefreshAnimation = AnimationUtils.loadAnimation(getSupportActivity(), R.anim.clockwise_refresh);
        mRefreshAnimation.setRepeatCount(Animation.INFINITE);

        if (mRefreshing) {
            startRefreshAnimation();
        } else {
            stopRefreshAnimation();
        }

        return titleView;
    }

    private void updateTitleView() {
        if (mIcon == null || mTitle == null) return;

        if (mDialogTitle.icon != 0) {
            mIcon.setImageResource(mDialogTitle.icon);
        } else if (mDialogTitle.iconD != null) {
            mIcon.setImageDrawable(mDialogTitle.iconD);
        } else {
            mTitle.setPadding(mIcon.getPaddingLeft(),
                    mIcon.getPaddingTop(),
                    mIcon.getPaddingRight(),
                    mIcon.getPaddingBottom());
            mIcon.setVisibility(View.GONE);
        }

        if (mDialogTitle.title != 0) {
            mTitle.setText(mDialogTitle.title);
        }

        if (mDialogTitle.titleChar != null) {
            mTitle.setText(mDialogTitle.titleChar);
        }
    }

    /**
     * Disables the refresh button and starts the animation.
     */
    public void startRefreshAnimation() {
        mRefreshing = true;
        if (verifyAnimation()) {
            mRefresh.setEnabled(false);
            mRefresh.startAnimation(mRefreshAnimation);
        }
    }

    /**
     * Enables the refresh button and stops the animation.
     */
    public void stopRefreshAnimation() {
        mRefreshing = false;
        if (verifyAnimation()) {
            mRefresh.setEnabled(true);
            mRefresh.clearAnimation();
        }
    }

    private boolean verifyAnimation() {
        return mRefresh != null && mRefreshAnimation != null;
    }

    /**
     * Holds the data for the title
     */
    public static class DialogTitle {
        private DialogTitle() {
        }

        private boolean created = false;

        private int title;
        private CharSequence titleChar;

        private int icon;
        private Drawable iconD;

        /**
         * Sets the dialog title resource
         *
         * @param title
         */
        public void setTitle(int title) {
            this.title = title;
        }

        /**
         * Sets the dialog title text
         *
         * @param title
         */
        public void setTitle(CharSequence title) {
            this.titleChar = title;
        }

        /**
         * Sets the dialog icon resource
         *
         * @param icon
         */
        public void setIcon(int icon) {
            this.icon = icon;
        }

        /**
         * Sets the dialog icon drawable
         *
         * @param icon
         */
        public void setIcon(Drawable icon) {
            this.iconD = icon;
        }
    }

    /**
     * Sets the dialog title resource
     *
     * @param title
     */
    public void setTitle(int title) {
        mDialogTitle.title = title;
        updateTitleView();
    }

    /**
     * Sets the dialog title text
     *
     * @param title
     */
    public void setTitle(CharSequence title) {
        mDialogTitle.titleChar = title;
        updateTitleView();
    }

    /**
     * Sets the dialog icon resource
     *
     * @param icon
     */
    public void setIcon(int icon) {
        mDialogTitle.icon = icon;
        updateTitleView();
    }

    /**
     * Sets the dialog icon drawable
     *
     * @param icon
     */
    public void setIcon(Drawable icon) {
        mDialogTitle.iconD = icon;
        updateTitleView();
    }

    public void hideRefresh() {
        if (mRefresh != null)
            mRefresh.setVisibility(View.GONE);
    }

    public void showRefresh() {
        if (mRefresh != null)
            mRefresh.setVisibility(View.VISIBLE);
    }

    public void setButtonEnabled(int buttonId, boolean enabled) {
        if (mDialog != null) {
            final Button button = mDialog.getButton(buttonId);
            if (button != null) {
                button.setEnabled(enabled);
            }
        }
    }

}