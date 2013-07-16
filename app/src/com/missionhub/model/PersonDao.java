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

import com.missionhub.model.Person;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table PERSON.
*/
public class PersonDao extends AbstractDao<Person, Long> {

    public static final String TABLENAME = "PERSON";

    /**
     * Properties of entity Person.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property First_name = new Property(1, String.class, "first_name", false, "FIRST_NAME");
        public final static Property Last_name = new Property(2, String.class, "last_name", false, "LAST_NAME");
        public final static Property Gender = new Property(3, String.class, "gender", false, "GENDER");
        public final static Property Campus = new Property(4, String.class, "campus", false, "CAMPUS");
        public final static Property Year_in_school = new Property(5, String.class, "year_in_school", false, "YEAR_IN_SCHOOL");
        public final static Property Major = new Property(6, String.class, "major", false, "MAJOR");
        public final static Property Minor = new Property(7, String.class, "minor", false, "MINOR");
        public final static Property Birth_date = new Property(8, String.class, "birth_date", false, "BIRTH_DATE");
        public final static Property Date_became_christian = new Property(9, String.class, "date_became_christian", false, "DATE_BECAME_CHRISTIAN");
        public final static Property Graduation_date = new Property(10, String.class, "graduation_date", false, "GRADUATION_DATE");
        public final static Property Picture = new Property(11, String.class, "picture", false, "PICTURE");
        public final static Property User_id = new Property(12, Long.class, "user_id", false, "USER_ID");
        public final static Property Fb_uid = new Property(13, Long.class, "fb_uid", false, "FB_UID");
        public final static Property Updated_at = new Property(14, String.class, "updated_at", false, "UPDATED_AT");
        public final static Property Created_at = new Property(15, String.class, "created_at", false, "CREATED_AT");
    };

    private DaoSession daoSession;


    public PersonDao(DaoConfig config) {
        super(config);
    }
    
    public PersonDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'PERSON' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'FIRST_NAME' TEXT," + // 1: first_name
                "'LAST_NAME' TEXT," + // 2: last_name
                "'GENDER' TEXT," + // 3: gender
                "'CAMPUS' TEXT," + // 4: campus
                "'YEAR_IN_SCHOOL' TEXT," + // 5: year_in_school
                "'MAJOR' TEXT," + // 6: major
                "'MINOR' TEXT," + // 7: minor
                "'BIRTH_DATE' TEXT," + // 8: birth_date
                "'DATE_BECAME_CHRISTIAN' TEXT," + // 9: date_became_christian
                "'GRADUATION_DATE' TEXT," + // 10: graduation_date
                "'PICTURE' TEXT," + // 11: picture
                "'USER_ID' INTEGER," + // 12: user_id
                "'FB_UID' INTEGER," + // 13: fb_uid
                "'UPDATED_AT' TEXT," + // 14: updated_at
                "'CREATED_AT' TEXT);"); // 15: created_at
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'PERSON'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Person entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String first_name = entity.getFirst_name();
        if (first_name != null) {
            stmt.bindString(2, first_name);
        }
 
        String last_name = entity.getLast_name();
        if (last_name != null) {
            stmt.bindString(3, last_name);
        }
 
        String gender = entity.getGender();
        if (gender != null) {
            stmt.bindString(4, gender);
        }
 
        String campus = entity.getCampus();
        if (campus != null) {
            stmt.bindString(5, campus);
        }
 
        String year_in_school = entity.getYear_in_school();
        if (year_in_school != null) {
            stmt.bindString(6, year_in_school);
        }
 
        String major = entity.getMajor();
        if (major != null) {
            stmt.bindString(7, major);
        }
 
        String minor = entity.getMinor();
        if (minor != null) {
            stmt.bindString(8, minor);
        }
 
        String birth_date = entity.getBirth_date();
        if (birth_date != null) {
            stmt.bindString(9, birth_date);
        }
 
        String date_became_christian = entity.getDate_became_christian();
        if (date_became_christian != null) {
            stmt.bindString(10, date_became_christian);
        }
 
        String graduation_date = entity.getGraduation_date();
        if (graduation_date != null) {
            stmt.bindString(11, graduation_date);
        }
 
        String picture = entity.getPicture();
        if (picture != null) {
            stmt.bindString(12, picture);
        }
 
        Long user_id = entity.getUser_id();
        if (user_id != null) {
            stmt.bindLong(13, user_id);
        }
 
        Long fb_uid = entity.getFb_uid();
        if (fb_uid != null) {
            stmt.bindLong(14, fb_uid);
        }
 
        String updated_at = entity.getUpdated_at();
        if (updated_at != null) {
            stmt.bindString(15, updated_at);
        }
 
        String created_at = entity.getCreated_at();
        if (created_at != null) {
            stmt.bindString(16, created_at);
        }
    }

    @Override
    protected void attachEntity(Person entity) {
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
    public Person readEntity(Cursor cursor, int offset) {
        Person entity = new Person( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // first_name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // last_name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // gender
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // campus
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // year_in_school
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // major
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // minor
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // birth_date
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // date_became_christian
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // graduation_date
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // picture
            cursor.isNull(offset + 12) ? null : cursor.getLong(offset + 12), // user_id
            cursor.isNull(offset + 13) ? null : cursor.getLong(offset + 13), // fb_uid
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // updated_at
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15) // created_at
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Person entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setFirst_name(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setLast_name(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setGender(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCampus(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setYear_in_school(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setMajor(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setMinor(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setBirth_date(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setDate_became_christian(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setGraduation_date(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setPicture(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setUser_id(cursor.isNull(offset + 12) ? null : cursor.getLong(offset + 12));
        entity.setFb_uid(cursor.isNull(offset + 13) ? null : cursor.getLong(offset + 13));
        entity.setUpdated_at(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setCreated_at(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Person entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Person entity) {
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
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getUserDao().getAllColumns());
            builder.append(" FROM PERSON T");
            builder.append(" LEFT JOIN USER T0 ON T.'USER_ID'=T0.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Person loadCurrentDeep(Cursor cursor, boolean lock) {
        Person entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        User user = loadCurrentOther(daoSession.getUserDao(), cursor, offset);
        entity.setUser(user);

        return entity;    
    }

    public Person loadDeep(Long key) {
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
    public List<Person> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Person> list = new ArrayList<Person>(count);
        
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
    
    protected List<Person> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Person> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
