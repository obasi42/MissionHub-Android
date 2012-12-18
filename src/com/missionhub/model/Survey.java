package com.missionhub.model;

import java.util.List;
import com.missionhub.model.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table SURVEY.
 */
public class Survey {

    private Long id;
    private String title;
    private Long organization_id;
    private String post_survey_message;
    private String terminology;
    private String login_paragraph;
    private Boolean is_frozen;
    private java.util.Date updated_at;
    private java.util.Date created_at;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient SurveyDao myDao;

    private Organization organization;
    private Long organization__resolvedKey;

    private List<SmsKeyword> smsKeywordList;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Survey() {
    }

    public Survey(Long id) {
        this.id = id;
    }

    public Survey(Long id, String title, Long organization_id, String post_survey_message, String terminology, String login_paragraph, Boolean is_frozen, java.util.Date updated_at, java.util.Date created_at) {
        this.id = id;
        this.title = title;
        this.organization_id = organization_id;
        this.post_survey_message = post_survey_message;
        this.terminology = terminology;
        this.login_paragraph = login_paragraph;
        this.is_frozen = is_frozen;
        this.updated_at = updated_at;
        this.created_at = created_at;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSurveyDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(Long organization_id) {
        this.organization_id = organization_id;
    }

    public String getPost_survey_message() {
        return post_survey_message;
    }

    public void setPost_survey_message(String post_survey_message) {
        this.post_survey_message = post_survey_message;
    }

    public String getTerminology() {
        return terminology;
    }

    public void setTerminology(String terminology) {
        this.terminology = terminology;
    }

    public String getLogin_paragraph() {
        return login_paragraph;
    }

    public void setLogin_paragraph(String login_paragraph) {
        this.login_paragraph = login_paragraph;
    }

    public Boolean getIs_frozen() {
        return is_frozen;
    }

    public void setIs_frozen(Boolean is_frozen) {
        this.is_frozen = is_frozen;
    }

    public java.util.Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(java.util.Date updated_at) {
        this.updated_at = updated_at;
    }

    public java.util.Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(java.util.Date created_at) {
        this.created_at = created_at;
    }

    /** To-one relationship, resolved on first access. */
    public Organization getOrganization() {
        if (organization__resolvedKey == null || !organization__resolvedKey.equals(organization_id)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            OrganizationDao targetDao = daoSession.getOrganizationDao();
            organization = targetDao.load(organization_id);
            organization__resolvedKey = organization_id;
        }
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        organization_id = organization == null ? null : organization.getId();
        organization__resolvedKey = organization_id;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public synchronized List<SmsKeyword> getSmsKeywordList() {
        if (smsKeywordList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SmsKeywordDao targetDao = daoSession.getSmsKeywordDao();
            smsKeywordList = targetDao._querySurvey_SmsKeywordList(id);
        }
        return smsKeywordList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetSmsKeywordList() {
        smsKeywordList = null;
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
