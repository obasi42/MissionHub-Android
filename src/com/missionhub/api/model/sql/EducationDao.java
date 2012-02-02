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

import com.missionhub.api.model.sql.Education;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table EDUCATION.
*/
public class EducationDao extends AbstractDao<Education, Integer> {

    public static final String TABLENAME = "EDUCATION";

    public static class Properties {
        public final static Property _id = new Property(0, Integer.class, "_id", true, "_ID");
        public final static Property Person_id = new Property(1, Integer.class, "person_id", false, "PERSON_ID");
        public final static Property School_name = new Property(2, String.class, "school_name", false, "SCHOOL_NAME");
        public final static Property School_id = new Property(3, String.class, "school_id", false, "SCHOOL_ID");
        public final static Property Year_name = new Property(4, String.class, "year_name", false, "YEAR_NAME");
        public final static Property Year_id = new Property(5, String.class, "year_id", false, "YEAR_ID");
        public final static Property Type = new Property(6, String.class, "type", false, "TYPE");
        public final static Property Provider = new Property(7, String.class, "provider", false, "PROVIDER");
    };

    private Query<Education> person_EducationQuery;

    public EducationDao(DaoConfig config) {
        super(config);
    }
    
    public EducationDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String sql = "CREATE TABLE " + (ifNotExists? "IF NOT EXISTS ": "") + "'EDUCATION' (" + //
                "'_ID' INTEGER PRIMARY KEY ," + // 0: _id
                "'PERSON_ID' INTEGER," + // 1: person_id
                "'SCHOOL_NAME' TEXT," + // 2: school_name
                "'SCHOOL_ID' TEXT," + // 3: school_id
                "'YEAR_NAME' TEXT," + // 4: year_name
                "'YEAR_ID' TEXT," + // 5: year_id
                "'TYPE' TEXT," + // 6: type
                "'PROVIDER' TEXT);"; // 7: provider
        db.execSQL(sql);
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'EDUCATION'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Education entity) {
        stmt.clearBindings();
 
        Integer _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        Integer person_id = entity.getPerson_id();
        if (person_id != null) {
            stmt.bindLong(2, person_id);
        }
 
        String school_name = entity.getSchool_name();
        if (school_name != null) {
            stmt.bindString(3, school_name);
        }
 
        String school_id = entity.getSchool_id();
        if (school_id != null) {
            stmt.bindString(4, school_id);
        }
 
        String year_name = entity.getYear_name();
        if (year_name != null) {
            stmt.bindString(5, year_name);
        }
 
        String year_id = entity.getYear_id();
        if (year_id != null) {
            stmt.bindString(6, year_id);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(7, type);
        }
 
        String provider = entity.getProvider();
        if (provider != null) {
            stmt.bindString(8, provider);
        }
    }

    /** @inheritdoc */
    @Override
    public Integer readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Education readEntity(Cursor cursor, int offset) {
        Education entity = new Education( //
            cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // person_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // school_name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // school_id
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // year_name
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // year_id
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // type
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // provider
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Education entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0));
        entity.setPerson_id(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setSchool_name(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSchool_id(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setYear_name(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setYear_id(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setType(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setProvider(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    @Override
    protected Integer updateKeyAfterInsert(Education entity, long rowId) {
        // TODO XXX Only Long PKs are supported currently
        return null;
    }
    
    /** @inheritdoc */
    @Override
    public Integer getKey(Education entity) {
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
    
    /** Internal query to resolve the "education" to-many relationship of Person. */
    public synchronized List<Education> _queryPerson_Education(Integer person_id) {
        if (person_EducationQuery == null) {
            QueryBuilder<Education> queryBuilder = queryBuilder();
            queryBuilder.where(Properties.Person_id.eq(person_id));
            person_EducationQuery = queryBuilder.build();
        } else {
            person_EducationQuery.setParameter(0, person_id);
        }
        return person_EducationQuery.list();
    }

}
