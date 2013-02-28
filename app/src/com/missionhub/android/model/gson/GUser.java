package com.missionhub.android.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.android.application.Application;
import com.missionhub.android.model.User;
import com.missionhub.android.model.UserDao;
import com.missionhub.android.util.U;

public class GUser {

	public long id;
	public Long primary_organization_id;
	public String created_at;
	public String updated_at;

	public static final Object lock = new Object();

	/**
	 * Saves the user to the SQLite database.
	 * 
	 * @param inTx
	 * @return
	 * @throws Exception
	 */
	public User save(final long personId, final boolean inTx) throws Exception {
		final Callable<User> callable = new Callable<User>() {
			@Override
			public User call() throws Exception {
				synchronized (lock) {
					final UserDao dao = Application.getDb().getUserDao();

					User user = dao.load(id);

					boolean insert = false;
					if (user == null) {
						user = new User();
						insert = true;
					}
					user.setId(id);
					user.setPerson_id(personId);
					user.setPrimary_organization_id(primary_organization_id);
					user.setCreated_at(U.parseISO8601(created_at));
					user.setUpdated_at(U.parseISO8601(updated_at));

					if (insert) {
						dao.insert(user);
					} else {
						dao.update(user);
					}

					return user;
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