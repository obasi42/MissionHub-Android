package com.missionhub.model;

import com.missionhub.model.DaoSession;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

/**
 * Entity mapped to table INTERACTION_INITIATOR.
 */
public class InteractionInitiator {

    private Long id;
    private Long person_id;
    private Long interaction_id;
    private String updated_at;
    private String created_at;

    /**
     * Used to resolve relations
     */
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    private transient InteractionInitiatorDao myDao;

    private Person person;
    private Long person__resolvedKey;

    private Interaction interaction;
    private Long interaction__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public InteractionInitiator() {
    }

    public InteractionInitiator(Long id) {
        this.id = id;
    }

    public InteractionInitiator(Long id, Long person_id, Long interaction_id, String updated_at, String created_at) {
        this.id = id;
        this.person_id = person_id;
        this.interaction_id = interaction_id;
        this.updated_at = updated_at;
        this.created_at = created_at;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getInteractionInitiatorDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPerson_id() {
        return person_id;
    }

    public void setPerson_id(Long person_id) {
        this.person_id = person_id;
    }

    public Long getInteraction_id() {
        return interaction_id;
    }

    public void setInteraction_id(Long interaction_id) {
        this.interaction_id = interaction_id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    public Person getPerson() {
        Long __key = this.person_id;
        if (person__resolvedKey == null || !person__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PersonDao targetDao = daoSession.getPersonDao();
            Person personNew = targetDao.load(__key);
            synchronized (this) {
                person = personNew;
                person__resolvedKey = __key;
            }
        }
        return person;
    }

    public void setPerson(Person person) {
        synchronized (this) {
            this.person = person;
            person_id = person == null ? null : person.getId();
            person__resolvedKey = person_id;
        }
    }

    /**
     * To-one relationship, resolved on first access.
     */
    public Interaction getInteraction() {
        Long __key = this.interaction_id;
        if (interaction__resolvedKey == null || !interaction__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            InteractionDao targetDao = daoSession.getInteractionDao();
            Interaction interactionNew = targetDao.load(__key);
            synchronized (this) {
                interaction = interactionNew;
                interaction__resolvedKey = __key;
            }
        }
        return interaction;
    }

    public void setInteraction(Interaction interaction) {
        synchronized (this) {
            this.interaction = interaction;
            interaction_id = interaction == null ? null : interaction.getId();
            interaction__resolvedKey = interaction_id;
        }
    }

    /**
     * Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context.
     */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context.
     */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context.
     */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
