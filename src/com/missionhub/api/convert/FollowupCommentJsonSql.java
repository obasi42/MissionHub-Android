package com.missionhub.api.convert;

import android.content.Context;
import android.os.Bundle;

import com.missionhub.Application;
import com.missionhub.api.ApiNotifier;
import com.missionhub.api.model.json.GComment;
import com.missionhub.api.model.json.GFCTop;
import com.missionhub.api.model.json.GPerson;
import com.missionhub.api.model.json.GRejoicable;
import com.missionhub.api.model.sql.FollowupComment;
import com.missionhub.api.model.sql.FollowupCommentDao;
import com.missionhub.api.model.sql.Rejoicable;
import com.missionhub.api.model.sql.RejoicableDao;
import com.missionhub.helper.Helper;

public class FollowupCommentJsonSql {

	public static void update(Context context, GFCTop[] comments) {
		update(context, comments, null);
	}

	public static void update(final Context context, final GFCTop[] comments, final String tag) {
		if (comments == null)
			return;

		Thread t = new Thread(new Runnable() {
			public void run() {
				Application app = (Application) context.getApplicationContext();
				FollowupCommentDao fcd = app.getDbSession().getFollowupCommentDao();
				RejoicableDao rd = app.getDbSession().getRejoicableDao();

				for (GFCTop commentTop : comments) {
					if (commentTop.getFollowup_comment() == null)
						break;
					final GComment comment = commentTop.getFollowup_comment().getComment();
					if (comment == null)
						break;
					final GRejoicable[] rejoicables = commentTop.getFollowup_comment().getRejoicables();
					if (rejoicables == null)
						break;

					FollowupComment c = fcd.load(comment.getId());
					if (c == null)
						c = new FollowupComment();

					c.set_id(comment.getId());

					if (comment.getComment() != null)
						c.setComment(comment.getComment());

					final GPerson commenter = comment.getCommenter();
					if (commenter != null) {
						PersonJsonSql.update(context, commenter, tag);
						c.setCommenter_id(commenter.getId());
					}

					c.setContact_id(comment.getContact_id());
					if (comment.getCreated_at() != null)
						c.setCreated_at(Helper.getDateFromUTCString(comment.getCreated_at()));

					if (comment.getDeleted_at() != null)
						c.setDeleted_at(Helper.getDateFromUTCString(comment.getDeleted_at()));

					c.setOrganization_id(comment.getOrganization_id());

					if (comment.getStatus() != null)
						c.setStatus(comment.getStatus());

					if (comment.getUpdated_at() != null)
						c.setUpdated_at(Helper.getDateFromUTCString(comment.getUpdated_at()));

					for (GRejoicable rejoicable : rejoicables) {
						Rejoicable r = new Rejoicable();
						r.set_id(rejoicable.getId());
						r.setComment_id(comment.getId());
						r.setWhat(rejoicable.getWhat());

						long id = rd.insertOrReplace(r);

						Bundle b = new Bundle();
						b.putLong("id", id);
						b.putInt("rejoicableId", r.get_id());
						b.putInt("commentId", r.getComment_id());
						if (tag != null)
							b.putString("tag", tag);
						app.getApiNotifier().postMessage(ApiNotifier.Type.UPDATE_REJOICABLE, b);
					}

					long id = fcd.insertOrReplace(c);

					Bundle b = new Bundle();
					b.putLong("id", id);
					b.putInt("commentId", comment.getId());
					if (tag != null)
						b.putString("tag", tag);
					app.getApiNotifier().postMessage(ApiNotifier.Type.UPDATE_FOLLOWUP_COMMENT, b);
				}

				Bundle b = new Bundle();
				if (tag != null)
					b.putString("tag", tag);
				app.getApiNotifier().postMessage(ApiNotifier.Type.UPDATE_FOLLOWUP_COMMENTS, b);
			}
		});
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}
}