package com.missionhub.sql.convert;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.json.GQuestion;
import com.missionhub.sql.Question;
import com.missionhub.sql.QuestionDao;

import android.content.Context;

public class QuestionJsonSql {
	
	public static void update(Context context, int organizationId, GQuestion[] questions) {
		if (questions == null) return;
		
		MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		QuestionDao qd = app.getDbSession().getQuestionDao();
		
		for (GQuestion question : questions) {
			Question q = qd.load(question.getId());
			if (q == null) q = new Question();
			q.set_id(question.getId());
			q.setLabel(question.getLabel());
			q.setRequired(question.getRequired());
			q.setStyle(question.getStyle());
			q.setKind(question.getKind());
			qd.insertOrReplace(q);
		}
	}
}