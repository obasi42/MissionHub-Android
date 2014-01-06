package com.missionhub.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.missionhub.R;
import com.missionhub.api.Api;
import com.missionhub.api.ApiOptions;
import com.missionhub.api.ApiRequest;
import com.missionhub.application.Application;
import com.missionhub.event.OnOrganizationChangedEvent;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.fragment.dialog.AssignmentDialogFragment;
import com.missionhub.fragment.dialog.BulkUpdateDialogFragment;
import com.missionhub.fragment.dialog.DeletePeopleDialogFragment;
import com.missionhub.fragment.dialog.InteractionDialogFragment;
import com.missionhub.model.Address;
import com.missionhub.model.AnswerSheet;
import com.missionhub.model.ContactAssignment;
import com.missionhub.model.EmailAddress;
import com.missionhub.model.Interaction;
import com.missionhub.model.Label;
import com.missionhub.model.OrganizationalLabel;
import com.missionhub.model.OrganizationalLabelDao;
import com.missionhub.model.Person;
import com.missionhub.model.PhoneNumber;
import com.missionhub.model.TimestampedEntity;
import com.missionhub.model.generic.FollowupStatus;
import com.missionhub.model.generic.Gender;
import com.missionhub.model.gson.GInteraction;
import com.missionhub.ui.AnimatedNetworkImageView;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.ui.ViewArrayPagerAdapter;
import com.missionhub.ui.widget.ParallaxListView;
import com.missionhub.ui.widget.TabBar;
import com.missionhub.util.DisplayUtils;
import com.missionhub.util.FragmentUtils;
import com.missionhub.util.IntentHelper;
import com.missionhub.util.LruBitmapCache;
import com.missionhub.util.ResourceUtils;
import com.missionhub.util.SafeAsyncTask;
import com.missionhub.util.SortUtils;
import com.viewpagerindicator.CirclePageIndicator;

