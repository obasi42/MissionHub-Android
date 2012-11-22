package com.missionhub.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.Group;
import com.missionhub.model.GroupDao;
import com.missionhub.util.U;

public class GGroup {

	public long id;
	public String name;
	public String location;
	public String meets;
	public int start_time;
	public int end_time;
	public long organization_id;
	public String created_at;
	public String updated_at;
	public boolean list_publicly;
	public boolean approve_join_requests;
	public GGroupLabel[] labels;

	/**
	 * Saves a group to the local db.
	 */
	public Group save(final boolean inTx) throws Exception {
		final Callable<Group> callable = new Callable<Group>() {
			@Override
			public Group call() throws Exception {
				final GroupDao gd = Application.getDb().getGroupDao();

				Group g = gd.load(id);
				if (g == null) {
					g = new Group();
					g.setId(id);
					gd.insert(g);
				}

				if (!U.isNullEmpty(name)) g.setName(name);
				if (!U.isNullEmpty(location)) g.setLocation(location);
				if (!U.isNullEmpty(meets)) g.setMeets(meets);
				if (!U.isNullEmptyNegative(start_time)) g.setStart_time(start_time);
				if (!U.isNullEmptyNegative(end_time)) g.setEnd_time(end_time);
				if (!U.isNullEmptyNegative(organization_id)) g.setOrganization_id(organization_id);
				if (!U.isNullEmpty(created_at)) g.setCreated_at(U.parseUTC(created_at));
				if (!U.isNullEmpty(updated_at)) g.setCreated_at(U.parseUTC(updated_at));
				g.setList_publicly(list_publicly);
				g.setApprove_join_requests(approve_join_requests);

				GGroupLabel.save(labels, g, true);

				gd.update(g);

				// Application.postEvent(new GroupUpdatedEvent(g));

				return g;
			}
		};
		if (!inTx) {
			return Application.getDb().callInTx(callable);
		} else {
			return callable.call();
		}
	}

	/**
	 * Event posted when a group is updated
	 */
	public static class GroupUpdatedEvent {

		public Group group;

		public GroupUpdatedEvent(final Group g) {
			this.group = g;
		}

	}

}