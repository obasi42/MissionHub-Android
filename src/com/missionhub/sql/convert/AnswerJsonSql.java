package com.missionhub.sql.convert;

import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.missionhub.MissionHubApplication;
import com.missionhub.api.json.GQA;
import com.missionhub.sql.Answer;
import com.missionhub.sql.AnswerDao;
import com.missionhub.sql.AnswerDao.Properties;

public class AnswerJsonSql {

	public static void update(Context context, int personId, int organizationId, GQA[] form) {
		if (form == null) return;
		
		MissionHubApplication app = (MissionHubApplication) context.getApplicationContext();
		AnswerDao ad = app.getDbSession().getAnswerDao();
		
		// Delete current answers in org
		List<Answer> currentAnswers = ad.queryBuilder().where(Properties.Person_id.eq(personId), Properties.Organization_id.eq(organizationId)).list();
		Iterator<Answer> itr = currentAnswers.iterator();
		while(itr.hasNext()) {
			ad.delete(itr.next());
		}
		
		for (GQA answer : form) {
			Answer a = new Answer();
			a.setOrganization_id(organizationId);
			a.setPerson_id(personId);
			a.setAnswer(answer.getA());
			a.setQuestion_id(answer.getQ());
			ad.insert(a);
		}
	}
	
}