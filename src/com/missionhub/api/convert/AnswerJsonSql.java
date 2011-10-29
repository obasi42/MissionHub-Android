package com.missionhub.api.convert;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.os.Bundle;

import com.missionhub.Application;
import com.missionhub.api.ApiNotifier;
import com.missionhub.api.model.json.GQA;
import com.missionhub.api.model.sql.Answer;
import com.missionhub.api.model.sql.AnswerDao;
import com.missionhub.api.model.sql.AnswerDao.Properties;

public class AnswerJsonSql {
	
	public static void update(Context context, int personId, int organizationId, GQA[] form) {
		update(context, personId, organizationId, form, null);
	}

	public static void update(Context context, int personId, int organizationId, GQA[] form, String tag) {
		if (form == null) return;
		
		Application app = (Application) context.getApplicationContext();
		AnswerDao ad = app.getDbSession().getAnswerDao();
		
		// Delete current answers in org
		List<Answer> currentAnswers = ad.queryBuilder().where(Properties.Person_id.eq(personId), Properties.Organization_id.eq(organizationId)).list();
		Iterator<Answer> itr = currentAnswers.iterator();
		while(itr.hasNext()) {
			Answer a = itr.next();
			ad.delete(a);
			
			Bundle b = new Bundle();
			b.putLong("id", a.get_id());
			b.putInt("personId", a.getPerson_id());
			if (tag != null) b.putString("tag", tag);
			app.getApiNotifier().postMessage(ApiNotifier.Type.DELETE_ANSWER, b);
		}
		
		for (GQA answer : form) {
			Answer a = new Answer();
			a.setOrganization_id(organizationId);
			a.setPerson_id(personId);
			a.setAnswer(answer.getA());
			a.setQuestion_id(answer.getQ());
			long id = ad.insert(a);
			
			Bundle b = new Bundle();
			b.putLong("id", id);
			b.putInt("personId", a.getPerson_id());
			if (tag != null) b.putString("tag", tag);
			app.getApiNotifier().postMessage(ApiNotifier.Type.UPDATE_ANSWER, b);
		}
	}
}