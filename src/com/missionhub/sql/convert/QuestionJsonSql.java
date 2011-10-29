package com.missionhub.sql.convert;

import com.missionhub.Application;
import com.missionhub.api.ApiNotifier;
import com.missionhub.api.model.json.GQuestion;
import com.missionhub.api.model.sql.Question;
import com.missionhub.api.model.sql.QuestionDao;

import android.content.Context;
import android.os.Bundle;

public class QuestionJsonSql {
	
	public static void update(Context context, int organizationId, GQuestion[] questions) {
		update(context, organizationId, questions, null);
	}
	
	public static void update(Context context, int organizationId, GQuestion[] questions, String tag) {
		if (questions == null) return;
		
		Application app = (Application) context.getApplicationContext();
		QuestionDao qd = app.getDbSession().getQuestionDao();
		
		for (GQuestion question : questions) {
			Question q = qd.load(question.getId());
			if (q == null) q = new Question();
			q.set_id(question.getId());
			q.setLabel(question.getLabel());
			q.setRequired(question.getRequired());
			q.setStyle(question.getStyle());
			q.setKind(question.getKind());
			long id = qd.insertOrReplace(q);
			
			Bundle b = new Bundle();
			b.putLong("id", id);
			if (tag != null) b.putString("tag", tag);
			app.getApiNotifier().postMessage(ApiNotifier.Type.UPDATE_QUESTION, b);
			
		}
	}
}