package com.missionhub.android;

import greendroid.widget.AsyncImageView;
import greendroid.widget.ItemAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.missionhub.R;
import com.missionhub.android.api.old.model.sql.Organization;
import com.missionhub.android.api.old.model.sql.OrganizationalRole;
import com.missionhub.android.api.old.model.sql.OrganizationalRoleDao.Properties;
import com.missionhub.android.app.MissionHubBaseActivity;
import com.missionhub.android.app.User;
import com.missionhub.android.broadcast.SessionReceiver;
import com.missionhub.android.ui.widget.item.ProfileOrganizationItem;
import com.missionhub.android.util.TreeDataStructure;

import de.greenrobot.dao.QueryBuilder;

@Deprecated
/**
 * The Profile Activity.
 */
public class ProfileActivity extends MissionHubBaseActivity {

	/** logging tag */
	public static final String TAG = ProfileActivity.class.getSimpleName();

	@InjectView(R.id.version)
	TextView mVersion;
	@InjectView(R.id.profile_picture)
	AsyncImageView mProfilePicture;
	@InjectView(R.id.spinner)
	Spinner mSpinner;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		try {
			setTitle(getSession().getUser().getPerson().getName());
			if (getSession().getUser().getPerson().getGender().equalsIgnoreCase("female")) {
				getSupportActionBar().setIcon(R.drawable.ic_female_user_info);
			} else {
				getSupportActionBar().setIcon(R.drawable.ic_male_user_info);
			}
		} catch (final Exception e) {}

		setupPicture();

		try {
			mVersion.setText(getString(R.string.profile_version) + " " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (final Exception e) {}

		final SessionReceiver sr = new SessionReceiver(this) {
			// @Override public void onUpdateOrganizationsSuccess() {
			// Log.e("UPDATE COMPLETE", "DONE");
			// unregister();
			// }
			//
			// @Override public void onUpdateOrganizationsError(final Throwable
			// throwable) {
			// Log.e("UPDATE ERROR", throwable.getMessage(), throwable);
			// unregister();
			// }
		};
		sr.register();

		prepareSpinner();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(R.string.action_refresh).setOnMenuItemClickListener(new RefreshOnMenuItemClickListener()).setIcon(R.drawable.ic_action_refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return super.onCreateOptionsMenu(menu);
	}

	private class RefreshOnMenuItemClickListener implements OnMenuItemClickListener {
		@Override
		public boolean onMenuItemClick(final MenuItem item) {
			Toast.makeText(getApplicationContext(), "Refresh", Toast.LENGTH_LONG).show();

			// TODO:

			// getSession().updatePerson();
			// getSession().updateOrganizations();

			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// final Intent intent = new Intent(this, DashboardActivity.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(intent);
			// return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void setupPicture() {
		if (getSession().getUser().getPerson().getGender().equalsIgnoreCase("female")) {
			mProfilePicture.setDefaultImageResource(R.drawable.facebook_female);
		} else {
			mProfilePicture.setDefaultImageResource(R.drawable.facebook_male);
		}
		if (getSession().getUser().getPerson().getPicture() != null) {
			mProfilePicture.setUrl(getSession().getUser().getPerson().getPicture() + "?type=large");
		}
	}

	public void prepareSpinner() {
		final QueryBuilder<OrganizationalRole> builder = getDbSession().getOrganizationalRoleDao().queryBuilder();
		final List<String> adminRoles = new ArrayList<String>();
		adminRoles.add(User.LABEL_ADMIN);
		adminRoles.add(User.LABEL_LEADER);
		builder.where(Properties.Person_id.eq(getSession().getPersonId()), Properties.Role.in(adminRoles));
		final List<OrganizationalRole> roles = builder.where(Properties.Person_id.eq(getSession().getPersonId()), Properties.Role.in(adminRoles)).list();

		// build a tree from organization ancestry
		final TreeDataStructure<Long> tree = new TreeDataStructure<Long>(0l);

		final Iterator<OrganizationalRole> roleItr = roles.iterator();
		while (roleItr.hasNext()) {
			final OrganizationalRole role = roleItr.next();
			final Organization org = role.getOrganization();
			if (role.getOrganization().getAncestry() != null) {
				TreeDataStructure<Long> parent = tree;
				for (final String ancestor : role.getOrganization().getAncestry().trim().split("/")) {
					final Long a = Long.parseLong(ancestor);
					if (parent.getTree(a) == null) {
						parent = parent.addLeaf(a);
					} else {
						parent = parent.getTree(a);
					}
				}
				if (parent.getTree(org.getId()) == null) {
					parent.addLeaf(org.getId());
				}
			}
		}

		Log.e("TREE", tree.toString());

		mSpinner.setAdapter(recursiveCreateAdapter(null, tree, 0));
		mSpinner.setOnItemSelectedListener(organizationSelectedListener);

		mSpinner.setSelection(defaultPosition);
	}

	private final OnItemSelectedListener organizationSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
			// TODO Auto-generated method stub
			final ProfileOrganizationItem item = (ProfileOrganizationItem) ((ItemAdapter) parent.getAdapter()).getItem(position);
			getSession().setOrganizationId(item.organizationId);
		}

		@Override
		public void onNothingSelected(final AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	};

	private int defaultPosition = 0;

	private ItemAdapter recursiveCreateAdapter(ItemAdapter adapter, final TreeDataStructure<Long> tree, final int depth) {
		if (adapter == null) {
			adapter = new ItemAdapter(this);
		}

		for (final TreeDataStructure<Long> subTree : tree.getSubTrees()) {
			final long organizationId = subTree.getHead();
			final Organization org = getDbSession().getOrganizationDao().load(organizationId);
			if (org != null && getSession().getUser().isAdminOrLeader(org.getId())) {
				adapter.add(new ProfileOrganizationItem(org.getName(), org.getId(), depth));
				if (org.getId() == getSession().getOrganizationId()) {
					defaultPosition = adapter.getCount() - 1;
				}
				recursiveCreateAdapter(adapter, subTree, depth + 1);
			} else {
				recursiveCreateAdapter(adapter, subTree, depth);
			}
		}

		return adapter;
	}

}