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

import com.missionhub.model.Address;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ADDRESS.
*/
public class AddressDao extends AbstractDao<Address, Long> {

    public static final String TABLENAME = "ADDRESS";

    /**
     * Properties of entity Address.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Person_id = new Property(1, Long.class, "person_id", false, "PERSON_ID");
        public final static Property Address1 = new Property(2, String.class, "address1", false, "ADDRESS1");
        public final static Property Address2 = new Property(3, String.class, "address2", false, "ADDRESS2");
        public final static Property City = new Property(4, String.class, "city", false, "CITY");
        public final static Property State = new Property(5, String.class, "state", false, "STATE");
        public final static Property Country = new Property(6, String.class, "country", false, "COUNTRY");
        public final static Property Zip = new Property(7, String.class, "zip", false, "ZIP");
        public final static Property Address_type = new Property(8, String.class, "address_type", false, "ADDRESS_TYPE");
    };

    private DaoSession daoSession;

    private Query<Address> person_AddressListQuery;

    public AddressDao(DaoConfig config) {
        super(config);
    }
    
    public AddressDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ADDRESS' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'PERSON_ID' INTEGER," + // 1: person_id
                "'ADDRESS1' TEXT," + // 2: address1
                "'ADDRESS2' TEXT," + // 3: address2
                "'CITY' TEXT," + // 4: city
                "'STATE' TEXT," + // 5: state
                "'COUNTRY' TEXT," + // 6: country
                "'ZIP' TEXT," + // 7: zip
                "'ADDRESS_TYPE' TEXT);"); // 8: address_type
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ADDRESS'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Address entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long person_id = entity.getPerson_id();
        if (person_id != null) {
            stmt.bindLong(2, person_id);
        }
 
        String address1 = entity.getAddress1();
        if (address1 != null) {
            stmt.bindString(3, address1);
        }
 
        String address2 = entity.getAddress2();
        if (address2 != null) {
            stmt.bindString(4, address2);
        }
 
        String city = entity.getCity();
        if (city != null) {
            stmt.bindString(5, city);
        }
 
        String state = entity.getState();
        if (state != null) {
            stmt.bindString(6, state);
        }
 
        String country = entity.getCountry();
        if (country != null) {
            stmt.bindString(7, country);
        }
 
        String zip = entity.getZip();
        if (zip != null) {
            stmt.bindString(8, zip);
        }
 
        String address_type = entity.getAddress_type();
        if (address_type != null) {
            stmt.bindString(9, address_type);
        }
    }

    @Override
    protected void attachEntity(Address entity) {
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
    public Address readEntity(Cursor cursor, int offset) {
        Address entity = new Address( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // person_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // address1
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // address2
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // city
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // state
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // country
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // zip
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) // address_type
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Address entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPerson_id(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setAddress1(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAddress2(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCity(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setState(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setCountry(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setZip(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setAddress_type(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Address entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Address entity) {
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
    
    /** Internal query to resolve the "addressList" to-many relationship of Person. */
    public List<Address> _queryPerson_AddressList(Long person_id) {
        synchronized (this) {
            if (person_AddressListQuery == null) {
                QueryBuilder<Address> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.Person_id.eq(null));
                person_AddressListQuery = queryBuilder.build();
            }
        }
        Query<Address> query = person_AddressListQuery.forCurrentThread();
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
            builder.append(" FROM ADDRESS T");
            builder.append(" LEFT JOIN PERSON T0 ON T.'PERSON_ID'=T0.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Address loadCurrentDeep(Cursor cursor, boolean lock) {
        Address entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Person person = loadCurrentOther(daoSession.getPersonDao(), cursor, offset);
        entity.setPerson(person);

        return entity;    
    }

    public Address loadDeep(Long key) {
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
    public List<Address> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Address> list = new ArrayList<Address>(count);
        
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
    
    protected List<Address> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Address> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
