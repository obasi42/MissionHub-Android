package com.missionhub.fragment;

import roboguice.inject.InjectView;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.missionhub.R;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.application.Session.NoPersonException;
import com.missionhub.application.Session.SessionOrganizationIdChanged;
import com.missionhub.model.Organization;
import com.missionhub.ui.ObjectArrayAdapter;
import com.missionhub.util.TreeDataStructure;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class PreferencesFragment extends BaseFragment {

	@InjectView(R.id.picture) private ImageView mPicture;
	@InjectView(R.id.name) private TextView mName;
	@InjectView(R.id.organizations) private Spinner mOrganizations;

	private OrganizationsAdapter mOrganizationsAdapter;
	private int mDefaultSpinnerIndex = 0;
	private DisplayImageOptions mImageLoaderOptions;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);

		mImageLoaderOptions = new DisplayImageOptions.Builder().displayer(new FadeInBitmapDisplayer(200)).showImageForEmptyUri(R.drawable.default_contact).cacheInMemory().cacheOnDisc().build();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_preferences, null);
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Application.registerEventSubscriber(this, SessionOrganizationIdChanged.class);
	}

	@Override
	public void onDetach() {
		Application.unregisterEventSubscriber(this);
		super.onDetach();
	}

	public void onEventMainThread(final SessionOrganizationIdChanged event) {
		rebuildOrganizationList();
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (mOrganizationsAdapter == null) {
			mOrganizationsAdapter = new OrganizationsAdapter(getActivity());
		} else {
			mOrganizationsAdapter.setContext(getActivity());
		}

		mOrganizations.setAdapter(mOrganizationsAdapter);
		rebuildOrganizationList();
		mOrganizations.setOnItemSelectedListener(new OrganizationSelectedListener());

		try {
			mName.setText(Session.getInstance().getPerson().getName());
			ImageLoader.getInstance().displayImage(Session.getInstance().getPerson().getPicture(), mPicture, mImageLoaderOptions);
			// TODO: set picture;
		} catch (final NoPersonException e) { /* this should be impossible */}

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
			final StringBuffer sb = new StringBuffer();
			for (int i = 0; i < depth; i++) {
				sb.append("Ñ");
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

	private class OrganizationSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(final AdapterView<?> spinner, final View view, final int position, final long id) {
			final OrganizationItem item = (OrganizationItem) spinner.getItemAtPosition(position);
			try {
				Session.getInstance().setOrganizationId(item.organization.getId());
			} catch (final NoPersonException e) {
				/** ignore, shouldn't be possible to get here */
			}
		}

		@Override
		public void onNothingSelected(final AdapterView<?> spinner) {}
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

		try {
			rebuildOrganizationListR(Session.getInstance().getPerson().getOrganizationHierarchy(), 0);
		} catch (final NoPersonException e) {
			/** shouldn't be possible to get here */
		}

		mOrganizationsAdapter.notifyDataSetChanged();
		mOrganizations.setSelection(mDefaultSpinnerIndex);
	}

	private synchronized void rebuildOrganizationListR(final TreeDataStructure<Long> tree, final int depth) throws NoPersonException {
		for (final TreeDataStructure<Long> subTree : tree.getSubTrees()) {
			final long organizationId = subTree.getHead();
			final Organization org = Application.getDb().getOrganizationDao().load(organizationId);
			if (org != null && Session.getInstance().getPerson().isAdminOrLeader(org.getId())) {
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