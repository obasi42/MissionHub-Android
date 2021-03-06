package com.missionhub.people;

import android.content.Context;
import android.util.Log;

import com.missionhub.api.Api;
import com.missionhub.api.ApiOptions;
import com.missionhub.api.ApiRequest;
import com.missionhub.api.PeopleListOptions;
import com.missionhub.model.Person;
import com.missionhub.util.SafeAsyncTask;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A {@link DynamicPeopleListProvider} that loads people from the api.
 */
public class ApiPeopleListProvider extends DynamicPeopleListProvider {

    /**
     * The android logging tag
     */
    public static final String TAG = ApiPeopleListProvider.class.getSimpleName();
    /**
     * Single thread executor to ensure sequential results
     */
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    /**
     * The person list options used to filter the api results
     */
    private PeopleListOptions mOptions;
    /**
     * The task used to fetch contacts
     */
    private SafeAsyncTask<List<Person>> mTask;

    /**
     * Constructs a new ApiPeopleListProvider.
     *
     * @param context The current context.
     */
    public ApiPeopleListProvider(Context context) {
        this(context, null);
    }

    /**
     * Constructs a new ApiPeopleListProvider with options.
     *
     * @param context The current context.
     * @param options The people list options
     */
    public ApiPeopleListProvider(Context context, PeopleListOptions options) {
        this(context, options, true);
    }

    /**
     * Constructs a new ApiPeopleListProvider with start state.
     *
     * @param context The current context.
     * @param start   True if the provider should begin loading immediately.
     */
    public ApiPeopleListProvider(Context context, boolean start) {
        this(context, null, start);
    }

    /**
     * Constructs a new ApiPeopleListProvider with options and start state.
     *
     * @param context The current context.
     * @param options The people list options
     * @param start   True if the provider should begin loading immediately.
     */
    public ApiPeopleListProvider(Context context, PeopleListOptions options, boolean start) {
        this(context, options, start, 3);
    }

    /**
     * Constructs a new ApiPeopleListProvider with options, start state, and mave view types.
     *
     * @param context
     * @param options      The people list options
     * @param start        True if the provider should begin loading immediately.
     * @param maxViewTypes The maximum number of view types the {@link PeopleListView} will accept.
     */
    public ApiPeopleListProvider(Context context, PeopleListOptions options, boolean start, int maxViewTypes) {
        super(context, start, maxViewTypes);

        mOptions = options;
        if (mOptions == null) {
            mOptions = new PeopleListOptions.Builder().build();
            Log.w(TAG, "PeopleListOptions was null... creating default.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load() {
        loadMore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Person> loadMore() {
        synchronized (getLock()) {
            mTask = new SafeAsyncTask<List<Person>>() {
                private ApiRequest<List<Person>> mRequest;

                @Override
                public List<Person> call() throws Exception {
                    mRequest = Api.listPeople(mOptions, ApiOptions.builder()
                            .include(Api.Include.assigned_tos)
                            .include(Api.Include.current_address)
                            .include(Api.Include.email_addresses)
                            .include(Api.Include.organizational_permission)
                            .include(Api.Include.organizational_labels)
                            .include(Api.Include.phone_numbers)
                            .build());

                    return mRequest.get();
                }

                @Override
                public void onSuccess(final List<Person> people) {
                    if (isCanceled()) return;
                    synchronized (getLock()) {
                        mOptions.advanceOffset();
                        if (people.size() < mOptions.getLimit()) {
                            setDone(true);
                        }
                        onAfterLoad(people);
                    }
                }

                @Override
                public void onFinally() {
                    if (mRequest != null) {
                        mRequest.disconnect();
                    }
                    if (isCanceled()) return;
                    synchronized (getLock()) {
                        mTask = null;
                        setLoading(false);
                    }
                }

                @Override
                public void onException(final Exception e) {
                    if (isCanceled()) return;
                    setPaused(true);
                    ApiPeopleListProvider.this.onException(e);
                }
            };
            setLoading(true);
            mExecutor.execute(mTask.future());
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelLoadMore() {
        synchronized (getLock()) {
            try {
                mTask.cancel(true);
                mTask = null;
                setLoading(false);
            } catch (Exception e) { /* ignore */ }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reload() {
        synchronized (getLock()) {
            mOptions.setOffset(0);
            super.reload();
        }
    }

    /**
     * Returns a cloned copy of the people list options. Modifications to the options
     * will not change the list.
     *
     * @return
     */
    public PeopleListOptions getPeopleListOptions() {
        synchronized (getLock()) {
            return (PeopleListOptions) mOptions.clone();
        }
    }

    /**
     * Sets the PeopleListOptions that filter the api people list.
     *
     * @param options the list options
     */
    public void setPeopleListOptions(PeopleListOptions options) {
        synchronized (getLock()) {
            mOptions = (PeopleListOptions) options.clone();
            reload();
        }
    }
}