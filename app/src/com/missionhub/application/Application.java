package com.missionhub.application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.missionhub.R;
import com.missionhub.event.ToastEvent;
import com.missionhub.model.DaoMaster;
import com.missionhub.model.DaoMaster.OpenHelper;
import com.missionhub.model.DaoSession;
import com.missionhub.model.MissionHubOpenHelper;
import com.missionhub.util.NetworkUtils;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import de.greenrobot.event.EventBus;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.ACRAConfigurationException;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.holoeverywhere.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The MissionHub application context
 */
@ReportsCrashes(formKey = "")
public class Application extends org.holoeverywhere.app.Application {

    /**
     * the logging tag
     */
    public static final String TAG = Application.class.getSimpleName();

    /**
     * singleton application
     */
    private static Application sApplication;

    /**
     * the executor service
     */
    private static ExecutorService sExecutorService;

    /**
     * application context's sqlite database
     */
    private static SQLiteDatabase mDb;

    /**
     * application context's database cache mSession
     */
    private static DaoSession mDaoSession;

    /**
     * database name
     */
    private static final String DB_NAME = "missionhub.db";

    /**
     * called when the application is created.
     */
    @Override
    public synchronized void onCreate() {
        super.onCreate();
        sApplication = this;

        UpgradeManager.doUpgrade();

        if (Configuration.isACRAEnabled()) {
            try {
                ACRAConfiguration config = ACRA.getConfig();
                config.setFormKey(Configuration.getACRAFormKey());
                config.setResToastText(R.string.crash_dialog_title);
                config.setResDialogCommentPrompt(R.string.crash_dialog_comment_prompt);
                config.setResDialogText(R.string.crash_dialog_text);
                config.setResDialogTitle(R.string.crash_dialog_title);
                config.setResDialogIcon(R.drawable.ic_launcher);
                config.setResDialogOkToast(R.string.crash_dialog_ok_toast);
                config.setMode(ReportingInteractionMode.DIALOG);
                ACRA.init(this);
            } catch (ACRAConfigurationException e) {
                Log.e("MissionHub", e.getMessage(), e);
            }
        }

        if (Configuration.isAnalyticsEnabled()) {
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);
            ga.setDebug(Configuration.isAnalyticsDebug());

            Tracker tracker = ga.getTracker(Configuration.getAnalyticsKey());
            tracker.setAnonymizeIp(Configuration.isAnalyticsAnonymizeIp());
            tracker.setUseSecure(true);

            ga.setDefaultTracker(tracker);
        }

        // set up the networking
        NetworkUtils.disableConnectionReuseIfNecessary();
        NetworkUtils.enableHttpResponseCache(this);

        // setup the image loader
        initImageLoader();

        registerEventSubscriber(this, ToastEvent.class);
    }

    private void initImageLoader() {
        DisplayImageOptions.Builder defaultOptions = new DisplayImageOptions.Builder();
        defaultOptions.cacheInMemory(true);
        if (Configuration.isCacheFileEnabled()) {
            defaultOptions.cacheOnDisc(true);
        }

        int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        MemoryCacheAware<String, Bitmap> memoryCache = new LRULimitedMemoryCache(memoryCacheSize);

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.defaultDisplayImageOptions(defaultOptions.build());
        config.memoryCache(memoryCache);
        config.threadPoolSize(3);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);

        if (Configuration.isCacheFileEnabled()) {
            LimitedAgeDiscCache diskCache = new LimitedAgeDiscCache(StorageUtils.getCacheDirectory(this), Configuration.getCacheFileAge());
            config.discCache(diskCache);
        }

        ImageLoader.getInstance().init(config.build());
    }

    /**
     * @return the singleton instance of the application
     */
    public synchronized static Application getInstance() {
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
    public synchronized static ExecutorService getExecutor() {
        if (sExecutorService == null) {
            sExecutorService = Executors.newCachedThreadPool();
        }
        return sExecutorService;
    }

    /**
     * triggered when a log memory notification is received from the os. posts an OnLowMemoryEvent event to notify
     * listeners that they should reduce their memory usage.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        postEvent(new OnLowMemoryEvent());
    }

    /**
     * event posted on low memory
     */
    public static class OnLowMemoryEvent {
    }

    /**
     * @return the raw sqlite database for the application context
     */
    public synchronized static SQLiteDatabase getRawDb() {
        if (mDb == null) {
            final OpenHelper helper = new MissionHubOpenHelper(getContext(), DB_NAME, null);
            mDb = helper.getWritableDatabase();
        }
        return mDb;
    }

    /**
     * Closes the database entirely
     */
    public synchronized static void closeDb() {
        mDaoSession = null;
        if (mDb != null) {
            mDb.close();
            mDb = null;
        }
    }

    /**
     * @return the database database session for the application context
     */
    public synchronized static DaoSession getDb() {
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
            return getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionCode;
        } catch (final Exception e) {
            /* ignore */
        }
        return -1;
    }

    /**
     * Returns the package version name
     *
     * @return
     */
    public static String getVersionName() {
        try {
            return getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName;
        } catch (final Exception e) {
            /* ignore */
        }
        return null;
    }

    /**
     * Returns the default event bus
     *
     * @return
     */
    public static EventBus getEventBus() {
        return EventBus.getDefault();
    }

    /**
     * Posts an event to the default EventBus
     */
    public static void postEvent(final Object event) {
        getEventBus().post(event);
    }

    /**
     * Register an EventBus event subscriber with the default bus
     */
    public static void registerEventSubscriber(final Object subscriber) {
        getEventBus().register(subscriber);
    }

    /**
     * Register an EventBus event subscriber with the default bus
     */
    public static void registerEventSubscriber(final Object subscriber, final Class<?> eventType, final Class<?>... moreEventTypes) {
        getEventBus().register(subscriber, eventType, moreEventTypes);
    }

    /**
     * Unregisters an EventBus event subscriber from the default bus
     */
    public static void unregisterEventSubscriber(final Object subscriber) {
        getEventBus().unregister(subscriber);
    }

    /**
     * Unregisters an EventBus event subscriber from the default bus
     */
    public static void unregisterEventSubscriber(final Object subscriber, final Class<?>... eventTypes) {
        getEventBus().unregister(subscriber, eventTypes);
    }

    public static void showToast(final int message, final int duration) {
        showToast(getContext().getString(message), duration);
    }

    public static void showToast(final String message, final int duration) {
        postEvent(new ToastEvent(message, duration));
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(final ToastEvent event) {
        Toast.makeText(getContext(), event.message, event.duration).show();
    }

    public static Tracker getTracker() {
        if (Configuration.isAnalyticsEnabled()) {
            return GoogleAnalytics.getInstance(sApplication).getDefaultTracker();
        }
        return null;
    }

    public static void trackView(String view) {
        if (getTracker() != null) {
            getTracker().sendView(view);
        }
    }

    public static void trackException(String thread, Throwable e, boolean fatal) {
        if (getTracker() != null) {
            getTracker().sendException(thread, e, fatal);
        }
    }

    public static void trackEvent(String category, String action, String label) {
        trackEvent(category, action, label, 0);
    }

    public static void trackEvent(String category, String action, String label, long value) {
        if (getTracker() != null) {
            getTracker().sendEvent(category, action, label, value);
        }
    }

    public static void trackNewSession() {
        if (getTracker() != null) {
            getTracker().setStartSession(true);
        }
    }
}