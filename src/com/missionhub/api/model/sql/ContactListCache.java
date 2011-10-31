package com.missionhub.api.model.sql;

import com.missionhub.api.model.sql.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table CONTACT_LIST_CACHE (schema version 1).
 */
public class ContactListCache {

    private Integer _id;
    private Integer person_id;
    private Integer request_time;
    private String tag;

    /** Used to resolve relations */
    private DaoSession daoSession;

    /** Used for active entity operations. */
    private ContactListCacheDao myDao;

    private Person person;
    private Integer person__resolvedKey;


    public ContactListCache() {
    }

    public ContactListCache(Integer _id) {
        this._id = _id;
    }

    public ContactListCache(Integer _id, Integer person_id, Integer request_time, String tag) {
        this._id = _id;
        this.person_id = person_id;
        this.request_time = request_time;
        this.tag = tag;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getContactListCacheDao() : null;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public Integer getPerson_id() {
        return person_id;
    }

    public void setPerson_id(Integer person_id) {
        this.person_id = person_id;
    }

    public Integer getRequest_time() {
        return request_time;
    }

    public void setRequest_time(Integer request_time) {
        this.request_time = request_time;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    /** To-one relationship, resolved on first access. */
    public Person getPerson() {
        if (person__resolvedKey == null || !person__resolvedKey.equals(person_id)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PersonDao targetDao = daoSession.getPersonDao();
            person = targetDao.load(person_id);
            person__resolvedKey = person_id;
        }
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
        person_id = person == null ? null : person.get_id();
        person__resolvedKey = person_id;
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