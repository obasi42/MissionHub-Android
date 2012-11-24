package com.missionhub.model;

import java.util.List;
import com.missionhub.model.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table ORGANIZATION.
 */
public class Organization {

    private Long id;
    private String name;
    private String ancestry;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient OrganizationDao myDao;

    private List<Group> groups;
    private List<GroupLabel> labels;
    private List<FollowupComment> followupCommentList;
    private List<Keyword> keywordList;
    private List<Answer> answerList;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Organization() {
    }

    public Organization(Long id) {
        this.id = id;
    }

    public Organization(Long id, String name, String ancestry) {
        this.id = id;
        this.name = name;
        this.ancestry = ancestry;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getOrganizationDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAncestry() {
        return ancestry;
    }

    public void setAncestry(String ancestry) {
        this.ancestry = ancestry;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public synchronized List<Group> getGroups() {
        if (groups == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GroupDao targetDao = daoSession.getGroupDao();
            groups = targetDao._queryOrganization_Groups(id);
        }
        return groups;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetGroups() {
        groups = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public synchronized List<GroupLabel> getLabels() {
        if (labels == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GroupLabelDao targetDao = daoSession.getGroupLabelDao();
            labels = targetDao._queryOrganization_Labels(id);
        }
        return labels;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetLabels() {
        labels = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public synchronized List<FollowupComment> getFollowupCommentList() {
        if (followupCommentList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FollowupCommentDao targetDao = daoSession.getFollowupCommentDao();
            followupCommentList = targetDao._queryOrganization_FollowupCommentList(id);
        }
        return followupCommentList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetFollowupCommentList() {
        followupCommentList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public synchronized List<Keyword> getKeywordList() {
        if (keywordList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            KeywordDao targetDao = daoSession.getKeywordDao();
            keywordList = targetDao._queryOrganization_KeywordList(id);
        }
        return keywordList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetKeywordList() {
        keywordList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public synchronized List<Answer> getAnswerList() {
        if (answerList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AnswerDao targetDao = daoSession.getAnswerDao();
            answerList = targetDao._queryOrganization_AnswerList(id);
        }
        return answerList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetAnswerList() {
        answerList = null;
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
