package com.missionhub.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.model.Organization;
import com.missionhub.model.Person;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.util.TreeDataStructure;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.TextView;

public class PreferencesFragment extends BaseFragment {

    private ImageView mPicture;
    private TextView mName;
    private Spinner mOrganizations;

    private OrganizationsAdapter mOrganizationsAdapter;
    private int mDefaultSpinnerIndex = 0;
    private DisplayImageOptions mImageLoaderOptions;

    public PreferencesFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        mImageLoaderOptions = new DisplayImageOptions.Builder().displayer(new FadeInBitmapDisplayer(200)).showImageForEmptyUri(R.drawable.default_contact).cacheInMemory(true).cacheOnDisc(true).build();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preferences, null);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Application.registerEventSubscriber(this, SessionOrganizationIdChanged.class);
    }

    @Override
    public void onStart() {
        super.onStart();

        Application.trackView("Preferences");
    }

    @Override
    public void onDetach() {
        Application.unregisterEventSubscriber(this);
        super.onDetach();
    }

    // TODO: uncomment
//    public void onEventMainThread(final SessionOrganizationIdChanged event) {
//        Log.e("EVENT", event.organizationId + "");
//
//
//        rebuildOrganizationList();
//    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPicture = (ImageView) view.findViewById(R.id.picture);
        mName = (TextView) view.findViewById(R.id.name);
        mOrganizations = (Spinner) view.findViewById(R.id.organizations);

        if (mOrganizationsAdapter == null) {
            mOrganizationsAdapter = new OrganizationsAdapter(getSupportActivity());
        } else {
            mOrganizationsAdapter.setContext(getSupportActivity());
        }

        mOrganizations.setAdapter(mOrganizationsAdapter);
        rebuildOrganizationList();
        mOrganizations.setOnItemSelectedListener(new OrganizationSelectedListener());

        final Person person = Session.getInstance().getPerson();
        mName.setText(person.getName());
        ImageLoader.getInstance().displayImage(person.getPictureUrl(100, 100), mPicture, mImageLoaderOptions);

    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private static class OrganizationsAdapter extends ObjectArrayAdapter {

        public OrganizationsAdapter(final Context context) {
            super(context);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final OrganizationItem item = (OrganizationItem) getItem(position);
            View view = convertView;

            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                view = getLayoutInflater().inflate(R.layout.item_preference_organization, null);
                holder.text = (TextView) view.findViewById(android.R.id.text1);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.text.setText(item.organization.getName());

            return view;
        }

        public String appendDepth(final String text, final int depth) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                sb.append("—");
            }
            sb.append(text);
            return sb.toString();
        }

        public class ViewHolder {
            TextView text;
        }

        @Override
        public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
            final View view = getView(position, convertView, parent);
            final OrganizationItem item = (OrganizationItem) getItem(position);

            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.text.setText(appendDepth(item.organization.getName(), item.depth));

            return view;
        }
    }

    private class OrganizationSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(final AdapterView<?> spinner, final View view, final int position, final long id) {
            final OrganizationItem item = (OrganizationItem) spinner.getItemAtPosition(position);
            Session.getInstance().setOrganizationId(item.organization.getId());
        }

        @Override
        public void onNothingSelected(final AdapterView<?> spinner) {
        }
    }

    private static class OrganizationItem {
        public final Organization organization;
        public final int depth;

        public OrganizationItem(final Organization organization, final int depth) {
            this.organization = organization;
            this.depth = depth;
        }
    }

    private synchronized void rebuildOrganizationList() {
        mOrganizationsAdapter.setNotifyOnChange(false);
        mOrganizationsAdapter.clear();

        rebuildOrganizationListR(Session.getInstance().getPerson().getOrganizationHierarchy(), 0);

        mOrganizationsAdapter.notifyDataSetChanged();
        mOrganizations.setSelection(mDefaultSpinnerIndex);
    }

    private synchronized void rebuildOrganizationListR(final TreeDataStructure<Long> tree, final int depth) {
        for (final TreeDataStructure<Long> subTree : tree.getSubTrees()) {
            final long organizationId = subTree.getHead();
            final Organization org = Application.getDb().getOrganizationDao().load(organizationId);
            if (org != null) {
                mOrganizationsAdapter.add(new OrganizationItem(org, depth));
                if (org.getId() == Session.getInstance().getOrganizationId()) {
                    mDefaultSpinnerIndex = mOrganizationsAdapter.getCount() - 1;
                }
                rebuildOrganizationListR(subTree, depth + 1);
            } else {
                rebuildOrganizationListR(subTree, depth);
            }
        }
    }
}