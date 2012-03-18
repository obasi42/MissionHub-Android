package com.missionhub.api.model.sql;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.IdentityScopeType;

import com.missionhub.api.model.sql.Person;
import com.missionhub.api.model.sql.Assignment;
import com.missionhub.api.model.sql.Interest;
import com.missionhub.api.model.sql.Education;
import com.missionhub.api.model.sql.Location;
import com.missionhub.api.model.sql.Organization;
import com.missionhub.api.model.sql.OrganizationalRole;
import com.missionhub.api.model.sql.Group;
import com.missionhub.api.model.sql.Label;
import com.missionhub.api.model.sql.GroupMembership;
import com.missionhub.api.model.sql.FollowupComment;
import com.missionhub.api.model.sql.Rejoicable;
import com.missionhub.api.model.sql.Keyword;
import com.missionhub.api.model.sql.Question;
import com.missionhub.api.model.sql.Answer;
import com.missionhub.api.model.sql.QuestionChoice;

import com.missionhub.api.model.sql.PersonDao;
import com.missionhub.api.model.sql.AssignmentDao;
import com.missionhub.api.model.sql.InterestDao;
import com.missionhub.api.model.sql.EducationDao;
import com.missionhub.api.model.sql.LocationDao;
import com.missionhub.api.model.sql.OrganizationDao;
import com.missionhub.api.model.sql.OrganizationalRoleDao;
import com.missionhub.api.model.sql.GroupDao;
import com.missionhub.api.model.sql.LabelDao;
import com.missionhub.api.model.sql.GroupMembershipDao;
import com.missionhub.api.model.sql.FollowupCommentDao;
import com.missionhub.api.model.sql.RejoicableDao;
import com.missionhub.api.model.sql.KeywordDao;
import com.missionhub.api.model.sql.QuestionDao;
import com.missionhub.api.model.sql.AnswerDao;
import com.missionhub.api.model.sql.QuestionChoiceDao;

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
    private final DaoConfig organizationDaoConfig;
    private final DaoConfig organizationalRoleDaoConfig;
    private final DaoConfig groupDaoConfig;
    private final DaoConfig labelDaoConfig;
    private final DaoConfig groupMembershipDaoConfig;
    private final DaoConfig followupCommentDaoConfig;
    private final DaoConfig rejoicableDaoConfig;
    private final DaoConfig keywordDaoConfig;
    private final DaoConfig questionDaoConfig;
    private final DaoConfig answerDaoConfig;
    private final DaoConfig questionChoiceDaoConfig;

    private final PersonDao personDao;
    private final AssignmentDao assignmentDao;
    private final InterestDao interestDao;
    private final EducationDao educationDao;
    private final LocationDao locationDao;
    private final OrganizationDao organizationDao;
    private final OrganizationalRoleDao organizationalRoleDao;
    private final GroupDao groupDao;
    private final LabelDao labelDao;
    private final GroupMembershipDao groupMembershipDao;
    private final FollowupCommentDao followupCommentDao;
    private final RejoicableDao rejoicableDao;
    private final KeywordDao keywordDao;
    private final QuestionDao questionDao;
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

        organizationDaoConfig = daoConfigMap.get(OrganizationDao.class).clone();
        organizationDaoConfig.initIdentityScope(type);

        organizationalRoleDaoConfig = daoConfigMap.get(OrganizationalRoleDao.class).clone();
        organizationalRoleDaoConfig.initIdentityScope(type);

        groupDaoConfig = daoConfigMap.get(GroupDao.class).clone();
        groupDaoConfig.initIdentityScope(type);

        labelDaoConfig = daoConfigMap.get(LabelDao.class).clone();
        labelDaoConfig.initIdentityScope(type);

        groupMembershipDaoConfig = daoConfigMap.get(GroupMembershipDao.class).clone();
        groupMembershipDaoConfig.initIdentityScope(type);

        followupCommentDaoConfig = daoConfigMap.get(FollowupCommentDao.class).clone();
        followupCommentDaoConfig.initIdentityScope(type);

        rejoicableDaoConfig = daoConfigMap.get(RejoicableDao.class).clone();
        rejoicableDaoConfig.initIdentityScope(type);

        keywordDaoConfig = daoConfigMap.get(KeywordDao.class).clone();
        keywordDaoConfig.initIdentityScope(type);

        questionDaoConfig = daoConfigMap.get(QuestionDao.class).clone();
        questionDaoConfig.initIdentityScope(type);

        answerDaoConfig = daoConfigMap.get(AnswerDao.class).clone();
        answerDaoConfig.initIdentityScope(type);

        questionChoiceDaoConfig = daoConfigMap.get(QuestionChoiceDao.class).clone();
        questionChoiceDaoConfig.initIdentityScope(type);

        personDao = new PersonDao(personDaoConfig, this);
        assignmentDao = new AssignmentDao(assignmentDaoConfig, this);
        interestDao = new InterestDao(interestDaoConfig, this);
        educationDao = new EducationDao(educationDaoConfig, this);
        locationDao = new LocationDao(locationDaoConfig, this);
        organizationDao = new OrganizationDao(organizationDaoConfig, this);
        organizationalRoleDao = new OrganizationalRoleDao(organizationalRoleDaoConfig, this);
        groupDao = new GroupDao(groupDaoConfig, this);
        labelDao = new LabelDao(labelDaoConfig, this);
        groupMembershipDao = new GroupMembershipDao(groupMembershipDaoConfig, this);
        followupCommentDao = new FollowupCommentDao(followupCommentDaoConfig, this);
        rejoicableDao = new RejoicableDao(rejoicableDaoConfig, this);
        keywordDao = new KeywordDao(keywordDaoConfig, this);
        questionDao = new QuestionDao(questionDaoConfig, this);
        answerDao = new AnswerDao(answerDaoConfig, this);
        questionChoiceDao = new QuestionChoiceDao(questionChoiceDaoConfig, this);

        registerDao(Person.class, personDao);
        registerDao(Assignment.class, assignmentDao);
        registerDao(Interest.class, interestDao);
        registerDao(Education.class, educationDao);
        registerDao(Location.class, locationDao);
        registerDao(Organization.class, organizationDao);
        registerDao(OrganizationalRole.class, organizationalRoleDao);
        registerDao(Group.class, groupDao);
        registerDao(Label.class, labelDao);
        registerDao(GroupMembership.class, groupMembershipDao);
        registerDao(FollowupComment.class, followupCommentDao);
        registerDao(Rejoicable.class, rejoicableDao);
        registerDao(Keyword.class, keywordDao);
        registerDao(Question.class, questionDao);
        registerDao(Answer.class, answerDao);
        registerDao(QuestionChoice.class, questionChoiceDao);
    }
    
    public void clear() {
        personDaoConfig.getIdentityScope().clear();
        assignmentDaoConfig.getIdentityScope().clear();
        interestDaoConfig.getIdentityScope().clear();
        educationDaoConfig.getIdentityScope().clear();
        locationDaoConfig.getIdentityScope().clear();
        organizationDaoConfig.getIdentityScope().clear();
        organizationalRoleDaoConfig.getIdentityScope().clear();
        groupDaoConfig.getIdentityScope().clear();
        labelDaoConfig.getIdentityScope().clear();
        groupMembershipDaoConfig.getIdentityScope().clear();
        followupCommentDaoConfig.getIdentityScope().clear();
        rejoicableDaoConfig.getIdentityScope().clear();
        keywordDaoConfig.getIdentityScope().clear();
        questionDaoConfig.getIdentityScope().clear();
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

    public OrganizationDao getOrganizationDao() {
        return organizationDao;
    }

    public OrganizationalRoleDao getOrganizationalRoleDao() {
        return organizationalRoleDao;
    }

    public GroupDao getGroupDao() {
        return groupDao;
    }

    public LabelDao getLabelDao() {
        return labelDao;
    }

    public GroupMembershipDao getGroupMembershipDao() {
        return groupMembershipDao;
    }

    public FollowupCommentDao getFollowupCommentDao() {
        return followupCommentDao;
    }

    public RejoicableDao getRejoicableDao() {
        return rejoicableDao;
    }

    public KeywordDao getKeywordDao() {
        return keywordDao;
    }

    public QuestionDao getQuestionDao() {
        return questionDao;
    }

    public AnswerDao getAnswerDao() {
        return answerDao;
    }

    public QuestionChoiceDao getQuestionChoiceDao() {
        return questionChoiceDao;
    }

}
