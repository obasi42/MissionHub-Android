package com.missionhub.sql;

import com.missionhub.sql.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table KEYWORD_QUESTION (schema version 1).
 */
public class KeywordQuestion {

    private Integer _id;
    private Integer question_id;
    private Integer keyword_id;

    /** Used to resolve relations */
    private DaoSession daoSession;

    /** Used for active entity operations. */
    private KeywordQuestionDao myDao;

    private Question question;
    private Integer question__resolvedKey;

    private Keyword keyword;
    private Integer keyword__resolvedKey;


    public KeywordQuestion() {
    }

    public KeywordQuestion(Integer _id) {
        this._id = _id;
    }

    public KeywordQuestion(Integer _id, Integer question_id, Integer keyword_id) {
        this._id = _id;
        this.question_id = question_id;
        this.keyword_id = keyword_id;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getKeywordQuestionDao() : null;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public Integer getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(Integer question_id) {
        this.question_id = question_id;
    }

    public Integer getKeyword_id() {
        return keyword_id;
    }

    public void setKeyword_id(Integer keyword_id) {
        this.keyword_id = keyword_id;
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
        question_id = question == null ? null : question.get_id();
        question__resolvedKey = question_id;
    }

    /** To-one relationship, resolved on first access. */
    public Keyword getKeyword() {
        if (keyword__resolvedKey == null || !keyword__resolvedKey.equals(keyword_id)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            KeywordDao targetDao = daoSession.getKeywordDao();
            keyword = targetDao.load(keyword_id);
            keyword__resolvedKey = keyword_id;
        }
        return keyword;
    }

    public void setKeyword(Keyword keyword) {
        this.keyword = keyword;
        keyword_id = keyword == null ? null : keyword.get_id();
        keyword__resolvedKey = keyword_id;
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

}
