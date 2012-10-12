package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.GroupMembership;
import com.missionhub.model.GroupMembershipDao;
import com.missionhub.model.Person;
import com.missionhub.util.U;

import de.greenrobot.dao.CloseableListIterator;
import de.greenrobot.dao.LazyList;

public class GGroupMembership {

	public long group_id;
	public String name;
	public String role;

	/**
	 * Saves the memberships for a person This is not in a transaction, make sure to wrap in a tx block.
	 * 
	 * @param group_memberships
	 * @param p
	 */
	public static void save(final GGroupMembership[] group_memberships, final Person p) {
		final GroupMembershipDao gmd = Application.getDb().getGroupMembershipDao();

		// delete current records as we don't have stable id's currently
		final LazyList<GroupMembership> delMemberships = gmd.queryBuilder().where(com.missionhub.model.GroupMembershipDao.Properties.Person_id.eq(p.getId()))
				.listLazyUncached();
		final CloseableListIterator<GroupMembership> itr = delMemberships.listIteratorAutoClose();
		while (itr.hasNext()) {
			final GroupMembership membership = itr.next();
			gmd.delete(membership);
		}

		// insert the new records
		for (final GGroupMembership membership : group_memberships) {
			if (membership == null) continue;

			final GGroup group = new GGroup();
			group.id = membership.group_id;
			group.name = membership.name;
			group.save(false);

			final GroupMembership gm = new GroupMembership();
			if (!U.isNullEmpty(membership.group_id)) gm.setGroup_id(membership.group_id);
			if (!U.isNullEmpty(membership.name)) gm.setName(membership.name);
			if (!U.isNullEmptyNegative(p, p.getId())) gm.setPerson_id(p.getId());
			if (!U.isNullEmpty(membership.role)) gm.setRole(membership.role);
			gmd.insert(gm);
		}
	}

}