package com.missionhub.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.model.Person;
import com.missionhub.ui.AnimateOnceImageLoadingListener;
import com.missionhub.ui.ViewArrayPagerAdapter;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.ui.widget.ParallaxListView;
import com.missionhub.util.U;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.viewpagerindicator.CirclePageIndicator;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

public class ProfileFragment extends BaseFragment {

    private long mPersonId;

    private ParallaxListView mListView;
    private ObjectArrayAdapter mAdapter;

    private ImageView mAvatar;
    private ViewPager mPager;
    private CirclePageIndicator mIndicator;
    private HeaderPagerAdapter mPagerAdapter;
    private int mSelectedPagerPage = 0;

    private AnimateOnceImageLoadingListener mLoadingListener = new AnimateOnceImageLoadingListener(250);

    public ProfileFragment() {
    }

    public static ProfileFragment instantiate(final Person person) {
        return instantiate(person.getId());
    }

    public static ProfileFragment instantiate(final long personId) {
        final Bundle bundle = new Bundle();
        bundle.putLong("personId", personId);

        final ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!U.superGetRetainInstance(this)) {
            setRetainInstance(true);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        if (getArguments() != null) {
            mPersonId = getArguments().getLong("personId", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListView = (ParallaxListView) inflater.inflate(R.layout.fragment_profile);
        mListView.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);

        View headerView = inflater.inflate(R.layout.fragment_profile_header, mListView, false);
        mAvatar = (ImageView) headerView.findViewById(R.id.avatar);
        mPager = (ViewPager) headerView.findViewById(R.id.pager);
        mIndicator = (CirclePageIndicator) headerView.findViewById(R.id.indicator);

        setupPager(inflater.getContext());

        View buttonGroup = inflater.inflate(R.layout.fragment_profile_button_group, mListView, false);

        mListView.addHeaderView(headerView);
        mListView.setParallaxView(headerView);

        mListView.addHeaderView(buttonGroup);

        return mListView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (mAdapter == null) {
            mAdapter = new ProfileObjectAdapter(view.getContext());
            for (int i = 0; i < 50; i++) {
                mAdapter.add(new String("Item " + i));
            }
        } else {
            mAdapter.setContext(view.getContext());
        }
        mListView.setAdapter(mAdapter);

        notifyPersonChanged();
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

    public static class ProfileObjectAdapter extends ObjectArrayAdapter {

        public ProfileObjectAdapter(Context context) {
            super(context);
        }

        public ProfileObjectAdapter(Context context, int maxViewTypes) {
            super(context, maxViewTypes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = new TextView(getContext());
            view.setText((CharSequence) getItem(position));
            view.setMinHeight(100);
            return view;
        }
    }

    public void notifyPersonChanged() {
        Person person = Application.getDb().getPersonDao().load(mPersonId);

        if (person == null || mListView == null) return;

        mPagerAdapter.notifyPersonChanged();

        // The avatar
        DisplayMetrics metrics = U.getRealDisplayMetrics(getSupportActivity());
        String picture = U.getProfilePicture(person, metrics.widthPixels, metrics.heightPixels);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory()
                .cacheOnDisc()
                .showImageForEmptyUri(R.drawable.default_contact)
                .showImageOnFail(R.drawable.default_contact)
                .build();
        ImageLoader.getInstance().displayImage(picture, mAvatar, options, mLoadingListener);

        mPagerAdapter.notifyPersonChanged();
    }

    private class HeaderPagerAdapter extends ViewArrayPagerAdapter {

        private Context mContext;
        private View mPageName;
        private View mPageLabels;

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
            mPageLabels = inflater.inflate(R.layout.fragment_profile_header_page_labels);
            notifyPersonChanged();

            addView(mPageName);
            addView(mPageLabels);
            notifyDataSetChanged();
        }

        public void notifyPersonChanged() {
            Person person = Application.getDb().getPersonDao().load(mPersonId);

            TextView name = (TextView) mPageName.findViewById(R.id.name);
            if (person != null) {
                name.setText(person.getName());
                name.setVisibility(View.VISIBLE);
            } else {
                name.setVisibility(View.GONE);
            }

            TextView labels = (TextView) mPageLabels.findViewById(R.id.labels);
            if (person != null && !person.getLables(Session.getInstance().getOrganizationId()).isEmpty()) {
                labels.setText("TODO: list labels");
            } else {
                labels.setText("No Labels");
            }
        }
    }
}