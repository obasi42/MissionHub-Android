package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Group;
import com.missionhub.model.GroupLabel;
import com.missionhub.model.GroupLabelDao;
import com.missionhub.model.GroupLabels;
import com.missionhub.model.GroupLabelsDao;
import com.missionhub.util.U;

import de.greenrobot.dao.CloseableListIterator;
import de.greenrobot.dao.LazyList;

public class GGroupLabel {

	public long id;
	public String name;
	public long organization_id;
	public String ancestry;
	public String created_at;
	public String updated_at;
	public int group_labelings_count;

	/**
	 * Saves the labels for a group Make sure to wrap this in a transaction for performance
	 * 
	 * @param labels
	 * @param g
	 */
	public static void save(final GGroupLabel[] labels, final Group g) {
		if (labels == null || labels.length == 0 || g == null) return;

		final GroupLabelsDao glsd = Application.getDb().getGroupLabelsDao();
		final GroupLabelDao gld = Application.getDb().getGroupLabelDao();

		// delete the current group <--> group labels join
		final LazyList<GroupLabels> delGroupLabels = glsd.queryBuilder().where(com.missionhub.model.GroupLabelsDao.Properties.Group_id.eq(g.getId()))
				.listLazyUncached();
		final CloseableListIterator<GroupLabels> itr = delGroupLabels.listIteratorAutoClose();
		while (itr.hasNext()) {
			glsd.delete(itr.next());
		}

		for (final GGroupLabel label : labels) {
			if (label == null) continue;

			GroupLabel gl = gld.load(label.id);

			if (gl == null) {
				gl = new GroupLabel();
			}

			// create/update label
			if (!U.isNullEmptyNegative(label.id)) gl.setId(label.id);
			if (!U.isNullEmptyNegative(label.organization_id)) gl.setOrganization_id(label.organization_id);
			if (!U.isNullEmpty(label.name)) gl.setName(label.name);
			if (!U.isNullEmpty(label.ancestry)) gl.setAncestry(label.ancestry);
			if (!U.isNullEmpty(label.created_at)) gl.setCreated_at(U.parseUTC(label.created_at));
			if (!U.isNullEmpty(label.updated_at)) gl.setUpdated_at(U.parseUTC(label.updated_at));
			if (!U.isNullEmpty(label.group_labelings_count)) gl.setGroup_labelings_count(label.group_labelings_count);
			gld.insertOrReplace(gl);

			// a reference to group
			final GroupLabels gls = new GroupLabels();
			gls.setGroup_id(g.getId());
			gls.setLabel_id(gl.getId());
			glsd.insert(gls);
		}

		// TODO:
	}

}