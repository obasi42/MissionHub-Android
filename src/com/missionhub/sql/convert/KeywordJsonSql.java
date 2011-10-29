package com.missionhub.sql.convert;

import java.util.Iterator;
import java.util.List;

import com.missionhub.Application;
import com.missionhub.api.ApiNotifier;
import com.missionhub.api.model.json.GKeyword;
import com.missionhub.api.model.sql.Keyword;
import com.missionhub.api.model.sql.KeywordDao;
import com.missionhub.api.model.sql.KeywordQuestion;
import com.missionhub.api.model.sql.KeywordQuestionDao;
import com.missionhub.api.model.sql.KeywordQuestionDao.Properties;

import android.content.Context;
import android.os.Bundle;

public class KeywordJsonSql {
	
	public static void update(Context context, int organizationId, GKeyword[] keywords) {
		update(context, organizationId, keywords, null);
	}
	
	public static void update(Context context, int organizationId, GKeyword[] keywords, String tag) {
		if (keywords == null) return;
		
		Application app = (Application) context.getApplicationContext();
		KeywordDao kd = app.getDbSession().getKeywordDao();
		KeywordQuestionDao kqd = app.getDbSession().getKeywordQuestionDao();
		
		for (GKeyword keyword : keywords) {
			Keyword k = kd.load(keyword.getKeyword_id());
			if (k == null) k = new Keyword();
			
			k.set_id(keyword.getKeyword_id());
			k.setName(keyword.getName());
			k.setOrganization_id(organizationId);
			
			// Delete all questions associations for this this keyword
			List<KeywordQuestion> keywordQuestions = kqd.queryBuilder().where(Properties.Keyword_id.eq(keyword.getKeyword_id())).list();
			Iterator<KeywordQuestion> itr = keywordQuestions.iterator();
			while (itr.hasNext()) {
				KeywordQuestion kq = itr.next();
				kqd.delete(kq);
				Bundle b = new Bundle();
				b.putLong("id", kq.get_id());
				b.putInt("organizationId", k.getOrganization_id());
				b.putInt("keywordId", kq.getKeyword_id());
				b.putInt("questionId", kq.getQuestion_id());
				if (tag != null) b.putString("tag", tag);
				app.getApiNotifier().postMessage(ApiNotifier.Type.DELETE_KEYWORD_QUESTION, b);
			}
			
			if (keyword.getQuestions() != null) {
				for (int question : keyword.getQuestions()) {
					KeywordQuestion kq = new KeywordQuestion();
					kq.setKeyword_id(keyword.getKeyword_id());
					kq.setQuestion_id(question);
					long id = kqd.insert(kq);
					
					Bundle b = new Bundle();
					b.putLong("id", id);
					b.putInt("organizationId", k.getOrganization_id());
					b.putInt("keywordId", kq.getKeyword_id());
					b.putInt("questionId", kq.getQuestion_id());
					if (tag != null) b.putString("tag", tag);
					app.getApiNotifier().postMessage(ApiNotifier.Type.UPDATE_KEYWORD_QUESTION, b);
				}
			}
			
			long id = kd.insertOrReplace(k);
			
			Bundle b = new Bundle();
			b.putLong("id", id);
			b.putInt("organizationId", k.getOrganization_id());
			if (tag != null) b.putString("tag", tag);
			app.getApiNotifier().postMessage(ApiNotifier.Type.UPDATE_KEYWORD, b);
		}
		
	}
}