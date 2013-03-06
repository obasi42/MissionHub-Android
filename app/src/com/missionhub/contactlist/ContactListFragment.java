package com.missionhub.contactlist;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.application.Session.SessionOrganizationIdChanged;
import com.missionhub.contactlist.ContactListAdapter.ProgressItem;
import com.missionhub.contactlist.ContactListProvider.OnContactListAdapterWorkingListener;
import com.missionhub.contactlist.ContactListProvider.OnContactListProviderExceptionListener;
import com.missionhub.contactlist.ContactListView.OnContactCheckedListener;
import com.missionhub.contactlist.ContactListView.OnContactClickListener;
import com.missionhub.contactlist.ContactListView.OnContactLongClickListener;
import com.missionhub.fragment.BaseFragment;
import com.missionhub.model.Person;
import com.missionhub.util.U;
import org.holoeverywhere.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

public abstract class ContactListFragment extends BaseFragment implements OnContactCheckedListener, OnContactClickListener, OnContactLongClickListener, OnContactListAdapterWorkingListener,
        OnContactListProviderExceptionListener {

    /**
     * the logging tag
     */
    public static final String TAG = ContactListFragment.class.getSimpleName();

    /**
     * the contact list view
     */
    private ContactListView mListView;

    /**
     * the contact list provider
     */
    private ContactListProvider mProvider;

    /**
     * the alternative provider (for search)
     */
    private ContactListProvider mAltProvider;

    /**
     * the on contact checked listener
     */
    private OnContactCheckedListener mOnContactCheckedListener;

    /**
     * the on contact click listener
     */
    private OnContactClickListener mOnContactClickListener;

    /**
     * the on contact long click listener
     */
    private OnContactLongClickListener mOnContactLongClickListener;

    /**
     * the on working listener
     */
    private OnContactListAdapterWorkingListener mOnContactListAdapterWorkingListener;

    /**
     * the on exception listener
     */
    private OnContactListProviderExceptionListener mOnContactListProviderExceptionListener;

    /**
     * a listener to wrap the other listeners to pass the fragment
     */
    private ContactListFragmentListener mFragmentListener;

    /**
     * the list view progress item
     */
    private ProgressItem mProgressItem;

    public ContactListFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!U.superGetRetainInstance(this)) {
            this.setRetainInstance(true);
        }
    }

    public int onGetLayoutResource() {
        return R.layout.fragment_contact_list;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(onGetLayoutResource(), container, false);
        mListView = (ContactListView) view.findViewById(android.R.id.list);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));
        mListView.setOnContactCheckedListener(mOnContactCheckedListener);
        mListView.setOnContactClickListener(mOnContactClickListener);
        mListView.setOnContactLongClickListener(mOnContactLongClickListener);

        if (mOnContactCheckedListener != null) {
            mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        }

        mProgressItem = onCreateProgressItem();

        if (mProvider == null) {
            mProvider = onCreateContactProvider();
            Application.registerEventSubscriber(mProvider, SessionOrganizationIdChanged.class);
            configureProvider();
            mProvider.afterCreate();
        } else {
            configureProvider();
        }

        return view;
    }

    private void configureProvider() {
        for (ContactListProvider provider : new ContactListProvider[] { mProvider, mAltProvider }) {
            if (provider != null) {
                provider.setContext(getSupportActivity());
                provider.setOnContactListExceptionListener(mOnContactListProviderExceptionListener);
                provider.setOnContactListWorkingListener(mOnContactListAdapterWorkingListener);
                provider.setProgressItem(mProgressItem);
                provider.setContactList(mListView);
            }
        }
        if (mAltProvider != null) {
            mListView.setProvider(mAltProvider);
        } else {
            mListView.setProvider(mProvider);
        }
    }

    public abstract ContactListProvider onCreateContactProvider();

    @Override
    public void onDestroy() {
        if (mProvider != null) {
            Application.unregisterEventSubscriber(mProvider);
        }
        super.onDestroy();
    }

    public ProgressItem onCreateProgressItem() {
        return new ProgressItem();
    }

    /**
     * Returns the list provider
     */
    public ContactListProvider getProvider() {
        return mProvider;
    }

    /**
     * Sets the contact list provider
     */
    public void setProvider(final ContactListProvider provider) {
        mProvider = provider;
        configureProvider();
    }

    public void setAltProvider(final ContactListProvider provider) {
        mAltProvider = provider;
        configureProvider();
    }

    public void removeAltProvider() {
        mAltProvider = null;
        configureProvider();
    }

    /**
     * Sets the OnContactLongClickListener
     */
    public void setOnContactLongClickListener(final OnContactLongClickListener listener) {
        mOnContactLongClickListener = listener;
        if (mListView != null) {
            mListView.setOnContactLongClickListener(mOnContactLongClickListener);
        }
    }

    /**
     * Sets the OnContactClickListener
     *
     * @param listener
     */
    public void setOnContactClickListener(final OnContactClickListener listener) {
        mOnContactClickListener = listener;
        if (mListView != null) {
            mListView.setOnContactClickListener(mOnContactClickListener);
        }
    }

    /**
     * Sets the OnContactCheckedListener
     *
     * @param listener
     */
    public void setOnContactCheckedListener(final OnContactCheckedListener listener) {
        mOnContactCheckedListener = listener;
        if (mListView != null) {
            mListView.setOnContactCheckedListener(mOnContactCheckedListener);
        }
    }

    public void setOnContactListAdapterWorkingListener(final OnContactListAdapterWorkingListener listener) {
        mOnContactListAdapterWorkingListener = listener;
        if (mProvider != null) {
            mProvider.setOnContactListWorkingListener(mOnContactListAdapterWorkingListener);
        }
    }

    public void setOnContactListProviderExceptionListener(final OnContactListProviderExceptionListener listener) {
        mOnContactListProviderExceptionListener = listener;
        if (mProvider != null) {
            mProvider.setOnContactListExceptionListener(mOnContactListProviderExceptionListener);
        }
    }

    public void setContactListFragmentListener(final ContactListFragmentListener listener) {
        mFragmentListener = listener;
        mOnContactCheckedListener = this;
        mOnContactClickListener = this;
        mOnContactLongClickListener = this;
        mOnContactListAdapterWorkingListener = this;
        mOnContactListProviderExceptionListener = this;
    }

    public boolean isWorking() {
        return mProvider != null && mProvider.isWorking();
    }

    public void reload() {
        if (mProvider != null) {
            mProvider.reload();
        }
    }

    public int getCheckedItemCount() {
        if (mListView != null) {
            return mListView.getCheckedItemCount();
        }
        return 0;
    }

    public List<Person> getCheckedPeople() {
        final List<Person> people = new ArrayList<Person>();

        if (mListView == null) return people;

        final long[] personIds = mListView.getCheckedItemIds();

        for (final long id : personIds) {
            final Person person = Application.getDb().getPersonDao().load(id);
            if (person != null) {
                people.add(person);
            }
        }

        return people;
    }

    public void clearChecked() {
        if (mListView != null) mListView.clearChecked();
    }

    public static interface ContactListFragmentListener {

        public void onContactListProviderException(ContactListFragment fragment, final Exception exception);

        public void onWorkingChanged(ContactListFragment fragment, final boolean working);

        public boolean onContactLongClick(ContactListFragment fragment, Person person, int position, long id);

        public void onContactClick(ContactListFragment fragment, final Person person, final int position, final long id);

        public void onContactChecked(ContactListFragment fragment, final Person person, final int position, final boolean checked);

        public void onAllContactsUnchecked(ContactListFragment fragment);
    }

    @Override
    public void onContactListProviderException(final Exception exception) {
        if (mFragmentListener != null) {
            mFragmentListener.onContactListProviderException(this, exception);
        }
    }

    @Override
    public void onWorkingChanged(final boolean working) {
        if (mFragmentListener != null) {
            mFragmentListener.onWorkingChanged(this, working);
        }
    }

    @Override
    public boolean onContactLongClick(final Person person, final int position, final long id) {
        return mFragmentListener != null && mFragmentListener.onContactLongClick(this, person, position, id);
    }

    @Override
    public void onContactClick(final Person person, final int position, final long id) {
        if (mFragmentListener != null) {
            mFragmentListener.onContactClick(this, person, position, id);
        }
    }

    @Override
    public void onContactChecked(final Person person, final int position, final boolean checked) {
        if (mFragmentListener != null) {
            mFragmentListener.onContactChecked(this, person, position, checked);
        }
    }

    @Override
    public void onAllContactsUnchecked() {
        if (mFragmentListener != null) {
            mFragmentListener.onAllContactsUnchecked(this);
        }
    }
}