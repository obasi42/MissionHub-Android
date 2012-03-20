package com.missionhub.api.convert;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.model.GKeyword;
import com.missionhub.api.model.sql.DaoSession;
import com.missionhub.api.model.sql.Keyword;
import com.missionhub.api.model.sql.KeywordDao;
import com.missionhub.broadcast.GenericCUDEBroadcast;

public class KeywordJsonSql {

	/** logging tag */
	public final static String TAG = KeywordJsonSql.class.getSimpleName();

	public static void update(final Context context, final long organizationId, final GKeyword[] keywords, final String... categories) {
		update(context, organizationId, keywords, true, true, categories);
	}

	public static void update(final Context context, final long organizationId, final GKeyword[] keywords, final boolean threaded, final boolean notify, final String... categories) {
		try {
			privateUpdate(context, organizationId, keywords, threaded, notify, categories);
		} catch (final Exception e) {
			if (notify) {
				final long[] ids = new long[keywords.length];
				for (int i = 0; i < keywords.length; i++) {
					ids[i] = keywords[i].getId();
				}
				GenericCUDEBroadcast.broadcastError(context, Keyword.class, ids, e, categories);
			}
		}
	}

	private static void privateUpdate(final Context context, final long organizationId, final GKeyword[] keywords, final boolean threaded, final boolean notify,
			final String... categories) throws Exception {
		if (threaded) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// call update again, only this time we are in a thread, so
					// set threaded=false
					update(context, organizationId, keywords, false, notify, categories);
				}
			}).start();
			return;
		}

		final MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		final DaoSession session = app.getDbSession();
		final KeywordDao kd = session.getKeywordDao();

		final List<Keyword> words = new ArrayList<Keyword>();
		final ArrayList<Long> createdIds = new ArrayList<Long>();
		final ArrayList<Long> updatedIds = new ArrayList<Long>();
		for (final GKeyword keyword : keywords) {
			Keyword k = kd.load(keyword.getId());
			if (k == null) {
				k = new Keyword();
				createdIds.add(keyword.getId());
			} else {
				k.refresh();
				updatedIds.add(keyword.getId());
			}

			k.setId(keyword.getId());
			k.setOrganization_id(organizationId);

			if (keyword.getKeyword() != null) {
				k.setKeyword(keyword.getKeyword());
			}

			if (keyword.getState() != null) {
				k.setState(keyword.getState());
			}

			if (keyword.getQuestions() != null) {
				QuestionJsonSql.update(context, keyword.getId(), keyword.getQuestions(), false, false, categories);
			}

			words.add(k);
		}

		app.getDbSession().runInTx(new Runnable() {
			@Override
			public void run() {
				for (final Keyword word : words) {
					session.insertOrReplace(word);
				}
			}
		});

		if (notify) {
			GenericCUDEBroadcast.broadcastCreate(context, Keyword.class, createdIds, categories);
			GenericCUDEBroadcast.broadcastUpdate(context, Keyword.class, updatedIds, categories);
		}
	}
}