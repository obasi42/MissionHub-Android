package com.missionhub.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.IdentityScopeType;

import com.missionhub.sql.Person;
import com.missionhub.sql.Assignment;
import com.missionhub.sql.Interest;
import com.missionhub.sql.Education;
import com.missionhub.sql.Location;
import com.missionhub.sql.OrganizationalRole;
import com.missionhub.sql.FollowupComment;
import com.missionhub.sql.Rejoicable;
import com.missionhub.sql.Organization;
import com.missionhub.sql.Keyword;
import com.missionhub.sql.Question;
import com.missionhub.sql.KeywordQuestion;
import com.missionhub.sql.Answer;
import com.missionhub.sql.QuestionChoice;

import com.missionhub.sql.PersonDao;
import com.missionhub.sql.AssignmentDao;
import com.missionhub.sql.InterestDao;
import com.missionhub.sql.EducationDao;
import com.missionhub.sql.LocationDao;
import com.missionhub.sql.OrganizationalRoleDao;
import com.missionhub.sql.FollowupCommentDao;
import com.missionhub.sql.RejoicableDao;
import com.missionhub.sql.OrganizationDao;
import com.missionhub.sql.KeywordDao;
import com.missionhub.sql.QuestionDao;
import com.missionhub.sql.KeywordQuestionDao;
import com.missionhub.sql.AnswerDao;
import com.missionhub.sql.QuestionChoiceDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig personDaoConfig;
    private final DaoConfig assignmentDaoConfig;
    private final DaoConfig interestDaoConfig;
    private final DaoConfig educationDaoConfig;
    private final DaoConfig locationDaoConfig;
    private final DaoConfig organizationalRoleDaoConfig;
    private final DaoConfig followupCommentDaoConfig;
    private final DaoConfig rejoicableDaoConfig;
    private final DaoConfig organizationDaoConfig;
    private final DaoConfig keywordDaoConfig;
    private final DaoConfig questionDaoConfig;
    private final DaoConfig keywordQuestionDaoConfig;
    private final DaoConfig answerDaoConfig;
    private final DaoConfig questionChoiceDaoConfig;

    private final PersonDao personDao;
    private final AssignmentDao assignmentDao;
    private final InterestDao interestDao;
    private final EducationDao educationDao;
    private final LocationDao locationDao;
    private final OrganizationalRoleDao organizationalRoleDao;
    private final FollowupCommentDao followupCommentDao;
    private final RejoicableDao rejoicableDao;
    private final OrganizationDao organizationDao;
    private final KeywordDao keywordDao;
    private final QuestionDao questionDao;
    private final KeywordQuestionDao keywordQuestionDao;
    private final AnswerDao answerDao;
    private final QuestionChoiceDao questionChoiceDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        personDaoConfig = daoConfigMap.get(PersonDao.class).clone();
        personDaoConfig.initIdentityScope(type);

        assignmentDaoConfig = daoConfigMap.get(AssignmentDao.class).clone();
        assignmentDaoConfig.initIdentityScope(type);

        interestDaoConfig = daoConfigMap.get(InterestDao.class).clone();
        interestDaoConfig.initIdentityScope(type);

        educationDaoConfig = daoConfigMap.get(EducationDao.class).clone();
        educationDaoConfig.initIdentityScope(type);

        locationDaoConfig = daoConfigMap.get(LocationDao.class).clone();
        locationDaoConfig.initIdentityScope(type);

        organizationalRoleDaoConfig = daoConfigMap.get(OrganizationalRoleDao.class).clone();
        organizationalRoleDaoConfig.initIdentityScope(type);

        followupCommentDaoConfig = daoConfigMap.get(FollowupCommentDao.class).clone();
        followupCommentDaoConfig.initIdentityScope(type);

        rejoicableDaoConfig = daoConfigMap.get(RejoicableDao.class).clone();
        rejoicableDaoConfig.initIdentityScope(type);

        organizationDaoConfig = daoConfigMap.get(OrganizationDao.class).clone();
        organizationDaoConfig.initIdentityScope(type);

        keywordDaoConfig = daoConfigMap.get(KeywordDao.class).clone();
        keywordDaoConfig.initIdentityScope(type);

        questionDaoConfig = daoConfigMap.get(QuestionDao.class).clone();
        questionDaoConfig.initIdentityScope(type);

        keywordQuestionDaoConfig = daoConfigMap.get(KeywordQuestionDao.class).clone();
        keywordQuestionDaoConfig.initIdentityScope(type);

        answerDaoConfig = daoConfigMap.get(AnswerDao.class).clone();
        answerDaoConfig.initIdentityScope(type);

        questionChoiceDaoConfig = daoConfigMap.get(QuestionChoiceDao.class).clone();
        questionChoiceDaoConfig.initIdentityScope(type);

        personDao = new PersonDao(personDaoConfig, this);
        assignmentDao = new AssignmentDao(assignmentDaoConfig, this);
        interestDao = new InterestDao(interestDaoConfig, this);
        educationDao = new EducationDao(educationDaoConfig, this);
        locationDao = new LocationDao(locationDaoConfig, this);
        organizationalRoleDao = new OrganizationalRoleDao(organizationalRoleDaoConfig, this);
        followupCommentDao = new FollowupCommentDao(followupCommentDaoConfig, this);
        rejoicableDao = new RejoicableDao(rejoicableDaoConfig, this);
        organizationDao = new OrganizationDao(organizationDaoConfig, this);
        keywordDao = new KeywordDao(keywordDaoConfig, this);
        questionDao = new QuestionDao(questionDaoConfig, this);
        keywordQuestionDao = new KeywordQuestionDao(keywordQuestionDaoConfig, this);
        answerDao = new AnswerDao(answerDaoConfig, this);
        questionChoiceDao = new QuestionChoiceDao(questionChoiceDaoConfig, this);

        registerDao(Person.class, personDao);
        registerDao(Assignment.class, assignmentDao);
        registerDao(Interest.class, interestDao);
        registerDao(Education.class, educationDao);
        registerDao(Location.class, locationDao);
        registerDao(OrganizationalRole.class, organizationalRoleDao);
        registerDao(FollowupComment.class, followupCommentDao);
        registerDao(Rejoicable.class, rejoicableDao);
        registerDao(Organization.class, organizationDao);
        registerDao(Keyword.class, keywordDao);
        registerDao(Question.class, questionDao);
        registerDao(KeywordQuestion.class, keywordQuestionDao);
        registerDao(Answer.class, answerDao);
        registerDao(QuestionChoice.class, questionChoiceDao);
    }
    
    public void clear() {
        personDaoConfig.getIdentityScope().clear();
        assignmentDaoConfig.getIdentityScope().clear();
        interestDaoConfig.getIdentityScope().clear();
        educationDaoConfig.getIdentityScope().clear();
        locationDaoConfig.getIdentityScope().clear();
        organizationalRoleDaoConfig.getIdentityScope().clear();
        followupCommentDaoConfig.getIdentityScope().clear();
        rejoicableDaoConfig.getIdentityScope().clear();
        organizationDaoConfig.getIdentityScope().clear();
        keywordDaoConfig.getIdentityScope().clear();
        questionDaoConfig.getIdentityScope().clear();
        keywordQuestionDaoConfig.getIdentityScope().clear();
        answerDaoConfig.getIdentityScope().clear();
        questionChoiceDaoConfig.getIdentityScope().clear();
    }

    public PersonDao getPersonDao() {
        return personDao;
    }

    public AssignmentDao getAssignmentDao() {
        return assignmentDao;
    }

    public InterestDao getInterestDao() {
        return interestDao;
    }

    public EducationDao getEducationDao() {
        return educationDao;
    }

    public LocationDao getLocationDao() {
        return locationDao;
    }

    public OrganizationalRoleDao getOrganizationalRoleDao() {
        return organizationalRoleDao;
    }

    public FollowupCommentDao getFollowupCommentDao() {
        return followupCommentDao;
    }

    public RejoicableDao getRejoicableDao() {
        return rejoicableDao;
    }

    public OrganizationDao getOrganizationDao() {
        return organizationDao;
    }

    public KeywordDao getKeywordDao() {
        return keywordDao;
    }

    public QuestionDao getQuestionDao() {
        return questionDao;
    }

    public KeywordQuestionDao getKeywordQuestionDao() {
        return keywordQuestionDao;
    }

    public AnswerDao getAnswerDao() {
        return answerDao;
    }

    public QuestionChoiceDao getQuestionChoiceDao() {
        return questionChoiceDao;
    }

}
