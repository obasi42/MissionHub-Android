package com.missionhub.api.convert;

import java.util.Iterator;
import java.util.List;

import com.missionhub.Application;
import com.missionhub.api.ApiNotifier;
import com.missionhub.api.model.json.GQuestion;
import com.missionhub.api.model.sql.Question;
import com.missionhub.api.model.sql.QuestionChoice;
import com.missionhub.api.model.sql.QuestionChoiceDao;
import com.missionhub.api.model.sql.QuestionChoiceDao.Properties;
import com.missionhub.api.model.sql.QuestionDao;

import android.content.Context;
import android.os.Bundle;

public class QuestionJsonSql {

	public static void update(Context context, int keywordId, GQuestion[] questions) {
		update(context, keywordId, questions, null);
	}

	public static void update(final Context context, final int keywordId, final GQuestion[] questions, final String tag) {
		if (questions == null)
			return;

		Thread t = new Thread(new Runnable() {
			public void run() {
				Application app = (Application) context.getApplicationContext();
				QuestionDao qd = app.getDbSession().getQuestionDao();
				QuestionChoiceDao qcd = app.getDbSession().getQuestionChoiceDao();

				for (GQuestion question : questions) {
					Question q = qd.load(question.getId());
					if (q == null)
						q = new Question();
					q.set_id(question.getId());
					q.setKeyword_id(keywordId);

					if (question.getLabel() != null)
						q.setLabel(question.getLabel());

					q.setRequired(question.getRequired());

					if (question.getStyle() != null)
						q.setStyle(question.getStyle());

					if (question.getKind() != null)
						q.setKind(question.getKind());

					if (question.getChoices() != null) {
						// Delete all questions choices for this question
						List<QuestionChoice> questionChoices = qcd.queryBuilder().where(Properties.Question_id.eq(question.getId())).list();
						Iterator<QuestionChoice> itr = questionChoices.iterator();
						while (itr.hasNext()) {
							QuestionChoice qc = itr.next();
							qcd.delete(qc);
							Bundle b = new Bundle();
							b.putLong("id", qc.get_id());
							b.putInt("questionId", qc.getQuestion_id());
							if (tag != null)
								b.putString("tag", tag);
							app.getApiNotifier().postMessage(ApiNotifier.Type.DELETE_QUESTION_CHOICE, b);
						}

						for (String choice : question.getChoices()) {
							QuestionChoice qc = new QuestionChoice();
							qc.setChoice(choice);
							qc.setQuestion_id(question.getId());
							long id = qcd.insert(qc);

							Bundle b = new Bundle();
							b.putLong("id", id);
							b.putInt("questionId", qc.getQuestion_id());
							if (tag != null)
								b.putString("tag", tag);
							app.getApiNotifier().postMessage(ApiNotifier.Type.UPDATE_QUESTION_CHOICE, b);
						}
					}

					long id = qd.insertOrReplace(q);

					Bundle b = new Bundle();
					b.putLong("id", id);
					if (tag != null)
						b.putString("tag", tag);
					app.getApiNotifier().postMessage(ApiNotifier.Type.UPDATE_QUESTION, b);
				}
			}
		});
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}
}