package com.missionhub.sql.convert;

import java.util.Iterator;
import java.util.List;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.json.GKeyword;
import com.missionhub.sql.Keyword;
import com.missionhub.sql.KeywordDao;
import com.missionhub.sql.KeywordQuestion;
import com.missionhub.sql.KeywordQuestionDao;
import com.missionhub.sql.KeywordQuestionDao.Properties;

import android.content.Context;

public class KeywordJsonSql {
	
	public static void update(Context context, int organizationId, GKeyword[] keywords) {
		if (keywords == null) return;
		
		MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
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
				kqd.delete(itr.next());
			}
			
			if (keyword.getQuestions() != null) {
				for (int question : keyword.getQuestions()) {
					KeywordQuestion kq = new KeywordQuestion();
					kq.setKeyword_id(keyword.getKeyword_id());
					kq.setQuestion_id(question);
					kqd.insert(kq);
				}
			}
			
			kd.insertOrReplace(k);
		}
		
	}
}