import org.apache.commons.lang3.StringUtils;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HostedProfileFragment extends HostedFragment implements TabBar.OnTabSelectedListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static final String TAG = HostedProfileFragment.class.getSimpleName();
    private final ListMultimap<Integer, Object> mCachedObjects = ArrayListMultimap.create();
    private long mPersonId;
    private ParallaxListView mListView;
    private ProfileObjectAdapter mAdapter;
    private AnimatedNetworkImageView mAvatar;
    private ViewPager mPager;
    private CirclePageIndicator mIndicator;
    private HeaderPagerAdapter mPagerAdapter;
    private TabBar mTabBar;
    private int mSelectedPagerPage = 0;
    private int mSelectedTab = R.id.tab_info;
    private SafeAsyncTask<List<Object>> mSelectTabTask;
    private SafeAsyncTask<Void> mUpdateTask;
    private long mLastUpdate;
    private MenuItem mRefeshMenuItem;
    private RequestQueue mVolley;
    private ImageLoader mProfileImageLoader;

    public HostedProfileFragment() {
    }

    public static HostedProfileFragment instantiate(final Person person) {
        return instantiate(person.getId());
    }

    public static HostedProfileFragment instantiate(final long personId) {
        final Bundle bundle = new Bundle();
        bundle.putLong("personId", personId);

        final HostedProfileFragment fragment = new HostedProfileFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentUtils.retainInstance(this);
        setHasOptionsMenu(true);

        Application.registerEventSubscriber(this, OnOrganizationChangedEvent.class);
    }

    @Override
    public void onPrepareActionBar(ActionBar actionBar) {
        actionBar.setTitle("Profile");
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(OnOrganizationChangedEvent event) {
        Person p = Application.getDb().getPersonDao().load(mPersonId);
        if (p != null) {
            if (p.inOrganization(event.getOrganizationId())) {
                clearObjectCache();
                notifyPersonChanged();
                return;
            }
        }
        if (isVisible()) {
            getHost().onBackPressed();
        }
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        mVolley = Volley.newRequestQueue(activity);
        mProfileImageLoader = new ImageLoader(mVolley, LruBitmapCache.getInstance());

        if (getArguments() != null) {
            mPersonId = getArguments().getLong("personId", -1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mLastUpdate < System.currentTimeMillis() - 60 * 30 * 1000) {
            updatePerson();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListView = (ParallaxListView) inflater.inflate(R.layout.fragment_profile);
        mListView.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        View headerView = inflater.inflate(R.layout.fragment_profile_header, mListView, false);
        mAvatar = (AnimatedNetworkImageView) headerView.findViewById(R.id.avatar);
        mPager = (ViewPager) headerView.findViewById(R.id.pager);
        mIndicator = (CirclePageIndicator) headerView.findViewById(R.id.indicator);

        setupPager(inflater.getContext());

        View buttonGroup = inflater.inflate(R.layout.fragment_profile_button_group, mListView, false);
        mTabBar = (TabBar) buttonGroup.findViewById(R.id.tabbar);
        mTabBar.setOnTabSelectedListener(this);

        mListView.addHeaderView(headerView);
        mListView.setParallaxView(headerView);

        mListView.addHeaderView(buttonGroup);

        if (mAdapter == null) {
            mAdapter = new ProfileObjectAdapter(inflater.getContext());
        } else {
            mAdapter.setContext(inflater.getContext());
        }
        mListView.setAdapter(mAdapter);

        notifyPersonChanged();
        updateRefreshState();

        return mListView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        Person person = Application.getDb().getPersonDao().load(mPersonId);

        MenuItem assign = menu.add(Menu.NONE, R.id.action_assign, Menu.NONE, R.string.action_assign).setIcon(R.drawable.ic_action_assign);
        MenuItemCompat.setShowAsAction(assign, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItem label = menu.add(Menu.NONE, R.id.action_label, Menu.NONE, R.string.action_labels).setIcon(R.drawable.ic_action_labels);
        MenuItemCompat.setShowAsAction(label, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        if (person != null && !(person.isAdmin() && !Application.getSession().isAdmin())) {
            MenuItem permission = menu.add(Menu.NONE, R.id.action_permission, Menu.NONE, R.string.action_permissions).setIcon(R.drawable.ic_action_permissions);
            MenuItemCompat.setShowAsAction(permission, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        }

        MenuItem interaction = menu.add(Menu.NONE, R.id.action_interaction, Menu.NONE, R.string.action_record_interaction).setIcon(R.drawable.ic_action_interaction);
        MenuItemCompat.setShowAsAction(interaction, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        if (person != null && !person.isAdmin()) {
            MenuItem delete = menu.add(Menu.NONE, R.id.action_delete, Menu.NONE, R.string.action_delete).setIcon(R.drawable.ic_action_delete);
            MenuItemCompat.setShowAsAction(delete, MenuItemCompat.SHOW_AS_ACTION_NEVER);
        }

//      MenuItem archive = menu.add(Menu.NONE, R.id.action_archive, Menu.NONE, R.string.action_archive).setIcon(R.drawable.ic_action_archive);
//      MenuItemCompat.setShowAsAction(archive, MenuItemCompat.SHOW_AS_ACTION_NEVER);

        MenuItem email = menu.add(Menu.NONE, R.id.action_email, Menu.NONE, R.string.action_email).setIcon(R.drawable.ic_action_email);
        MenuItemCompat.setShowAsAction(email, MenuItemCompat.SHOW_AS_ACTION_NEVER);

        MenuItem text = menu.add(Menu.NONE, R.id.action_text, Menu.NONE, R.string.action_text).setIcon(R.drawable.ic_action_text);
        MenuItemCompat.setShowAsAction(text, MenuItemCompat.SHOW_AS_ACTION_NEVER);

        mRefeshMenuItem = menu.add(Menu.NONE, R.id.action_refresh, Menu.NONE, R.string.action_refresh).setIcon(R.drawable.ic_action_refresh_dark);
        MenuItemCompat.setShowAsAction(mRefeshMenuItem, MenuItemCompat.SHOW_AS_ACTION_NEVER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_assign:
                AssignmentDialogFragment.showForResult(getChildFragmentManager(), new long[]{mPersonId}, R.id.action_assign);
                return true;
            case R.id.action_label:
                BulkUpdateDialogFragment.showForResult(getChildFragmentManager(), BulkUpdateDialogFragment.TYPE_LABELS, mPersonId, R.id.action_label);
                return true;
            case R.id.action_permission:
                BulkUpdateDialogFragment.showForResult(getChildFragmentManager(), BulkUpdateDialogFragment.TYPE_PERMISSIONS, mPersonId, R.id.action_label);
                return true;
            case R.id.action_interaction:
                InteractionDialogFragment.showForResult(getChildFragmentManager(), mPersonId, null, R.id.action_interaction);
                return true;
            case R.id.action_delete:
                DeletePeopleDialogFragment.showForResult(getChildFragmentManager(), new long[]{mPersonId}, R.id.action_delete);
                return true;
            case R.id.action_email:
                IntentHelper.sendEmail(new long[]{mPersonId});
                return true;
            case R.id.action_text:
                IntentHelper.sendSms(new long[]{mPersonId});
                return true;
            case R.id.action_refresh:
                updatePerson();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onFragmentResult(int requestCode, int resultCode, Object data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case R.id.action_label:
                    clearObjectCache(R.id.tab_all);
                    notifyPersonChanged();
                    return true;
                case R.id.action_interaction:
                    clearObjectCache(R.id.tab_interactions);
                    clearObjectCache(R.id.tab_all);
                    notifyPersonChanged();
                    return true;
                case R.id.action_delete:
                    getHost().onBackPressed();
                    return true;
                case R.id.action_assign:
                case R.id.action_followup_status:
                    clearObjectCache(R.id.tab_info);
                    notifyPersonChanged();
                    return true;
            }
        }
        return super.onFragmentResult(requestCode, resultCode, data);
    }

    private void setupPager(Context context) {
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mSelectedPagerPage = position;
            }

            @Override
            public void onPageSelected(int position) {
                mSelectedPagerPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        if (mPagerAdapter == null) {
            mPagerAdapter = new HeaderPagerAdapter(getSupportActivity());
        } else {
            mPagerAdapter.setContext(context);
        }
        mPager.setAdapter(mPagerAdapter);
        mIndicator.setViewPager(mPager, mSelectedPagerPage);
    }

    public void setSelectedTab(int tabId) {
        mSelectedTab = tabId;

        try {
            mSelectTabTask.cancel(true);
        } catch (Exception e) { /* ignore */ }

        mSelectTabTask = new SafeAsyncTask<List<Object>>() {
            @Override
            public List<Object> call() throws Exception {
                return getCachedObjects(mSelectedTab);
            }

            @Override
            protected void onSuccess(List<Object> objects) throws Exception {
                synchronized (mAdapter.getLock()) {
                    mAdapter.setNotifyOnChange(false);
                    mAdapter.clear();
                    mAdapter.addAll(objects);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                Log.e(TAG, e.getMessage(), e);

                ExceptionHelper eh = new ExceptionHelper(e);
                eh.makeToast();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                mSelectTabTask = null;
                updateRefreshState();
            }
        };
        updateRefreshState();
        mSelectTabTask.execute();
    }

    @Override
    public void onTabSelected(int index, TabBar.TabBarButton button) {
        setSelectedTab(button.getId());
    }

    public void notifyPersonChanged() {
        Person person = Application.getDb().getPersonDao().load(mPersonId);
        if (person == null || mListView == null) return;

        // The avatar
        DisplayMetrics metrics = DisplayUtils.getRealDisplayMetrics(getSupportActivity());
        String picture = person.getPictureUrl(metrics.widthPixels, metrics.heightPixels);

        mAvatar.setEmptyImageResId(R.drawable.ic_default_contact_full);
        mAvatar.setErrorImageResId(R.drawable.ic_default_contact_full);
        mAvatar.setImageUrl(picture, mProfileImageLoader);

        mPagerAdapter.notifyPersonChanged();
        setSelectedTab(mSelectedTab);
    }

    public synchronized void clearObjectCache() {
        synchronized (mCachedObjects) {
            mCachedObjects.clear();
        }
    }

    public synchronized void clearObjectCache(int tab) {
        synchronized (mCachedObjects) {
            mCachedObjects.removeAll(tab);
        }
    }

    private List<Object> getCachedObjects(int tabId) {
        synchronized (mCachedObjects) {
            if (mCachedObjects.containsKey(tabId)) {
                return mCachedObjects.get(tabId);
            }
            switch (tabId) {
                case R.id.tab_info:
                    mCachedObjects.putAll(tabId, buildInfoTab());
                    break;
                case R.id.tab_interactions:
                    mCachedObjects.putAll(tabId, buildInteractionTab());
                    break;
                case R.id.tab_surveys:
                    mCachedObjects.putAll(tabId, buildSurveyTab());
                    break;
                case R.id.tab_all:
                    mCachedObjects.putAll(tabId, buildAllTab());
                    break;
            }
            return mCachedObjects.get(tabId);
        }
    }

    private List<Object> buildInfoTab() {
        List<Object> items = new ArrayList<Object>();

        Person person = Application.getDb().getPersonDao().load(mPersonId);

        if (person == null) return items;

        person.refresh();

        person.resetStatus();
        FollowupStatus status = person.getStatus(Application.getSession().getOrganizationId());
        if (status != null) {
            items.add(new InfoGroup(R.string.profile_group_followup_status));
            items.add(new InfoItem(status));
        }

        person.resetContactAssignments();
        ContactAssignment assignment = person.getContactAssignment(Application.getSession().getOrganizationId());
        if (assignment == null) assignment = new ContactAssignment();
        items.add(new InfoGroup(R.string.profile_group_assigned_to));
        items.add(new InfoItem(assignment));

        Gender gender = person.getGenderEnum();
        if (gender != null) {
            items.add(new InfoGroup(R.string.profile_group_gender));
            items.add(new InfoItem(gender));
        }

        person.resetEmailAddressList();
        List<EmailAddress> emails = person.getEmailAddressList();
        if (!emails.isEmpty()) {
            items.add(new InfoGroup(R.string.profile_group_email));
            for (EmailAddress email : emails) {
                items.add(new InfoItem(email));
            }
        }

        person.resetPhoneNumberList();
        List<PhoneNumber> phones = person.getPhoneNumberList();
        if (!phones.isEmpty()) {
            items.add(new InfoGroup(R.string.profile_group_phone));
            for (PhoneNumber phone : phones) {
                items.add(new InfoItem(phone));
            }
        }

        person.resetAddressList();
        List<Address> addresses = person.getAddressList();
        if (!addresses.isEmpty()) {
            items.add(new InfoGroup(R.string.profile_group_address));
            for (Address address : addresses) {
                items.add(new InfoItem(address));
            }
        }

        if (person.getFb_uid() != null && person.getFb_uid() > 0) {
            items.add(new InfoGroup(R.string.profile_group_facebook));
            items.add(new InfoItem(new InfoItem.FacebookLink(person.getFb_uid())));
        }

        if (items.isEmpty()) {
            items.add(new EmptyItem());
        }

        return items;
    }

    private List<TimestampedEntityItem> buildInteractionTab() {
        List<TimestampedEntityItem> items = new ArrayList<TimestampedEntityItem>();

        Person person = Application.getDb().getPersonDao().load(mPersonId);
        if (person == null) return items;

        person.resetReceivedInteractions();
        List<Interaction> interactions = person.getReceivedInteractions(Application.getSession().getOrganizationId());
        for (Interaction interaction : interactions) {
            interaction.invalidateViewCache();
            interaction.getViewCache();
            items.add(new InteractionItem(interaction));
        }

        if (items.isEmpty()) {
            items.add(new EmptyItem());
        }

        return SortUtils.sortTimestampedEnitiesByCreated(items, false);
    }

    private List<TimestampedEntityItem> buildSurveyTab() {
        List<TimestampedEntityItem> items = new ArrayList<TimestampedEntityItem>();

        Person person = Application.getDb().getPersonDao().load(mPersonId);
        if (person == null) return items;

        person.resetAnswerSheetList();
        List<AnswerSheet> sheets = person.getAnswerSheetList(Application.getSession().getOrganizationId());
        for (AnswerSheet sheet : sheets) {
            sheet.invalidateViewCache();
            sheet.getViewCache();
            items.add(new TimestampedEntityItem(sheet));
        }

        if (items.isEmpty()) {
            items.add(new EmptyItem());
        }

        return SortUtils.sortTimestampedEnitiesByCreated(items, false);
    }

    private List<TimestampedEntityItem> buildAllTab() {
        List<TimestampedEntityItem> items = new ArrayList<TimestampedEntityItem>();

        Person person = Application.getDb().getPersonDao().load(mPersonId);
        if (person == null) return items;

        List<Object> interactions = getCachedObjects(R.id.tab_interactions);
        for (Object interaction : interactions) {
            if (interaction instanceof TimestampedEntityItem && !(interaction instanceof EmptyItem)) {
                items.add((TimestampedEntityItem) interaction);
            }
        }
        List<Object> surveys = getCachedObjects(R.id.tab_surveys);
        for (Object survey : surveys) {
            if (survey instanceof TimestampedEntityItem && !(survey instanceof EmptyItem)) {
                items.add((TimestampedEntityItem) survey);
            }
        }

        List<OrganizationalLabel> labels = Application.getDb().getOrganizationalLabelDao().queryBuilder().where(
                OrganizationalLabelDao.Properties.Person_id.eq(person.getId()),
                OrganizationalLabelDao.Properties.Organization_id.eq(Application.getSession().getOrganizationId()),
                OrganizationalLabelDao.Properties.Removed_date.isNull()).list();

        for (OrganizationalLabel label : labels) {
            label.getViewCache();
            items.add(new TimestampedEntityItem(label));
        }

        if (items.isEmpty()) {
            items.add(new EmptyItem());
        }

        return SortUtils.sortTimestampedEnitiesByCreated(items, false);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Object item = adapterView.getItemAtPosition(i);
        if (item instanceof InfoItem && ((InfoItem) item).object instanceof FollowupStatus) {
            BulkUpdateDialogFragment.showForResult(getChildFragmentManager(), BulkUpdateDialogFragment.TYPE_FOLLOWUP_STATUS, mPersonId, R.id.action_followup_status);
        } else if (item instanceof InfoItem && ((InfoItem) item).object instanceof ContactAssignment) {
            AssignmentDialogFragment.showForResult(getChildFragmentManager(), new long[]{mPersonId}, R.id.action_assign);
        } else if (item instanceof Runnable) {
            ((Runnable) item).run();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        Object item = adapterView.getItemAtPosition(i);
        if (item instanceof InteractionItem) {
            InteractionDialogFragment.showForResult(getChildFragmentManager(), ((Interaction) ((InteractionItem) item).entity).getId(), R.id.action_interaction);
            return true;
        }
        return false;
    }

    public void updatePerson() {
        if (mPersonId <= 0) return;
        if (mUpdateTask != null) return;

        mUpdateTask = new SafeAsyncTask<Void>() {

            public ApiRequest<Person> mApiRequest;

            @Override
            public Void call() throws Exception {
                mApiRequest = Api.getPerson(mPersonId, ApiOptions.builder()
                        .include(Api.Include.addresses)
                        .include(Api.Include.phone_numbers)
                        .include(Api.Include.email_addresses)
                        .include(Api.Include.assigned_tos)
                        .include(Api.Include.answer_sheets)
                        .include(Api.Include.answers)
                        .include(Api.Include.organizational_labels)
                        .include(Api.Include.organizational_permission)
                        .include(Api.Include.interactions)
                        .build());
                mApiRequest.get();
                return null;
            }

            @Override
            protected void onSuccess(Void _) throws Exception {
                mLastUpdate = System.currentTimeMillis();
                clearObjectCache();
                notifyPersonChanged();
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                ExceptionHelper ex = new ExceptionHelper(e);
                ex.makeToast();
            }

            @Override
            protected void onFinally() throws RuntimeException {
                if (mApiRequest != null) {
                    mApiRequest.disconnect();
                }
                mUpdateTask = null;
                updateRefreshState();
            }
        };
        updateRefreshState();
        mUpdateTask.execute();
    }

    public void updateRefreshState() {
        if (mUpdateTask != null || mSelectTabTask != null) {
            if (getSupportActivity() != null) {
                getSupportActivity().setProgressBarIndeterminateVisibility(true);
            }
            if (mRefeshMenuItem != null) {
                mRefeshMenuItem.setEnabled(false);
            }
        } else {
            if (getSupportActivity() != null) {
                getSupportActivity().setProgressBarIndeterminateVisibility(false);
            }
            if (mRefeshMenuItem != null) {
                mRefeshMenuItem.setEnabled(true);
            }
        }
    }

    @Override
    public void onDestroy() {
        try {
            mSelectTabTask.cancel(true);
        } catch (Exception e) { /* ignore */ }
        try {
            mUpdateTask.cancel(true);
        } catch (Exception e) { /* ignore */ }
        super.onDestroy();
    }

    private static class InfoItem implements Runnable, ObjectArrayAdapter.SupportEnable {
        public final Object object;

        public InfoItem(Object object) {
            this.object = object;

            // initialize view caches
            if (object instanceof EmailAddress) {
                ((EmailAddress) object).getViewCache();
            } else if (object instanceof PhoneNumber) {
                ((PhoneNumber) object).getViewCache();
            } else if (object instanceof Address) {
                ((Address) object).getViewCache();
            }
        }

        @Override
        public void run() {
            if (getRunnable() != null) {
                getRunnable().run();
            }
        }

        @Override
        public boolean isEnabled() {
            if (object instanceof FollowupStatus) return true;
            if (object instanceof ContactAssignment) return true;
            return getRunnable() != null;
        }

        public Runnable getRunnable() {
            if (object instanceof EmailAddress) {
                return ((EmailAddress) object).getViewCache().onClick;
            } else if (object instanceof PhoneNumber) {
                return ((PhoneNumber) object).getViewCache().onClick;
            } else if (object instanceof Address) {
                return ((Address) object).getViewCache().onClick;
            } else if (object instanceof FacebookLink) {
                return ((FacebookLink) object).runnable;
            }
            return null;
        }

        public static class FacebookLink {
            public final long fbid;
            public final Runnable runnable;

            public FacebookLink(final long fbid) {
                this.fbid = fbid;

                if (fbid > 0) {
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            IntentHelper.openFacebookProfile(fbid);
                        }
                    };
                } else {
                    runnable = null;
                }
            }
        }
    }

    private static class TimestampedEntityItem implements TimestampedEntity, ObjectArrayAdapter.SupportEnable {

        public final TimestampedEntity entity;

        public TimestampedEntityItem(TimestampedEntity entity) {
            this.entity = entity;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public String getUpdated_at() {
            return entity.getUpdated_at();
        }

        @Override
        public void setUpdated_at(String updated_at) {
            entity.setUpdated_at(updated_at);
        }

        @Override
        public String getCreated_at() {
            return entity.getCreated_at();
        }

        @Override
        public void setCreated_at(String created_at) {
            entity.setCreated_at(created_at);
        }
    }

    private static class InteractionItem extends TimestampedEntityItem {

        private Boolean enabled;

        public InteractionItem(Interaction interaction) {
            super(interaction);
        }

        @Override
        public boolean isEnabled() {
            if (enabled == null) {
                GInteraction interaction = GInteraction.from((Interaction) entity);
                enabled = interaction.canEdit(Application.getSession().getPersonId()) || interaction.canDelete(Application.getSession().getPersonId());
            }
            return enabled;
        }
    }

    private static class EmptyItem extends TimestampedEntityItem {

        public EmptyItem() {
            super(null);
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public String getUpdated_at() {
            return null;
        }

        @Override
        public String getCreated_at() {
            return null;
        }
    }

    public static class ProfileObjectAdapter extends ObjectArrayAdapter<Object> {

        public ProfileObjectAdapter(Context context) {
            super(context, 4);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Object object = getItem(position);
            View view = convertView;
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                if (object instanceof InfoGroup) {
                    view = getLayoutInflater().inflate(R.layout.item_profile_info_group, parent, false);
                    holder.text1 = (TextView) view.findViewById(android.R.id.text1);
                } else if (object instanceof InfoItem) {
                    view = getLayoutInflater().inflate(R.layout.item_profile_info, parent, false);
                    holder.tagAbove = (TextView) view.findViewById(R.id.tag_above);
                    holder.tagInline = (TextView) view.findViewById(R.id.tag_inline);
                    holder.text1 = (TextView) view.findViewById(android.R.id.text1);
                    holder.text2 = (TextView) view.findViewById(android.R.id.text2);
                } else if (object instanceof EmptyItem) {
                    view = getLayoutInflater().inflate(R.layout.item_empty, parent, false);
                } else if (object instanceof TimestampedEntityItem) {
                    view = getLayoutInflater().inflate(R.layout.item_profile, parent, false);
                    holder.icon = (ImageView) view.findViewById(android.R.id.icon);
                    holder.timestamp = (TextView) view.findViewById(R.id.timestamp);
                    holder.visibility = (TextView) view.findViewById(R.id.visibility);
                    holder.text1 = (TextView) view.findViewById(android.R.id.text1);
                    holder.text2 = (TextView) view.findViewById(android.R.id.text2);
                    holder.updated = (TextView) view.findViewById(R.id.updated);
                    holder.divider = view.findViewById(R.id.divider);
                } else {
                    throw new RuntimeException("Unknown object type");
                }
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (object instanceof InfoGroup) {
                InfoGroup group = (InfoGroup) object;
                holder.text1.setText(group.mText1);
            } else if (object instanceof InfoItem) {
                InfoItem item = (InfoItem) object;

                holder.tagAbove.setVisibility(View.GONE);
                holder.text1.setVisibility(View.GONE);
                holder.text2.setVisibility(View.GONE);
                holder.tagInline.setVisibility(View.GONE);

                if (item.object instanceof FollowupStatus) {
                    safeSet(holder.text1, item.object.toString());
                } else if (item.object instanceof ContactAssignment) {
                    if (((ContactAssignment) item.object).getId() == null) {
                        safeSet(holder.text1, ResourceUtils.getString(R.string.assignment_unassigned));
                    } else {
                        Person leader = ((ContactAssignment) item.object).getPerson_assigned_to();
                        if (leader != null) {
                            safeSet(holder.text1, leader.getName());
                        } else {
                            safeSet(holder.text1, "Unknown Leader");
                        }
                    }
                } else if (item.object instanceof Gender) {
                    safeSet(holder.text1, item.object.toString());
                } else if (item.object instanceof EmailAddress) {
                    EmailAddress.EmailAddressViewCache cache = ((EmailAddress) item.object).getViewCache();

                    if (cache.primary) {
                        safeSet(holder.tagInline, ResourceUtils.getString(R.string.tag_primary));
                    }
                    safeSet(holder.text1, cache.email);
                } else if (item.object instanceof PhoneNumber) {
                    PhoneNumber.PhoneNumberViewCache cache = ((PhoneNumber) item.object).getViewCache();

                    safeSet(holder.tagInline, cache.location);
                    safeSet(holder.text1, cache.phone);
                } else if (item.object instanceof Address) {
                    Address.AddressViewCache cache = ((Address) item.object).getViewCache();

                    safeSet(holder.tagAbove, cache.type);
                    safeSet(holder.text1, cache.line1);
                    safeSet(holder.text2, cache.line2);
                } else if (item.object instanceof InfoItem.FacebookLink) {
                    if (((InfoItem.FacebookLink) item.object).fbid > 0) {
                        safeSet(holder.text1, ResourceUtils.getString(R.string.profile_view_facebook));
                    }
                }
            } else if (object instanceof TimestampedEntityItem && !(object instanceof EmptyItem)) {
                if (((TimestampedEntityItem) object).entity instanceof Interaction) {
                    Interaction.InteractionViewCache cache = ((Interaction) ((TimestampedEntityItem) object).entity).getViewCache();

                    safeSet(holder.icon, cache.iconResource);
                    safeSet(holder.timestamp, cache.timestamp);
                    safeSet(holder.visibility, cache.visibility);
                    safeSet(holder.text1, cache.action);
                    safeSet(holder.text2, cache.comment);
                    safeSet(holder.updated, cache.updated);
                } else if (((TimestampedEntityItem) object).entity instanceof AnswerSheet) {
                    AnswerSheet.AnswerSheetViewCache cache = ((AnswerSheet) ((TimestampedEntityItem) object).entity).getViewCache();

                    safeSet(holder.icon, R.drawable.ic_interaction_type_survey);
                    safeSet(holder.timestamp, cache.timestamp);
                    safeSet(holder.visibility, null);
                    safeSet(holder.text1, cache.action);
                    safeSet(holder.text2, cache.qa);
                    safeSet(holder.updated, null);

                } else if (((TimestampedEntityItem) object).entity instanceof OrganizationalLabel) {
                    OrganizationalLabel.OrganizationalLabelViewCache cache = ((OrganizationalLabel) ((TimestampedEntityItem) object).entity).getViewCache();

                    safeSet(holder.icon, R.drawable.ic_interaction_type_label);
                    safeSet(holder.timestamp, cache.timestamp);
                    safeSet(holder.visibility, null);
                    safeSet(holder.text1, cache.action);
                    safeSet(holder.text2, null);
                    safeSet(holder.updated, null);
                }
            }

            return view;
        }

        private void safeSet(ImageView image, int resourceId) {
            if (resourceId != 0) {
                image.setImageResource(resourceId);
                image.setVisibility(View.VISIBLE);
            } else {
                image.setVisibility(View.INVISIBLE);
            }
        }

        private void safeSet(TextView view, CharSequence text) {
            if (StringUtils.isNotEmpty(text)) {
                view.setText(text);
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }

        private static class ViewHolder {
            ImageView icon;
            TextView timestamp;
            TextView visibility;
            TextView text1;
            TextView text2;
            TextView updated;
            View divider;
            TextView tagAbove;
            TextView tagInline;
        }
    }

    private class HeaderPagerAdapter extends ViewArrayPagerAdapter {

        private Context mContext;
        private View mPageName;
        private View mPageLabels;
        private TextView mName;
        private TextView mLabels;

        public HeaderPagerAdapter(Context context) {
            setContext(context);
        }

        public void setContext(Context context) {
            mContext = context;
            rebuild();
        }

        public void rebuild() {
            setNotifyOnChange(false);
            clear();

            LayoutInflater inflater = LayoutInflater.from(mContext);
            mPageName = inflater.inflate(R.layout.fragment_profile_header_page_name);
            mName = (TextView) mPageName.findViewById(R.id.name);
            mPageLabels = inflater.inflate(R.layout.fragment_profile_header_page_labels);
            mLabels = (TextView) mPageLabels.findViewById(R.id.labels_text);
            notifyPersonChanged();

            addView(mPageName);
            addView(mPageLabels);
            notifyDataSetChanged();
        }

        public void notifyPersonChanged() {
            Person person = Application.getDb().getPersonDao().load(mPersonId);

            if (person != null) {
                mName.setText(person.getName());
                mName.setVisibility(View.VISIBLE);

                mLabels.setVisibility(View.VISIBLE);
                person.resetLabels();
                List<Long> labelList = person.getLables(Application.getSession().getOrganizationId());
                List<String> labelNames = new ArrayList<String>();
                for (long id : labelList) {
                    Label label = Application.getDb().getLabelDao().load(id);
                    if (label != null) {
                        labelNames.add(label.getTranslatedName());
                    }
                }
                if (labelNames.isEmpty()) {
                    mLabels.setText("No Labels");
                } else {
                    mLabels.setText(StringUtils.join(labelNames, "  •  "));
                }
                mLabels.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BulkUpdateDialogFragment.showForResult(getChildFragmentManager(), BulkUpdateDialogFragment.TYPE_LABELS, mPersonId, R.id.action_label);
                    }
                });
            } else {
                mName.setVisibility(View.GONE);
                mLabels.setVisibility(View.GONE);
            }
        }
    }

    private class InfoGroup extends ObjectArrayAdapter.DisabledItem {
        CharSequence mText1;

        public InfoGroup(int resourceId) {
            mText1 = ResourceUtils.getString(resourceId);
        }
    }
}