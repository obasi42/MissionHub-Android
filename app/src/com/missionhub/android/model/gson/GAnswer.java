package com.missionhub.android.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.android.application.Application;
import com.missionhub.android.model.Answer;
import com.missionhub.android.model.AnswerDao;

public class GAnswer {

	public long id;
	public long question_id;
	public String value;

	public static final Object lock = new Object();

	/**
	 * Saves the current answer to the SQLite database.
	 * 
	 * @param inTx
	 * @return saved current address
	 * @throws Exception
	 */
	public Answer save(final long answerSheetId, final boolean inTx) throws Exception {
		final Callable<Answer> callable = new Callable<Answer>() {
			@Override
			public Answer call() throws Exception {
				synchronized (lock) {
					final AnswerDao dao = Application.getDb().getAnswerDao();

					final Answer answer = new Answer();
					answer.setId(id);
					answer.setAnswer_sheet_id(answerSheetId);
					answer.setQuestion_id(question_id);
					answer.setValue(value);
					dao.insertOrReplace(answer);

					return answer;
				}
			}
		};
		if (inTx) {
			return callable.call();
		} else {
			return Application.getDb().callInTx(callable);
		}
	}

}