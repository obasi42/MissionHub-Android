package com.missionhub.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.Rejoicable;
import com.missionhub.model.RejoicableDao;
import com.missionhub.util.U;

public class GRejoicable {

	public long id;
	public String what;
	public String created_at;
	public String updated_at;
	public String deleted_at;

	public static final Object lock = new Object();

	/**
	 * Saves the rejoicable to the SQLite database.
	 * 
	 * @param inTx
	 * @return
	 * @throws Exception
	 */
	public Rejoicable save(final long personId, final long createdById, final long organizationId, final boolean inTx) throws Exception {
		final Callable<Rejoicable> callable = new Callable<Rejoicable>() {
			@Override
			public Rejoicable call() throws Exception {
				synchronized (lock) {
					final RejoicableDao dao = Application.getDb().getRejoicableDao();
					
					if (deleted_at != null) {
						dao.deleteByKey(id);
						return null;
					}

					Rejoicable rejoicable = new Rejoicable();
					rejoicable.setId(id);
					rejoicable.setPerson_id(personId);
					rejoicable.setCreated_by_id(createdById);
					rejoicable.setOrganization_id(organizationId);
					rejoicable.setWhat(what);
					rejoicable.setCreated_at(U.parseISO8601(created_at));
					rejoicable.setUpdated_at(U.parseISO8601(updated_at));
					dao.insertOrReplace(rejoicable);
					
					return rejoicable;
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