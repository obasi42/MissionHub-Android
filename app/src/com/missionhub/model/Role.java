package com.missionhub.model;

import java.util.List;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import com.missionhub.R;
import android.content.Context;
import com.missionhub.application.Application;
// KEEP INCLUDES END
/**
 * Entity mapped to table ROLE.
 */
public class Role {

    private Long id;
    private String name;
    private Long organization_id;
    private String i18n;
    private java.util.Date updated_at;
    private java.util.Date created_at;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient RoleDao myDao;

    private Organization organization;
    private Long organization__resolvedKey;

    private List<OrganizationalRole> organizationalRoleList;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Role() {
    }

    public Role(Long id) {
        this.id = id;
    }

    public Role(Long id, String name, Long organization_id, String i18n, java.util.Date updated_at, java.util.Date created_at) {
        this.id = id;
        this.name = name;
        this.organization_id = organization_id;
        this.i18n = i18n;
        this.updated_at = updated_at;
        this.created_at = created_at;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRoleDao() : null;
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

    public Long getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(Long organization_id) {
        this.organization_id = organization_id;
    }

    public String getI18n() {
        return i18n;
    }

    public void setI18n(String i18n) {
        this.i18n = i18n;
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
        Long __key = this.organization_id;
        if (organization__resolvedKey == null || !organization__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            OrganizationDao targetDao = daoSession.getOrganizationDao();
            Organization organizationNew = targetDao.load(__key);
            synchronized (this) {
                organization = organizationNew;
            	organization__resolvedKey = __key;
            }
        }
        return organization;
    }

    public void setOrganization(Organization organization) {
        synchronized (this) {
            this.organization = organization;
            organization_id = organization == null ? null : organization.getId();
            organization__resolvedKey = organization_id;
        }
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<OrganizationalRole> getOrganizationalRoleList() {
        if (organizationalRoleList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            OrganizationalRoleDao targetDao = daoSession.getOrganizationalRoleDao();
            List<OrganizationalRole> organizationalRoleListNew = targetDao._queryRole_OrganizationalRoleList(id);
            synchronized (this) {
                if(organizationalRoleList == null) {
                    organizationalRoleList = organizationalRoleListNew;
                }
            }
        }
        return organizationalRoleList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetOrganizationalRoleList() {
        organizationalRoleList = null;
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
	public void deleteWithRelations() {
		if (daoSession == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		final OrganizationalRoleDao targetDao = daoSession.getOrganizationalRoleDao();
		targetDao.deleteByKeyInTx(targetDao.queryBuilder().where(OrganizationalRoleDao.Properties.Role_id.eq(getId())).<Long>listKeys());
		delete();
	}
    public String getTranslatedName() {
        if (getI18n() != null && !getI18n().equals("")) {
            final Context context = Application.getContext();
            if (getI18n().equals("contact")) {
                return context.getString(R.string.role_contact);
            } else if (getI18n().equals("alumni")) {
                return context.getString(R.string.role_alumni);
            } else if (getI18n().equals("involved")) {
                return context.getString(R.string.role_involved);
            } else if (getI18n().equals("leader")) {
                return context.getString(R.string.role_leader);
            } else if (getI18n().equals("sent")) {
                return context.getString(R.string.role_sent);
            } else if (getI18n().equals("admin")) {
                return context.getString(R.string.role_admin);
            }
        }
        return getName();
    }
    // KEEP METHODS END

}
