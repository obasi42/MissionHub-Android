package com.missionhub.model.gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.missionhub.application.Application;
import com.missionhub.model.FollowupComment;

public class GMetaCommentTop {
	public GMeta meta;
	public GCommentTop[] followup_comments;

	public FutureTask<List<FollowupComment>> save() {
		return save(true);
	}

	public FutureTask<List<FollowupComment>> save(final boolean threaded) {
		final FutureTask<List<FollowupComment>> task = new FutureTask<List<FollowupComment>>(new Callable<List<FollowupComment>>() {
			@Override
			public List<FollowupComment> call() throws Exception {

				final Callable<List<FollowupComment>> callable = new Callable<List<FollowupComment>>() {
					@Override
					public List<FollowupComment> call() throws Exception {
						final List<FollowupComment> comments = new ArrayList<FollowupComment>();

						for (final GCommentTop comment : followup_comments) {
							comments.add(comment.followup_comment.save(false).get());
						}

						return comments;
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
