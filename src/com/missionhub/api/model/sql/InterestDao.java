package com.missionhub.api.model.sql;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.Query;
import de.greenrobot.dao.QueryBuilder;

import com.missionhub.api.model.sql.Interest;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table INTEREST (schema version 1).
*/
public class InterestDao extends AbstractDao<Interest, Integer> {

    public static final String TABLENAME = "INTEREST";

    public static class Properties {
        public final static Property _id = new Property(0, Integer.class, "_id", true, "_ID");
        public final static Property Person_id = new Property(1, Integer.class, "person_id", false, "PERSON_ID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Interest_id = new Property(3, String.class, "interest_id", false, "INTEREST_ID");
        public final static Property Category = new Property(4, String.class, "category", false, "CATEGORY");
        public final static Property Provider = new Property(5, String.class, "provider", false, "PROVIDER");
    };

    private Query<Interest> person_InterestQuery;

    public InterestDao(DaoConfig config) {
        super(config);
    }
    
    public InterestDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String sql = "CREATE TABLE " + (ifNotExists? "IF NOT EXISTS ": "") + "'INTEREST' (" + //
                "'_ID' INTEGER PRIMARY KEY ," + // 0: _id
                "'PERSON_ID' INTEGER," + // 1: person_id
                "'NAME' TEXT," + // 2: name
                "'INTEREST_ID' TEXT," + // 3: interest_id
                "'CATEGORY' TEXT," + // 4: category
                "'PROVIDER' TEXT);"; // 5: provider
        db.execSQL(sql);
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'INTEREST'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Interest entity) {
        stmt.clearBindings();
 
        Integer _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        Integer person_id = entity.getPerson_id();
        if (person_id != null) {
            stmt.bindLong(2, person_id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String interest_id = entity.getInterest_id();
        if (interest_id != null) {
            stmt.bindString(4, interest_id);
        }
 
        String category = entity.getCategory();
        if (category != null) {
            stmt.bindString(5, category);
        }
 
        String provider = entity.getProvider();
        if (provider != null) {
            stmt.bindString(6, provider);
        }
    }

    /** @inheritdoc */
    @Override
    public Integer readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Interest readEntity(Cursor cursor, int offset) {
        Interest entity = new Interest( //
            cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // person_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // interest_id
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // category
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // provider
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Interest entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0));
        entity.setPerson_id(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setInterest_id(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCategory(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setProvider(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    @Override
    protected Integer updateKeyAfterInsert(Interest entity, long rowId) {
        // TODO XXX Only Long PKs are supported currently
        return null;
    }
    
    /** @inheritdoc */
    @Override
    public Integer getKey(Interest entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "interest" to-many relationship of Person. */
    public synchronized List<Interest> _queryPerson_Interest(Integer person_id) {
        if (person_InterestQuery == null) {
            QueryBuilder<Interest> queryBuilder = queryBuilder();
            queryBuilder.where(Properties.Person_id.eq(person_id));
            person_InterestQuery = queryBuilder.build();
        } else {
            person_InterestQuery.setParameter(0, person_id);
        }
        return person_InterestQuery.list();
    }

}