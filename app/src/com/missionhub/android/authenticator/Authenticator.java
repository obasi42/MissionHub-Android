package com.missionhub.android.authenticator;

import android.accounts.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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
    public static final String ACCOUNT_TYPE = "com.missionhub.android";

    /**
     * the user data key for personId
     */
    public static final String KEY_PERSON_ID = "com.missionhub.android.authenticator.key_person_id";

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
    public Bundle confirmCredentials(final AccountAuthenticatorResponse response, final Account account, final Bundle options) {
        return null;
    }

    @Override
    public Bundle editProperties(final AccountAuthenticatorResponse response, final String accountType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getAuthToken(final AccountAuthenticatorResponse response, final Account account, final String authTokenType, final Bundle loginOptions) throws NetworkErrorException {
        if (!authTokenType.equals(ACCOUNT_TYPE)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        // Since we are just using the account manager for managing accounts, the account password IS the token.
        final AccountManager am = AccountManager.get(mContext);
        final String token = am.getPassword(account);
        if (token != null) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
            result.putString(AccountManager.KEY_AUTHTOKEN, token);
            return result;
        }

        // If we get here, then we couldn't access the user's token - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity panel.
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(final String authTokenType) {
        return null;
    }

    @Override
    public Bundle hasFeatures(final AccountAuthenticatorResponse response, final Account account, final String[] features) {
        // we don't currently have any features
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle updateCredentials(final AccountAuthenticatorResponse response, final Account account, final String authTokenType, final Bundle loginOptions) {
        return null;
    }
}
