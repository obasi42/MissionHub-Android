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

import com.missionhub.model.EmailAddress;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table EMAIL_ADDRESS.
*/
public class EmailAddressDao extends AbstractDao<EmailAddress, Long> {

    public static final String TABLENAME = "EMAIL_ADDRESS";

    /**
     * Properties of entity EmailAddress.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Email = new Property(1, String.class, "email", false, "EMAIL");
        public final static Property Primary = new Property(2, Boolean.class, "primary", false, "PRIMARY");
        public final static Property Person_id = new Property(3, Long.class, "person_id", false, "PERSON_ID");
        public final static Property Updated_at = new Property(4, String.class, "updated_at", false, "UPDATED_AT");
        public final static Property Created_at = new Property(5, String.class, "created_at", false, "CREATED_AT");
    };

    private DaoSession daoSession;

    private Query<EmailAddress> person_EmailAddressListQuery;

    public EmailAddressDao(DaoConfig config) {
        super(config);
    }
    
    public EmailAddressDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'EMAIL_ADDRESS' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'EMAIL' TEXT," + // 1: email
                "'PRIMARY' INTEGER," + // 2: primary
                "'PERSON_ID' INTEGER," + // 3: person_id
                "'UPDATED_AT' TEXT," + // 4: updated_at
                "'CREATED_AT' TEXT);"); // 5: created_at
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'EMAIL_ADDRESS'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, EmailAddress entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String email = entity.getEmail();
        if (email != null) {
            stmt.bindString(2, email);
        }
 
        Boolean primary = entity.getPrimary();
        if (primary != null) {
            stmt.bindLong(3, primary ? 1l: 0l);
        }
 
        Long person_id = entity.getPerson_id();
        if (person_id != null) {
            stmt.bindLong(4, person_id);
        }
 
        String updated_at = entity.getUpdated_at();
        if (updated_at != null) {
            stmt.bindString(5, updated_at);
        }
 
        String created_at = entity.getCreated_at();
        if (created_at != null) {
            stmt.bindString(6, created_at);
        }
    }

    @Override
    protected void attachEntity(EmailAddress entity) {
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
    public EmailAddress readEntity(Cursor cursor, int offset) {
        EmailAddress entity = new EmailAddress( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // email
            cursor.isNull(offset + 2) ? null : cursor.getShort(offset + 2) != 0, // primary
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // person_id
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // updated_at
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // created_at
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, EmailAddress entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setEmail(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPrimary(cursor.isNull(offset + 2) ? null : cursor.getShort(offset + 2) != 0);
        entity.setPerson_id(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setUpdated_at(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCreated_at(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(EmailAddress entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(EmailAddress entity) {
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
    
    /** Internal query to resolve the "emailAddressList" to-many relationship of Person. */
    public List<EmailAddress> _queryPerson_EmailAddressList(Long person_id) {
        synchronized (this) {
            if (person_EmailAddressListQuery == null) {
                QueryBuilder<EmailAddress> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.Person_id.eq(null));
                person_EmailAddressListQuery = queryBuilder.build();
            }
        }
        Query<EmailAddress> query = person_EmailAddressListQuery.forCurrentThread();
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
            builder.append(" FROM EMAIL_ADDRESS T");
            builder.append(" LEFT JOIN PERSON T0 ON T.'PERSON_ID'=T0.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected EmailAddress loadCurrentDeep(Cursor cursor, boolean lock) {
        EmailAddress entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Person person = loadCurrentOther(daoSession.getPersonDao(), cursor, offset);
        entity.setPerson(person);

        return entity;    
    }

    public EmailAddress loadDeep(Long key) {
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
    public List<EmailAddress> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<EmailAddress> list = new ArrayList<EmailAddress>(count);
        
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
    
    protected List<EmailAddress> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<EmailAddress> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
