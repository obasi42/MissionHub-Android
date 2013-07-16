package com.missionhub.model;

import java.util.List;

import com.missionhub.model.DaoSession;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.missionhub.application.Session;
import com.missionhub.util.SortUtils;

import org.apache.commons.lang3.StringUtils;
// KEEP INCLUDES END
/**
 * Entity mapped to table ORGANIZATION.
 */
public class Organization implements com.missionhub.model.TimestampedEntity {

    private Long id;
    private String name;
    private String terminology;
    private String ancestry;
    private Boolean show_sub_orgs;
    private String status;
    private String updated_at;
    private String created_at;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient OrganizationDao myDao;

    private List<Label> labelList;
    private List<Interaction> interactionList;
    private List<InteractionType> interactionTypeList;
    private List<Survey> surveys;
    private List<SmsKeyword> keywords;

    // KEEP FIELDS - put your custom fields here
    private List<Person> mUsersAdmins;
    private List<Organization> mSubOrganizations;
    private Organization mParent;
    private List<Organization> mAllSubOrganizations;
    private List<InteractionType> mAllInteractionTypes;
    private List<Label> mAllLabels;
    private List<Person> mAllUsersAdmins;
    // KEEP FIELDS END

    public Organization() {
    }

    public Organization(Long id) {
        this.id = id;
    }

