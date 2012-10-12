package com.missionhub.model.gson;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.missionhub.application.Application;
import com.missionhub.model.FollowupComment;
import com.missionhub.model.FollowupCommentDao;
import com.missionhub.model.Rejoicable;
import com.missionhub.model.RejoicableDao;
import com.missionhub.util.U;

public class GFollowupComment {
	public GComment comment;
	public GRejoicable[] rejoicables;

	public FutureTask<FollowupComment> save() {
		return save(true);
	}

	public FutureTask<FollowupComment> save(final boolean threaded) {
		final FutureTask<FollowupComment> task = new FutureTask<FollowupComment>(new Callable<FollowupComment>() {
			@Override
			public FollowupComment call() throws Exception {

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
							fc.setCommenter_id(comment.commenter.save(false).get().getId());
						}

						if (!U.isNullEmpty(comment.comment)) fc.setComment(comment.comment);
						if (!U.isNullEmpty(comment.status)) fc.setStatus(comment.status);

						if (!U.isNullEmpty(comment.created_at)) fc.setCreated_at(U.parseUTC(comment.created_at));
						if (!U.isNullEmpty(comment.deleted_at)) fc.setDeleted_at(U.parseUTC(comment.deleted_at));
						if (!U.isNullEmpty(comment.updated_at)) fc.setUpdated_at(U.parseUTC(comment.updated_at));

						fcd.insertOrReplace(fc);

						// delete current rejoicables
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
				if (threaded) {
					// since we are executing this, it is safe to assume it is the top call,
					// so we should wrap it in a transaction for performance
					return Application.getDb().callInTx(callable);
				} else {
					return callable.call();
				}
			}
		});

		if (threaded) {
			Application.getExecutor().execute(task);
		} else {
			task.run();
		}

		return task;
	}
}
