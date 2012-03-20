package com.missionhub.api.model.sql;

import java.util.List;
import com.missionhub.api.model.sql.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table FOLLOWUP_COMMENT.
 */
public class FollowupComment {

    private Long id;
    private Long contact_id;
    private Long commenter_id;
    private Long organization_id;
    private String comment;
    private String status;
    private java.util.Date created_at;
    private java.util.Date updated_at;
    private java.util.Date deleted_at;

    /** Used to resolve relations */
    private DaoSession daoSession;

    /** Used for active entity operations. */
    private FollowupCommentDao myDao;

    private List<Rejoicable> rejoicables;

    public FollowupComment() {
    }

    public FollowupComment(Long id) {
        this.id = id;
    }

    public FollowupComment(Long id, Long contact_id, Long commenter_id, Long organization_id, String comment, String status, java.util.Date created_at, java.util.Date updated_at, java.util.Date deleted_at) {
        this.id = id;
        this.contact_id = contact_id;
        this.commenter_id = commenter_id;
        this.organization_id = organization_id;
        this.comment = comment;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getFollowupCommentDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContact_id() {
        return contact_id;
    }

    public void setContact_id(Long contact_id) {
        this.contact_id = contact_id;
    }

    public Long getCommenter_id() {
        return commenter_id;
    }

    public void setCommenter_id(Long commenter_id) {
        this.commenter_id = commenter_id;
    }

    public Long getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(Long organization_id) {
        this.organization_id = organization_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public java.util.Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(java.util.Date created_at) {
        this.created_at = created_at;
    }

    public java.util.Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(java.util.Date updated_at) {
        this.updated_at = updated_at;
    }

    public java.util.Date getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(java.util.Date deleted_at) {
        this.deleted_at = deleted_at;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public synchronized List<Rejoicable> getRejoicables() {
        if (rejoicables == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RejoicableDao targetDao = daoSession.getRejoicableDao();
            rejoicables = targetDao._queryFollowupComment_Rejoicables(id);
        }
        return rejoicables;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetRejoicables() {
        rejoicables = null;
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
