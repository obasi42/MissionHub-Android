package com.missionhub.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.Answer;
import com.missionhub.model.AnswerDao;
import com.missionhub.model.Person;
import com.missionhub.util.U;

public class GQuestionAnswer {
	public long q;
	public String a;

	/**
	 * Saves an list of QuestionAnswers to a contact
	 * 
	 * @param form
	 * @param p
	 * @param request_org_id
	 * @throws Exception
	 */
	public static void save(final GQuestionAnswer[] form, final Person p, final String request_org_id, final boolean inTx) throws Exception {
		if (form == null || U.isNullEmptyNegative(form.length, p, request_org_id)) return;

		final Callable<Void> callable = new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				final long orgId = Long.parseLong(request_org_id);
				final AnswerDao qad = Application.getDb().getAnswerDao();

				// insert/update the answers
				for (final GQuestionAnswer answer : form) {
					if (answer == null) continue;

					Answer a = qad.queryBuilder().where(AnswerDao.Properties.Person_id.eq(p.getId()), AnswerDao.Properties.Question_id.eq(answer.q)).unique();

					if (a == null) {
						a = new Answer();
					}

					a.setQuestion_id(answer.q);
					a.setAnswer(answer.a);
					a.setPerson_id(p.getId());
					a.setOrganization_id(orgId);

					qad.insertOrReplace(a);
				}
				return null;
			}

		};
		if (!inTx) {
			Application.getDb().callInTx(callable);
		} else {
			callable.call();
		}
	}
}
