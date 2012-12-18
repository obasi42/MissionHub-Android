package com.missionhub.model.gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.FollowupComment;
import com.missionhub.model.FollowupCommentDao;
import com.missionhub.network.HttpParams;
import com.missionhub.util.U;

public class GFollowupComment {

	public long id;
	public long contact_id;
	public long commenter_id;
	public String comment;
	public String status;
	public long organization_id;
	public GRejoicable[] rejoicables;
	public String created_at;
	public String updated_at;
	public String deleted_at;

	public static final Object lock = new Object();
	public static final Object allLock = new Object();

	/**
	 * Saves the comment to the SQLite database.
	 * 
	 * @param inTx
	 * @return
	 * @throws Exception
	 */
	public FollowupComment save(final boolean inTx) throws Exception {
		final Callable<FollowupComment> callable = new Callable<FollowupComment>() {
			@Override
			public FollowupComment call() throws Exception {
				synchronized (lock) {
					final FollowupCommentDao dao = Application.getDb().getFollowupCommentDao();

					FollowupComment c = dao.load(id);

					if (deleted_at != null) {
						if (c != null) {
							c.deleteWithRelations();
						}
						return null;
					}

					boolean insert = false;
					if (c == null) {
						c = new FollowupComment();
						insert = true;
					}
					c.setId(id);
					c.setContact_id(contact_id);
					c.setCommenter_id(commenter_id);
					c.setComment(comment);
					c.setStatus(status);
					c.setOrganization_id(organization_id);
					c.setCreated_at(U.parseISO8601(created_at));
					c.setUpdated_at(U.parseISO8601(updated_at));

					if (insert) {
						dao.insert(c);
					} else {
						dao.update(c);
					}

					if (rejoicables != null) {
						for (final GRejoicable rejoicable : rejoicables) {
							rejoicable.save(contact_id, commenter_id, organization_id, true);
						}
					}

					return c;
				}
			}
		};
		if (inTx) {
			return callable.call();
		} else {
			return Application.getDb().callInTx(callable);
		}
	}

	/**
	 * Saves a list of followup comments
	 * 
	 * @param comments
	 * @param inTx
	 * @return list of saved comments
	 * @throws Exception
	 */
	public static List<FollowupComment> saveAll(final GFollowupComment[] comments, final boolean inTx) throws Exception {
		final Callable<List<FollowupComment>> callable = new Callable<List<FollowupComment>>() {
			@Override
			public List<FollowupComment> call() throws Exception {
				synchronized (allLock) {
					final List<FollowupComment> c = new ArrayList<FollowupComment>();
					for (final GFollowupComment comment : comments) {
						final FollowupComment a = comment.save(true);
						if (a != null) {
							c.add(a);
						}
					}
					return c;
				}
			}
		};
		if (inTx) {
			return callable.call();
		} else {
			return Application.getDb().callInTx(callable);
		}
	}

	public void toParams(final HttpParams params) {
		if (id > 0) {
			params.add("followup_comment[id]", id);
		}
		if (contact_id > 0) {
			params.add("followup_comment[contact_id]", contact_id);
		}
		if (U.isNullEmpty(comment)) {
			params.add("followup_comment[comment]", comment);
		}
		if (U.isNullEmpty(status)) {
			params.add("followup_comment[status]", status);
		}
		if (rejoicables != null) {
			for (final GRejoicable rejoice : rejoicables) {
				params.add("rejoicables[]", rejoice.what);
			}
		}
	}

}