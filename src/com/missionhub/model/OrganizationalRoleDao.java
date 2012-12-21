package com.missionhub.model;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.SqlUtils;
import de.greenrobot.dao.Query;
import de.greenrobot.dao.QueryBuilder;

import com.missionhub.model.OrganizationalRole;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ORGANIZATIONAL_ROLE.
*/
public class OrganizationalRoleDao extends AbstractDao<OrganizationalRole, Long> {

    public static final String TABLENAME = "ORGANIZATIONAL_ROLE";

    /**
     * Properties of entity OrganizationalRole.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Followup_status = new Property(1, String.class, "followup_status", false, "FOLLOWUP_STATUS");
        public final static Property Person_id = new Property(2, Long.class, "person_id", false, "PERSON_ID");
        public final static Property Organization_id = new Property(3, Long.class, "organization_id", false, "ORGANIZATION_ID");
        public final static Property Role_id = new Property(4, Long.class, "role_id", false, "ROLE_ID");
        public final static Property Start_date = new Property(5, java.util.Date.class, "start_date", false, "START_DATE");
        public final static Property Updated_at = new Property(6, java.util.Date.class, "updated_at", false, "UPDATED_AT");
        public final static Property Created_at = new Property(7, java.util.Date.class, "created_at", false, "CREATED_AT");
    };

    private DaoSession daoSession;

    private Query<OrganizationalRole> person_OrganizationalRoleListQuery;
    private Query<OrganizationalRole> role_OrganizationalRoleListQuery;

    public OrganizationalRoleDao(DaoConfig config) {
        super(config);
    }
    
    public OrganizationalRoleDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ORGANIZATIONAL_ROLE' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'FOLLOWUP_STATUS' TEXT," + // 1: followup_status
                "'PERSON_ID' INTEGER," + // 2: person_id
                "'ORGANIZATION_ID' INTEGER," + // 3: organization_id
                "'ROLE_ID' INTEGER," + // 4: role_id
                "'START_DATE' INTEGER," + // 5: start_date
                "'UPDATED_AT' INTEGER," + // 6: updated_at
                "'CREATED_AT' INTEGER);"); // 7: created_at
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ORGANIZATIONAL_ROLE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, OrganizationalRole entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String followup_status = entity.getFollowup_status();
        if (followup_status != null) {
            stmt.bindString(2, followup_status);
        }
 
        Long person_id = entity.getPerson_id();
        if (person_id != null) {
            stmt.bindLong(3, person_id);
        }
 
        Long organization_id = entity.getOrganization_id();
        if (organization_id != null) {
            stmt.bindLong(4, organization_id);
        }
 
        Long role_id = entity.getRole_id();
        if (role_id != null) {
            stmt.bindLong(5, role_id);
        }
 
        java.util.Date start_date = entity.getStart_date();
        if (start_date != null) {
            stmt.bindLong(6, start_date.getTime());
        }
 
        java.util.Date updated_at = entity.getUpdated_at();
        if (updated_at != null) {
            stmt.bindLong(7, updated_at.getTime());
        }
 
        java.util.Date created_at = entity.getCreated_at();
        if (created_at != null) {
            stmt.bindLong(8, created_at.getTime());
        }
    }

    @Override
    protected void attachEntity(OrganizationalRole entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public OrganizationalRole readEntity(Cursor cursor, int offset) {
        OrganizationalRole entity = new OrganizationalRole( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // followup_status
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // person_id
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // organization_id
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4), // role_id
            cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)), // start_date
            cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)), // updated_at
            cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)) // created_at
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, OrganizationalRole entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFollowup_status(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPerson_id(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setOrganization_id(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setRole_id(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
        entity.setStart_date(cursor.isNull(offset + 5) ? null : new java.util.Date(cursor.getLong(offset + 5)));
        entity.setUpdated_at(cursor.isNull(offset + 6) ? null : new java.util.Date(cursor.getLong(offset + 6)));
        entity.setCreated_at(cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(OrganizationalRole entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(OrganizationalRole entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "organizationalRoleList" to-many relationship of Person. */
    public synchronized List<OrganizationalRole> _queryPerson_OrganizationalRoleList(Long person_id) {
        if (person_OrganizationalRoleListQuery == null) {
            QueryBuilder<OrganizationalRole> queryBuilder = queryBuilder();
            queryBuilder.where(Properties.Person_id.eq(person_id));
            person_OrganizationalRoleListQuery = queryBuilder.build();
        } else {
            person_OrganizationalRoleListQuery.setParameter(0, person_id);
        }
        return person_OrganizationalRoleListQuery.list();
    }

    /** Internal query to resolve the "organizationalRoleList" to-many relationship of Role. */
    public synchronized List<OrganizationalRole> _queryRole_OrganizationalRoleList(Long role_id) {
        if (role_OrganizationalRoleListQuery == null) {
            QueryBuilder<OrganizationalRole> queryBuilder = queryBuilder();
            queryBuilder.where(Properties.Role_id.eq(role_id));
            role_OrganizationalRoleListQuery = queryBuilder.build();
        } else {
            role_OrganizationalRoleListQuery.setParameter(0, role_id);
        }
        return role_OrganizationalRoleListQuery.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getPhoneNumberDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getOrganizationDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T2", daoSession.getRoleDao().getAllColumns());
            builder.append(" FROM ORGANIZATIONAL_ROLE T");
            builder.append(" LEFT JOIN PHONE_NUMBER T0 ON T.'PERSON_ID'=T0.'_id'");
            builder.append(" LEFT JOIN ORGANIZATION T1 ON T.'ORGANIZATION_ID'=T1.'_id'");
            builder.append(" LEFT JOIN ROLE T2 ON T.'ROLE_ID'=T2.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected OrganizationalRole loadCurrentDeep(Cursor cursor, boolean lock) {
        OrganizationalRole entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        PhoneNumber phoneNumber = loadCurrentOther(daoSession.getPhoneNumberDao(), cursor, offset);
        entity.setPhoneNumber(phoneNumber);
        offset += daoSession.getPhoneNumberDao().getAllColumns().length;

        Organization organization = loadCurrentOther(daoSession.getOrganizationDao(), cursor, offset);
        entity.setOrganization(organization);
        offset += daoSession.getOrganizationDao().getAllColumns().length;

        Role role = loadCurrentOther(daoSession.getRoleDao(), cursor, offset);
        entity.setRole(role);

        return entity;    
    }

    public OrganizationalRole loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<OrganizationalRole> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<OrganizationalRole> list = new ArrayList<OrganizationalRole>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<OrganizationalRole> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<OrganizationalRole> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}