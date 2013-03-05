package com.missionhub.application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;
import com.missionhub.R;
import com.missionhub.model.DaoMaster;
import com.missionhub.model.DaoMaster.OpenHelper;
import com.missionhub.model.DaoSession;
import com.missionhub.model.MissionHubOpenHelper;
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

        // set the last last version id for future upgrades
        SettingsManager.setApplicationLastVersionId(getVersionCode());

        // setup the image loader
        initImageLoader();

        registerEventSubscriber(this, ToastEvent.class);
    }

    private void initImageLoader() {
        final DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        MemoryCacheAware<String, Bitmap> memoryCache = new LRULimitedMemoryCache(memoryCacheSize);

        LimitedAgeDiscCache diskCache = new LimitedAgeDiscCache(StorageUtils.getCacheDirectory(this), 60 * 60 * 24 * 3);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions)
                .discCache(diskCache)
                .memoryCache(memoryCache)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();

        ImageLoader.getInstance().init(config);
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
            final OpenHelper helper = new MissionHubOpenHelper(Application.getContext(), DB_NAME, null);
            mDb = helper.getWritableDatabase();
        }
        return mDb;
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
            return Application.getContext().getPackageManager().getPackageInfo(Application.getContext().getPackageName(), 0).versionCode;
        } catch (final Exception e) {
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
            return Application.getContext().getPackageManager().getPackageInfo(Application.getContext().getPackageName(), 0).versionName;
        } catch (final Exception e) {
        }
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
    public static void showToast(final int message, final int duration) {
        showToast(Application.getContext().getString(message), duration);
    }

    public static void showToast(final String message, final int duration) {
        Application.postEvent(new ToastEvent(message, duration));
    }

    public void onEventMainThread(final ToastEvent event) {
        Toast.makeText(getContext(), event.message, event.duration).show();
    }

    public static class ToastEvent {
        public String message;
        public int duration = Toast.LENGTH_SHORT;

        public ToastEvent(final String message) {
            this.message = message;
        }

        public ToastEvent(final String message, final int duration) {
            this.message = message;
            this.duration = duration;
        }
    }
}