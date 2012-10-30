package com.missionhub.application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.missionhub.model.DaoMaster;
import com.missionhub.model.DaoMaster.OpenHelper;
import com.missionhub.model.DaoSession;
import com.missionhub.model.MissionHubOpenHelper;
import com.missionhub.util.ErrbitNotifier;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import de.greenrobot.event.EventBus;

/**
 * The MissionHub application context
 */
public class Application extends android.app.Application {

	/** the logging tag */
	public static final String TAG = Application.class.getSimpleName();

	/** singleton application */
	private static Application sApplication;

	/** the executor service */
	private static ExecutorService sExecutorService;

	/** application context's sqlite database */
	private static SQLiteDatabase mDb;

	/** application context's database cache mSession */
	private static DaoSession mDaoSession;

	/** database name */
	private static final String DB_NAME = "missionhub.db";

	/**
	 * called when the application is created.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		sApplication = this;

		// register the errbit notifier
		ErrbitNotifier.register();

		// set the last last version id for future upgrades
		SettingsManager.setApplicationLastVersionId(getVersionCode());

		// set up the image loader
		final ImageLoader imageLoader = ImageLoader.getInstance();
		final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).memoryCache(new WeakMemoryCache())
				.discCache(new LimitedAgeDiscCache(StorageUtils.getIndividualCacheDirectory(this), 3600 * 24 * 7)) // cache
																													// for
																													// a
																													// week
				.threadPoolSize(3).threadPriority(Thread.MIN_PRIORITY + 2).offOutOfMemoryHandling().build();
		imageLoader.init(config);
	}

	/**
	 * @return the singleton instance of the application
	 */
	public static Application getInstance() {
		return sApplication;
	}

	/**
	 * @return the application context
	 */
	public static Context getContext() {
		return getInstance().getApplicationContext();
	}

	/**
	 * returns a global executor service for long running processes
	 * 
	 * @return
	 */
	public static ExecutorService getExecutor() {
		if (sExecutorService == null) {
			sExecutorService = Executors.newCachedThreadPool();
		}
		return sExecutorService;
	}

	/**
	 * triggered when a log memory notification is received from the os. posts an OnLowMemoryEvent event to notify
	 * listeners that they should reduce their memory usage.
	 * 
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		EventBus.getDefault().post(new OnLowMemoryEvent());
	}

	/** event posted on low memory */
	public static class OnLowMemoryEvent {}

	/**
	 * @return the raw sqlite database for the application context
	 */
	public static SQLiteDatabase getRawDb() {
		if (mDb == null) {
			final OpenHelper helper = new MissionHubOpenHelper(Application.getContext(), DB_NAME, null);
			mDb = helper.getWritableDatabase();
		}
		return mDb;
	}

	/**
	 * @return the database database session for the application context
	 */
	public static DaoSession getDb() {
		if (mDaoSession == null) {
			final DaoMaster daoMaster = new DaoMaster(getRawDb());
			mDaoSession = daoMaster.newSession();
		}
		return mDaoSession;
	}

	/**
	 * Deletes the mh-mDb
	 * 
	 * @return true if the database was successfully deleted; else false.
	 */
	public boolean deleteDatabase() {
		getRawDb().close();
		mDaoSession = null;
		mDb = null;
		return deleteDatabase(DB_NAME);
	}

	/**
	 * Returns the package version code
	 * 
	 * @return
	 */
	public static int getVersionCode() {
		try {
			return Application.getContext().getPackageManager().getPackageInfo(Application.getContext().getPackageName(), 0).versionCode;
		} catch (final Exception e) {}
		return -1;
	}

	/**
	 * Returns the package version name
	 * 
	 * @return
	 */
	public static String getVersionName() {
		try {
			return Application.getContext().getPackageManager().getPackageInfo(Application.getContext().getPackageName(), 0).versionName;
		} catch (final Exception e) {}
		return null;
	}

	/**
	 * Posts an event to the default EventBus
	 */
	public static void postEvent(final Object event) {
		EventBus.getDefault().post(event);
	}

	/**
	 * Register an EventBus event subscriber with the default bus
	 */
	public static void registerEventSubscriber(final Object subscriber) {
		EventBus.getDefault().register(subscriber);
	}

	/**
	 * Register an EventBus event subscriber with the default bus
	 */
	public static void registerEventSubscriber(final Object subscriber, final Class<?> eventType, final Class<?>... moreEventTypes) {
		EventBus.getDefault().register(subscriber, eventType, moreEventTypes);
	}

	/**
	 * Unregisters an EventBus event subscriber from the default bus
	 */
	public static void unregisterEventSubscriber(final Object subscriber) {
		EventBus.getDefault().unregister(subscriber);
	}

	/**
	 * Unregisters an EventBus event subscriber from the default bus
	 */
	public static void unregisterEventSubscriber(final Object subscriber, final Class<?>... eventTypes) {
		EventBus.getDefault().unregister(subscriber, eventTypes);
	}
}