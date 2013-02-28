package com.missionhub.android.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.android.application.Application;
import com.missionhub.android.model.Group;
import com.missionhub.android.model.GroupDao;
import com.missionhub.android.util.U;

public class GGroup {

	public long id;
	public String name;
	public String location;
	public String meets;
	public String meeting_day;
	public Integer start_time;
	public Integer end_time;
	public long organization_id;
	public Boolean list_publicly;
	public Boolean approve_join_requests;
	public String created_at;
	public String updated_at;

	public static final Object lock = new Object();

	/**
	 * Saves the group to the SQLite database.
	 * 
	 * @param inTx
	 * @return
	 * @throws Exception
	 */
	public Group save(final boolean inTx) throws Exception {
		final Callable<Group> callable = new Callable<Group>() {
			@Override
			public Group call() throws Exception {
				synchronized (lock) {
					final GroupDao dao = Application.getDb().getGroupDao();

					final Group group = new Group();
					group.setId(id);
					group.setName(name);
					group.setLocation(location);
					group.setMeets(meets);
					group.setMeeting_day(meeting_day);
					group.setStart_time(start_time);
					group.setEnd_time(end_time);
					group.setOrganization_id(organization_id);
					group.setList_publicly(list_publicly);
					group.setApprove_join_requests(approve_join_requests);
					group.setCreated_at(U.parseISO8601(created_at));
					group.setUpdated_at(U.parseISO8601(updated_at));
					dao.insertOrReplace(group);

					return group;
				}
			}
		};
		if (inTx) {
			return callable.call();
		} else {
			return Application.getDb().callInTx(callable);
		}
	}

}