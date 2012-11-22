package com.missionhub.model.gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.FollowupComment;

public class GMetaCommentTop {
	public GMeta meta;
	public GCommentTop[] followup_comments;

	public List<FollowupComment> save(final boolean inTx) throws Exception {
		final Callable<List<FollowupComment>> callable = new Callable<List<FollowupComment>>() {
			@Override
			public List<FollowupComment> call() throws Exception {
				final List<FollowupComment> comments = new ArrayList<FollowupComment>();

				for (final GCommentTop comment : followup_comments) {
					comments.add(comment.followup_comment.save(true));
				}

				return comments;
			}
		};
		if (!inTx) {
			return Application.getDb().callInTx(callable);
		} else {
			return callable.call();
		}
	}
}
