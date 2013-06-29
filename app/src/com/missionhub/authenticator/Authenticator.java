package com.missionhub.authenticator;

import android.accounts.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.facebook.Session;
import com.missionhub.application.Configuration;

/**
 * Authenticates MissionHub accounts using the system account manager
 */
public class Authenticator extends AbstractAccountAuthenticator {

    /**
     * the logging tag
     */
    public static final String TAG = Authenticator.class.getSimpleName();

    /**
     * the context
     */
    private final Context mContext;

    /**
     * the account type
     */
    public static final String ACCOUNT_TYPE = "com.missionhub";

    /**
     * the user data key for personId
     */
    public static final String KEY_PERSON_ID = "com.missionhub.authenticator.key_person_id";

    /**
     * Creates a new Authenticator object
     *
     * @param context
     */
    public Authenticator(final Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle addAccount(final AccountAuthenticatorResponse response, final String accountType, final String authTokenType, final String[] requiredFeatures, final Bundle options) {
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle getAuthToken(final AccountAuthenticatorResponse response, final Account account, final String authTokenType, final Bundle loginOptions) throws NetworkErrorException {
        final AccountManager accountManager = AccountManager.get(mContext);

        String token = accountManager.peekAuthToken(account, ACCOUNT_TYPE);
        if (!TextUtils.isEmpty(token)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, token);
            return result;
        }

        // If we get here, then we couldn't access the user's token - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity panel.
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(Authenticator.KEY_PERSON_ID, Long.parseLong(accountManager.getUserData(account, KEY_PERSON_ID)));
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle hasFeatures(final AccountAuthenticatorResponse response, final Account account, final String[] features) {
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }
}
