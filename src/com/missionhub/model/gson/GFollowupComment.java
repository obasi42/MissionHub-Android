package com.missionhub.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.FollowupComment;
import com.missionhub.model.FollowupCommentDao;
import com.missionhub.model.Rejoicable;
import com.missionhub.model.RejoicableDao;
import com.missionhub.util.U;

public class GFollowupComment {
	public GComment comment;
	public GRejoicable[] rejoicables;

	public FollowupComment save(final boolean inTx) throws Exception {
		final Callable<FollowupComment> callable = new Callable<FollowupComment>() {
			@Override
			public FollowupComment call() throws Exception {
				final FollowupCommentDao fcd = Application.getDb().getFollowupCommentDao();
				final RejoicableDao rd = Application.getDb().getRejoicableDao();

				FollowupComment fc = fcd.load(comment.id);
				if (fc == null) {
					fc = new FollowupComment();
					fc.setId(comment.id);
				}

				fc.setOrganization_id(comment.organization_id);
				fc.setContact_id(comment.contact_id);

				if (comment.commenter != null) {
					fc.setCommenter_id(comment.commenter.save(true).getId());
				}

				if (!U.isNullEmpty(comment.comment)) fc.setComment(comment.comment);
				if (!U.isNullEmpty(comment.status)) fc.setStatus(comment.status);

				if (!U.isNullEmpty(comment.created_at)) fc.setCreated_at(U.parseUTC(comment.created_at));
				if (!U.isNullEmpty(comment.deleted_at)) fc.setDeleted_at(U.parseUTC(comment.deleted_at));
				if (!U.isNullEmpty(comment.updated_at)) fc.setUpdated_at(U.parseUTC(comment.updated_at));

				fcd.insertOrReplace(fc);

				// delete current rejoicables
				fc.resetRejoicables();
				for (final Rejoicable r : fc.getRejoicables()) {
					rd.delete(r);
				}

				// add new rejoicables
				for (final GRejoicable rejoicable : rejoicables) {
					if (rejoicable == null) continue;

					final Rejoicable r = new Rejoicable();
					r.setId(rejoicable.id);
					r.setComment_id(fc.getId());
					r.setWhat(rejoicable.what);
					rd.insertOrReplace(r);
				}

				return fc;
			}
		};
		if (!inTx) {
			return Application.getDb().callInTx(callable);
		} else {
			return callable.call();
		}
	}
}
