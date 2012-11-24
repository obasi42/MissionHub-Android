package com.missionhub.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

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
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Gender = new Property(2, String.class, "gender", false, "GENDER");
        public final static Property Fb_id = new Property(3, String.class, "fb_id", false, "FB_ID");
        public final static Property Picture = new Property(4, String.class, "picture", false, "PICTURE");
        public final static Property Status = new Property(5, String.class, "status", false, "STATUS");
        public final static Property First_name = new Property(6, String.class, "first_name", false, "FIRST_NAME");
        public final static Property Last_name = new Property(7, String.class, "last_name", false, "LAST_NAME");
        public final static Property Phone_number = new Property(8, String.class, "phone_number", false, "PHONE_NUMBER");
        public final static Property Email_address = new Property(9, String.class, "email_address", false, "EMAIL_ADDRESS");
        public final static Property Birthday = new Property(10, String.class, "birthday", false, "BIRTHDAY");
        public final static Property Locale = new Property(11, String.class, "locale", false, "LOCALE");
        public final static Property Num_contacts = new Property(12, String.class, "num_contacts", false, "NUM_CONTACTS");
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
                "'NAME' TEXT," + // 1: name
                "'GENDER' TEXT," + // 2: gender
                "'FB_ID' TEXT," + // 3: fb_id
                "'PICTURE' TEXT," + // 4: picture
                "'STATUS' TEXT," + // 5: status
                "'FIRST_NAME' TEXT," + // 6: first_name
                "'LAST_NAME' TEXT," + // 7: last_name
                "'PHONE_NUMBER' TEXT," + // 8: phone_number
                "'EMAIL_ADDRESS' TEXT," + // 9: email_address
                "'BIRTHDAY' TEXT," + // 10: birthday
                "'LOCALE' TEXT," + // 11: locale
                "'NUM_CONTACTS' TEXT);"); // 12: num_contacts
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
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String gender = entity.getGender();
        if (gender != null) {
            stmt.bindString(3, gender);
        }
 
        String fb_id = entity.getFb_id();
        if (fb_id != null) {
            stmt.bindString(4, fb_id);
        }
 
        String picture = entity.getPicture();
        if (picture != null) {
            stmt.bindString(5, picture);
        }
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(6, status);
        }
 
        String first_name = entity.getFirst_name();
        if (first_name != null) {
            stmt.bindString(7, first_name);
        }
 
        String last_name = entity.getLast_name();
        if (last_name != null) {
            stmt.bindString(8, last_name);
        }
 
        String phone_number = entity.getPhone_number();
        if (phone_number != null) {
            stmt.bindString(9, phone_number);
        }
 
        String email_address = entity.getEmail_address();
        if (email_address != null) {
            stmt.bindString(10, email_address);
        }
 
        String birthday = entity.getBirthday();
        if (birthday != null) {
            stmt.bindString(11, birthday);
        }
 
        String locale = entity.getLocale();
        if (locale != null) {
            stmt.bindString(12, locale);
        }
 
        String num_contacts = entity.getNum_contacts();
        if (num_contacts != null) {
            stmt.bindString(13, num_contacts);
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
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // gender
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // fb_id
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // picture
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // status
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // first_name
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // last_name
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // phone_number
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // email_address
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // birthday
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // locale
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12) // num_contacts
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Person entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setGender(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFb_id(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setPicture(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setStatus(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setFirst_name(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setLast_name(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setPhone_number(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setEmail_address(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setBirthday(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setLocale(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setNum_contacts(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
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
    
}
