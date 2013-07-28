package com.missionhub.model;

import com.missionhub.model.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import com.missionhub.application.Application;
import com.missionhub.util.ResourceUtils;
// KEEP INCLUDES END
/**
 * Entity mapped to table PERMISSION.
 */
public class Permission implements com.missionhub.model.TimestampedEntity {

    private Long id;
    private String name;
    private String i18n;
    private String updated_at;
    private String created_at;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient PermissionDao myDao;


    // KEEP FIELDS - put your custom fields here
    public static final long ADMIN = 1;
    public static final long USER = 1938;
    public static final long NO_PERMISSIONS = 2;
    private String mTranslatedName;
    // KEEP FIELDS END

    public Permission() {
    }

    public Permission(Long id) {
        this.id = id;
    }

    public Permission(Long id, String name, String i18n, String updated_at, String created_at) {
        this.id = id;
        this.name = name;
        this.i18n = i18n;
        this.updated_at = updated_at;
        this.created_at = created_at;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPermissionDao() : null;
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

    public String getI18n() {
        return i18n;
    }

    public void setI18n(String i18n) {
        this.i18n = i18n;
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
        daoSession.getOrganizationalPermissionDao().deleteByKeyInTx(daoSession.getOrganizationalPermissionDao().queryBuilder().where(OrganizationalPermissionDao.Properties.Permission_id.eq(getId())).<Long>listKeys());
        delete();
    }

    public String getTranslatedName() {
        if (mTranslatedName == null) {
            mTranslatedName = ResourceUtils.getTranslatedName("permission", getI18n(), getName());
        }
        return mTranslatedName;
    }

    public void resetTranslatedName() {
        mTranslatedName = null;
    }

    public void refreshAll() {
        refresh();
        resetTranslatedName();
    }

    public static Permission getPermission(long permissionId) {
        Permission permisson = Application.getDb().getPermissionDao().load(permissionId);
        if (permisson == null) {
            permisson = Application.getDb().getPermissionDao().load(NO_PERMISSIONS);
        }
        return permisson;
    }
    // KEEP METHODS END

}
