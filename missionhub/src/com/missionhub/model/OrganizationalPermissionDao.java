package com.missionhub.model;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import com.missionhub.model.OrganizationalPermission;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ORGANIZATIONAL_PERMISSION.
*/
public class OrganizationalPermissionDao extends AbstractDao<OrganizationalPermission, Long> {

    public static final String TABLENAME = "ORGANIZATIONAL_PERMISSION";

    /**
     * Properties of entity OrganizationalPermission.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Person_id = new Property(1, Long.class, "person_id", false, "PERSON_ID");
        public final static Property Permission_id = new Property(2, Long.class, "permission_id", false, "PERMISSION_ID");
        public final static Property Organization_id = new Property(3, Long.class, "organization_id", false, "ORGANIZATION_ID");
        public final static Property Followup_status = new Property(4, String.class, "followup_status", false, "FOLLOWUP_STATUS");
        public final static Property Start_date = new Property(5, String.class, "start_date", false, "START_DATE");
        public final static Property Updated_at = new Property(6, String.class, "updated_at", false, "UPDATED_AT");
        public final static Property Created_at = new Property(7, String.class, "created_at", false, "CREATED_AT");
        public final static Property Archive_date = new Property(8, String.class, "archive_date", false, "ARCHIVE_DATE");
    };

    private DaoSession daoSession;

    private Query<OrganizationalPermission> person_OrganizationalPermissionListQuery;

    public OrganizationalPermissionDao(DaoConfig config) {
        super(config);
    }
    
    public OrganizationalPermissionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ORGANIZATIONAL_PERMISSION' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'PERSON_ID' INTEGER," + // 1: person_id
                "'PERMISSION_ID' INTEGER," + // 2: permission_id
                "'ORGANIZATION_ID' INTEGER," + // 3: organization_id
                "'FOLLOWUP_STATUS' TEXT," + // 4: followup_status
                "'START_DATE' TEXT," + // 5: start_date
                "'UPDATED_AT' TEXT," + // 6: updated_at
                "'CREATED_AT' TEXT," + // 7: created_at
                "'ARCHIVE_DATE' TEXT);"); // 8: archive_date
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ORGANIZATIONAL_PERMISSION'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, OrganizationalPermission entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long person_id = entity.getPerson_id();
        if (person_id != null) {
            stmt.bindLong(2, person_id);
        }
 
        Long permission_id = entity.getPermission_id();
        if (permission_id != null) {
            stmt.bindLong(3, permission_id);
        }
 
        Long organization_id = entity.getOrganization_id();
        if (organization_id != null) {
            stmt.bindLong(4, organization_id);
        }
 
        String followup_status = entity.getFollowup_status();
        if (followup_status != null) {
            stmt.bindString(5, followup_status);
        }
 
        String start_date = entity.getStart_date();
        if (start_date != null) {
            stmt.bindString(6, start_date);
        }
 
        String updated_at = entity.getUpdated_at();
        if (updated_at != null) {
            stmt.bindString(7, updated_at);
        }
 
        String created_at = entity.getCreated_at();
        if (created_at != null) {
            stmt.bindString(8, created_at);
        }
 
        String archive_date = entity.getArchive_date();
        if (archive_date != null) {
            stmt.bindString(9, archive_date);
        }
    }

    @Override
    protected void attachEntity(OrganizationalPermission entity) {
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
    public OrganizationalPermission readEntity(Cursor cursor, int offset) {
        OrganizationalPermission entity = new OrganizationalPermission( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // person_id
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // permission_id
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // organization_id
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // followup_status
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // start_date
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // updated_at
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // created_at
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) // archive_date
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, OrganizationalPermission entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPerson_id(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setPermission_id(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setOrganization_id(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setFollowup_status(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setStart_date(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setUpdated_at(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setCreated_at(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setArchive_date(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(OrganizationalPermission entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(OrganizationalPermission entity) {
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
    
    /** Internal query to resolve the "organizationalPermissionList" to-many relationship of Person. */
    public List<OrganizationalPermission> _queryPerson_OrganizationalPermissionList(Long person_id) {
        synchronized (this) {
            if (person_OrganizationalPermissionListQuery == null) {
                QueryBuilder<OrganizationalPermission> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.Person_id.eq(null));
                person_OrganizationalPermissionListQuery = queryBuilder.build();
            }
        }
        Query<OrganizationalPermission> query = person_OrganizationalPermissionListQuery.forCurrentThread();
        query.setParameter(0, person_id);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getPersonDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getPermissionDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T2", daoSession.getOrganizationDao().getAllColumns());
            builder.append(" FROM ORGANIZATIONAL_PERMISSION T");
            builder.append(" LEFT JOIN PERSON T0 ON T.'PERSON_ID'=T0.'_id'");
            builder.append(" LEFT JOIN PERMISSION T1 ON T.'PERMISSION_ID'=T1.'_id'");
            builder.append(" LEFT JOIN ORGANIZATION T2 ON T.'ORGANIZATION_ID'=T2.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected OrganizationalPermission loadCurrentDeep(Cursor cursor, boolean lock) {
        OrganizationalPermission entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Person person = loadCurrentOther(daoSession.getPersonDao(), cursor, offset);
        entity.setPerson(person);
        offset += daoSession.getPersonDao().getAllColumns().length;

        Permission permission = loadCurrentOther(daoSession.getPermissionDao(), cursor, offset);
        entity.setPermission(permission);
        offset += daoSession.getPermissionDao().getAllColumns().length;

        Organization organization = loadCurrentOther(daoSession.getOrganizationDao(), cursor, offset);
        entity.setOrganization(organization);

        return entity;    
    }

    public OrganizationalPermission loadDeep(Long key) {
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
    public List<OrganizationalPermission> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<OrganizationalPermission> list = new ArrayList<OrganizationalPermission>(count);
        
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
    
    protected List<OrganizationalPermission> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<OrganizationalPermission> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}