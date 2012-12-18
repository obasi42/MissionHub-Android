package com.missionhub.model;

import com.missionhub.model.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table ANSWER.
 */
public class Answer {

    private Long id;
    private Long answer_sheet_id;
    private Long question_id;
    private String value;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient AnswerDao myDao;

    private AnswerSheet answerSheet;
    private Long answerSheet__resolvedKey;

    private Question question;
    private Long question__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Answer() {
    }

    public Answer(Long id) {
        this.id = id;
    }

    public Answer(Long id, Long answer_sheet_id, Long question_id, String value) {
        this.id = id;
        this.answer_sheet_id = answer_sheet_id;
        this.question_id = question_id;
        this.value = value;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAnswerDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnswer_sheet_id() {
        return answer_sheet_id;
    }

    public void setAnswer_sheet_id(Long answer_sheet_id) {
        this.answer_sheet_id = answer_sheet_id;
    }

    public Long getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(Long question_id) {
        this.question_id = question_id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /** To-one relationship, resolved on first access. */
    public AnswerSheet getAnswerSheet() {
        if (answerSheet__resolvedKey == null || !answerSheet__resolvedKey.equals(answer_sheet_id)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AnswerSheetDao targetDao = daoSession.getAnswerSheetDao();
            answerSheet = targetDao.load(answer_sheet_id);
            answerSheet__resolvedKey = answer_sheet_id;
        }
        return answerSheet;
    }

    public void setAnswerSheet(AnswerSheet answerSheet) {
        this.answerSheet = answerSheet;
        answer_sheet_id = answerSheet == null ? null : answerSheet.getId();
        answerSheet__resolvedKey = answer_sheet_id;
    }

    /** To-one relationship, resolved on first access. */
    public Question getQuestion() {
        if (question__resolvedKey == null || !question__resolvedKey.equals(question_id)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            QuestionDao targetDao = daoSession.getQuestionDao();
            question = targetDao.load(question_id);
            question__resolvedKey = question_id;
        }
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
        question_id = question == null ? null : question.getId();
        question__resolvedKey = question_id;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
