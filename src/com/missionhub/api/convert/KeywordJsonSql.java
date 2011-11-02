package com.missionhub.api.convert;

import com.missionhub.Application;
import com.missionhub.api.ApiNotifier;
import com.missionhub.api.model.json.GKeyword;
import com.missionhub.api.model.sql.Keyword;
import com.missionhub.api.model.sql.KeywordDao;

import android.content.Context;
import android.os.Bundle;

public class KeywordJsonSql {

	public static void update(Context context, int organizationId, GKeyword[] keywords) {
		update(context, organizationId, keywords, null);
	}

	public static void update(final Context context, final int organizationId, final GKeyword[] keywords, final String tag) {
		if (keywords == null)
			return;

		Thread t = new Thread(new Runnable() {
			public void run() {
				Application app = (Application) context.getApplicationContext();
				KeywordDao kd = app.getDbSession().getKeywordDao();

				for (GKeyword keyword : keywords) {
					Keyword k = kd.load(keyword.getId());
					if (k == null)
						k = new Keyword();

					k.set_id(keyword.getId());
					k.setKeyword(keyword.getKeyword());
					k.setOrganization_id(organizationId);
					k.setState(keyword.getState());
					
					if (keyword.getQuestions() != null) {
						QuestionJsonSql.update(context, keyword.getId(), keyword.getQuestions(), tag);
					}

					long id = kd.insertOrReplace(k);

					Bundle b = new Bundle();
					b.putLong("id", id);
					b.putInt("organizationId", k.getOrganization_id());
					if (tag != null)
						b.putString("tag", tag);
					app.getApiNotifier().postMessage(ApiNotifier.Type.UPDATE_KEYWORD, b);
				}
			}
		});
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();

	}
}