    public Organization(Long id, String name, String terminology, String ancestry, Boolean show_sub_orgs, String status, String updated_at, String created_at) {
        this.id = id;
        this.name = name;
        this.terminology = terminology;
        this.ancestry = ancestry;
        this.show_sub_orgs = show_sub_orgs;
        this.status = status;
        this.updated_at = updated_at;
        this.created_at = created_at;
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

    public String getTerminology() {
        return terminology;
    }

    public void setTerminology(String terminology) {
        this.terminology = terminology;
    }

    public String getAncestry() {
        return ancestry;
    }

    public void setAncestry(String ancestry) {
        this.ancestry = ancestry;
    }

    public Boolean getShow_sub_orgs() {
        return show_sub_orgs;
    }

    public void setShow_sub_orgs(Boolean show_sub_orgs) {
        this.show_sub_orgs = show_sub_orgs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Label> getLabelList() {
        if (labelList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LabelDao targetDao = daoSession.getLabelDao();
            List<Label> labelListNew = targetDao._queryOrganization_LabelList(id);
            synchronized (this) {
                if(labelList == null) {
                    labelList = labelListNew;
                }
            }
        }
        return labelList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetLabelList() {
        labelList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Interaction> getInteractionList() {
        if (interactionList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            InteractionDao targetDao = daoSession.getInteractionDao();
            List<Interaction> interactionListNew = targetDao._queryOrganization_InteractionList(id);
            synchronized (this) {
                if(interactionList == null) {
                    interactionList = interactionListNew;
                }
            }
        }
        return interactionList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetInteractionList() {
        interactionList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<InteractionType> getInteractionTypeList() {
        if (interactionTypeList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            InteractionTypeDao targetDao = daoSession.getInteractionTypeDao();
            List<InteractionType> interactionTypeListNew = targetDao._queryOrganization_InteractionTypeList(id);
            synchronized (this) {
                if(interactionTypeList == null) {
                    interactionTypeList = interactionTypeListNew;
                }
            }
        }
        return interactionTypeList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetInteractionTypeList() {
        interactionTypeList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Survey> getSurveys() {
        if (surveys == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SurveyDao targetDao = daoSession.getSurveyDao();
            List<Survey> surveysNew = targetDao._queryOrganization_Surveys(id);
            synchronized (this) {
                if(surveys == null) {
                    surveys = surveysNew;
                }
            }
        }
        return surveys;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetSurveys() {
        surveys = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<SmsKeyword> getKeywords() {
        if (keywords == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SmsKeywordDao targetDao = daoSession.getSmsKeywordDao();
            List<SmsKeyword> keywordsNew = targetDao._queryOrganization_Keywords(id);
            synchronized (this) {
                if(keywords == null) {
                    keywords = keywordsNew;
                }
            }
        }
        return keywords;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetKeywords() {
        keywords = null;
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
    public void refreshAll() {
        refresh();
        resetLabelList();
        resetInteractionList();
        resetInteractionTypeList();
        resetSurveys();
        resetKeywords();
        synchronized (this) {
            mAllInteractionTypes = null;
            mAllLabels = null;
            mSubOrganizations = null;
            mAllSubOrganizations = null;
            mParent = null;
            mUsersAdmins = null;
            mAllUsersAdmins = null;
        }
    }

    public List<Organization> getSubOrganizations() {
        if (mSubOrganizations == null) {
            if (myDao == null) {
                throw new DaoException("Entity is detached from DAO context");
            }

            List<Organization> subOrgs = myDao.queryBuilder() //
                    .where(OrganizationDao.Properties.Status.eq("active")) //
                    .whereOr(OrganizationDao.Properties.Ancestry.eq(getId()), OrganizationDao.Properties.Ancestry.eq(getAncestry() + "/" + getId())) //
                    .orderAsc(OrganizationDao.Properties.Name).list();

            synchronized (this) {
                mSubOrganizations = subOrgs;
            }
        }
        return mSubOrganizations;
    }

    public List<Organization> getAllSubOrganizations() {
        if (mAllSubOrganizations == null) {
                List<Organization> orgs = myDao.queryBuilder() //
                        .where(OrganizationDao.Properties.Status.eq("active")) //
                        .whereOr(
                                OrganizationDao.Properties.Ancestry.eq(getId()),
                                OrganizationDao.Properties.Ancestry.eq(getAncestry() + "/" + getId()),
                                OrganizationDao.Properties.Ancestry.like(getAncestry() + "/" + getId() + "/%")) //
                        .orderAsc(OrganizationDao.Properties.Name).list();

                List<Organization> filteredOrgs = new ArrayList<Organization>();
                for (Organization org : orgs) {
                    if (org.getParent() != null) {
                        if (!org.getParent().getShow_sub_orgs()) {
                            continue;
                        }
                    }
                    filteredOrgs.add(org);
                }
                synchronized (this) {
                mAllSubOrganizations = filteredOrgs;
            }
        }
        return mAllSubOrganizations;
    }

    public List<Label> getAllLabels() {
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        if (mAllLabels == null) {
            List<Label> labels = daoSession.getLabelDao().queryBuilder().where(LabelDao.Properties.Organization_id.in(0, getId())).list();

            synchronized (this) {
                mAllLabels = SortUtils.sortLabels(labels, true);
            }
        }
        return mAllLabels;
    }

    public List<InteractionType> getAllInteractionTypes() {
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        if (mAllInteractionTypes == null) {
            List<InteractionType> types = daoSession.getInteractionTypeDao().queryBuilder().where(InteractionTypeDao.Properties.Organization_id.in(0, getId())).list();

            synchronized (this) {
                mAllInteractionTypes = SortUtils.sortInteractionTypes(types, true);
            }
        }
        return mAllInteractionTypes;
    }

    public Organization getParent() {
        if (mParent == null) {
            if (myDao == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            if (StringUtils.isNotEmpty(getAncestry())) {
                List<String> ancestors = Arrays.asList(getAncestry().trim().split("/"));
                long organizationId = Long.parseLong(ancestors.get(ancestors.size() - 1).trim());
                Organization organization = myDao.load(organizationId);
                synchronized (this) {
                    mParent = organization;
                }
            }
        }
        return mParent;
    }

    public List<Person> getUsersAdmins() {
        if (mUsersAdmins == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            HashSet<Person> people = new HashSet<Person>();
            final List<OrganizationalPermission> permissions = daoSession.getOrganizationalPermissionDao().queryBuilder().where(OrganizationalPermissionDao.Properties.Organization_id.eq(getId()), OrganizationalPermissionDao.Properties.Permission_id.in(Permission.ADMIN, Permission.USER)).list();
            for (OrganizationalPermission permission : permissions) {
                if (permission != null && permission.getPerson() != null) {
                    people.add(permission.getPerson());
                }
            }
            if (Session.getInstance().getPerson().isAdminOrUser(getId())) {
                people.add(Session.getInstance().getPerson());
            }
            synchronized (this) {
                mUsersAdmins = SortUtils.sortPeople(people, true);
            }
        }
        return mUsersAdmins;
    }

    public List<Person> getAllUsersAdmins() {
        if (mAllUsersAdmins == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            HashSet<Person> people = new HashSet<Person>();

                Organization current = this;
                while (true) {
                    final List<OrganizationalPermission> permissions = daoSession.getOrganizationalPermissionDao().queryBuilder().where(OrganizationalPermissionDao.Properties.Organization_id.eq(current.getId()), OrganizationalPermissionDao.Properties.Permission_id.in(Permission.ADMIN, Permission.USER)).list();
                    for (OrganizationalPermission permission : permissions) {
                        Person person = permission.getPerson();
                        if (person != null) {
                            people.add(person);
                        }
                    }
                    current = current.getParent();
                    if (current == null || !current.getShow_sub_orgs()) {
                        break;
                    }
                }
            synchronized (this) {
                mAllUsersAdmins = SortUtils.sortPeople(people, true);
            }
        }
        return mAllUsersAdmins;

    }
    // KEEP METHODS END

}
