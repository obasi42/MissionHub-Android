package com.missionhub.application;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import android.content.Context;
import android.util.Log;

/**
 * Accesses the configuration values stored in assets/missionhub.properties. If no configuration is found, a runtime
 * exception is thrown.
 */
public class Configuration {

	/** the static configuration instance */
	private static Configuration sConfiguration;

	/** the logging tag */
	public final String TAG = Configuration.class.getSimpleName();

	/** the properties object */
	private final Properties sProperties;

	/** returns a singleton Configuration instance */
	public static Configuration getInstance() {
		if (sConfiguration == null) {
			sConfiguration = new Configuration(Application.getContext());
		}
		return sConfiguration;
	}

	/**
	 * Creates a new configuration object
	 */
	private Configuration(final Context ctx) {
		sProperties = new Properties();
		try {
			sProperties.load(ctx.getAssets().open("missionhub.properties"));
		} catch (final IOException e) {
			Log.e(TAG, "Could not load configuration file. Please make sure assets/missionhub.properties exists and is a valid property file.", e);
			throw new RuntimeException("Could not load configuration");
		}
	}

	public static enum Environment {
		DEVELOPMENT, TEST, PRODUCTION
	}

	public static Environment getEnvironment() {
		final String env = Configuration.getInstance().sProperties.getProperty("ENVIRONMENT", Environment.PRODUCTION.name());
		return Environment.valueOf(env.toUpperCase(Locale.US));
	}

	public static enum Market {
		PLAY, AMAZON, NOOK
	}

	public static Market getMarket() {
		final String market = Configuration.getInstance().sProperties.getProperty("MARKET", Market.PLAY.name());
		return Market.valueOf(market.toUpperCase(Locale.US));
	}

	public static String getApiUrl() {
		return stripTrailingSlash(Configuration.getInstance().sProperties.getProperty("API_URL", ""));
	}

	public static String getApiVersion() {
		return Configuration.getInstance().sProperties.getProperty("API_VERSION", "");
	}

	public static String getSurveyUrl() {
		return stripTrailingSlash(Configuration.getInstance().sProperties.getProperty("SURVEY_URL", ""));
	}

	public static String getOauthUrl() {
		return stripTrailingSlash(Configuration.getInstance().sProperties.getProperty("OAUTH_URL", ""));
	}

	public static String getOauthClientId() {
		return Configuration.getInstance().sProperties.getProperty("OAUTH_CLIENT_ID", "");
	}

	public static String getOauthClientSecret() {
		return Configuration.getInstance().sProperties.getProperty("OAUTH_CLIENT_SECRET", "");
	}

	public static String getOauthScope() {
		return Configuration.getInstance().sProperties.getProperty("OAUTH_SCOPE", "");
	}

	public static boolean isCacheHttpEnabled() {
		final String cache = Configuration.getInstance().sProperties.getProperty("CACHE_HTTP_ENABLED", "true");
		return Boolean.parseBoolean(cache);
	}

	public static boolean isCacheFileEnabled() {
		final String cache = Configuration.getInstance().sProperties.getProperty("CACHE_FILE_ENABLED", "true");
		return Boolean.parseBoolean(cache);
	}

	public static String getCacheFileSize() {
		return Configuration.getInstance().sProperties.getProperty("CACHE_FILE_SIZE", "10m");
	}

	public static boolean isAnalyticsEnabled() {
		final String cache = Configuration.getInstance().sProperties.getProperty("ANALYTICS_ENABLED", "false");
		return Boolean.parseBoolean(cache);
	}

	public static boolean isAnalyticsDebug() {
		final String cache = Configuration.getInstance().sProperties.getProperty("ANALYTICS_DEBUG", "false");
		return Boolean.parseBoolean(cache);
	}

	public static boolean isAnalyticsDryRun() {
		final String cache = Configuration.getInstance().sProperties.getProperty("ANALYTICS_DRY_RUN", "true");
		return Boolean.parseBoolean(cache);
	}

	public static boolean isAnalyticsAnonymizeIp() {
		final String cache = Configuration.getInstance().sProperties.getProperty("ANAYLTICS_ANONYMIZE_IP", "false");
		return Boolean.parseBoolean(cache);
	}

	public static String getAnalyticsKey() {
		return Configuration.getInstance().sProperties.getProperty("ANALYTICS_KEY", "");
	}

	public static boolean isErrbitEnabled() {
		final String cache = Configuration.getInstance().sProperties.getProperty("ERRBIT_ENABLED", "false");
		return Boolean.parseBoolean(cache);
	}

	public static String getErrbitApiUrl() {
		return stripTrailingSlash(Configuration.getInstance().sProperties.getProperty("ERRBIT_API_URL", ""));
	}

	public static String getErrbitApiKey() {
		return Configuration.getInstance().sProperties.getProperty("ERRBIT_API_KEY", "");
	}

	@Override
	public String toString() {
		return Configuration.getInstance().sProperties.toString();
	}

	/**
	 * removes the trailing slash from a url
	 * 
	 * @param s
	 * @return
	 */
	private static String stripTrailingSlash(String s) {
		if (s.endsWith("/")) {
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}
}