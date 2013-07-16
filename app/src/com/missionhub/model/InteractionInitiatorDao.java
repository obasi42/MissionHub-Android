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

import com.missionhub.model.InteractionInitiator;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table INTERACTION_INITIATOR.
*/
public class InteractionInitiatorDao extends AbstractDao<InteractionInitiator, Long> {

    public static final String TABLENAME = "INTERACTION_INITIATOR";

    /**
     * Properties of entity InteractionInitiator.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Person_id = new Property(1, Long.class, "person_id", false, "PERSON_ID");
        public final static Property Interaction_id = new Property(2, Long.class, "interaction_id", false, "INTERACTION_ID");
        public final static Property Updated_at = new Property(3, String.class, "updated_at", false, "UPDATED_AT");
        public final static Property Created_at = new Property(4, String.class, "created_at", false, "CREATED_AT");
    };

    private DaoSession daoSession;

    private Query<InteractionInitiator> person_InteractionInitiatorListQuery;
    private Query<InteractionInitiator> interaction_InteractionInitiatorListQuery;

    public InteractionInitiatorDao(DaoConfig config) {
        super(config);
    }
    
    public InteractionInitiatorDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'INTERACTION_INITIATOR' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'PERSON_ID' INTEGER," + // 1: person_id
                "'INTERACTION_ID' INTEGER," + // 2: interaction_id
                "'UPDATED_AT' TEXT," + // 3: updated_at
                "'CREATED_AT' TEXT);"); // 4: created_at
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'INTERACTION_INITIATOR'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, InteractionInitiator entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long person_id = entity.getPerson_id();
        if (person_id != null) {
            stmt.bindLong(2, person_id);
        }
 
        Long interaction_id = entity.getInteraction_id();
        if (interaction_id != null) {
            stmt.bindLong(3, interaction_id);
        }
 
        String updated_at = entity.getUpdated_at();
        if (updated_at != null) {
            stmt.bindString(4, updated_at);
        }
 
        String created_at = entity.getCreated_at();
        if (created_at != null) {
            stmt.bindString(5, created_at);
        }
    }

    @Override
    protected void attachEntity(InteractionInitiator entity) {
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
    public InteractionInitiator readEntity(Cursor cursor, int offset) {
        InteractionInitiator entity = new InteractionInitiator( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // person_id
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // interaction_id
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // updated_at
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // created_at
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, InteractionInitiator entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPerson_id(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setInteraction_id(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setUpdated_at(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCreated_at(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(InteractionInitiator entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(InteractionInitiator entity) {
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
    
    /** Internal query to resolve the "interactionInitiatorList" to-many relationship of Person. */
    public List<InteractionInitiator> _queryPerson_InteractionInitiatorList(Long person_id) {
        synchronized (this) {
            if (person_InteractionInitiatorListQuery == null) {
                QueryBuilder<InteractionInitiator> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.Person_id.eq(null));
                person_InteractionInitiatorListQuery = queryBuilder.build();
            }
        }
        Query<InteractionInitiator> query = person_InteractionInitiatorListQuery.forCurrentThread();
        query.setParameter(0, person_id);
        return query.list();
    }

    /** Internal query to resolve the "interactionInitiatorList" to-many relationship of Interaction. */
    public List<InteractionInitiator> _queryInteraction_InteractionInitiatorList(Long interaction_id) {
        synchronized (this) {
            if (interaction_InteractionInitiatorListQuery == null) {
                QueryBuilder<InteractionInitiator> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.Interaction_id.eq(null));
                interaction_InteractionInitiatorListQuery = queryBuilder.build();
            }
        }
        Query<InteractionInitiator> query = interaction_InteractionInitiatorListQuery.forCurrentThread();
        query.setParameter(0, interaction_id);
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
            SqlUtils.appendColumns(builder, "T1", daoSession.getInteractionDao().getAllColumns());
            builder.append(" FROM INTERACTION_INITIATOR T");
            builder.append(" LEFT JOIN PERSON T0 ON T.'PERSON_ID'=T0.'_id'");
            builder.append(" LEFT JOIN INTERACTION T1 ON T.'INTERACTION_ID'=T1.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected InteractionInitiator loadCurrentDeep(Cursor cursor, boolean lock) {
        InteractionInitiator entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Person person = loadCurrentOther(daoSession.getPersonDao(), cursor, offset);
        entity.setPerson(person);
        offset += daoSession.getPersonDao().getAllColumns().length;

        Interaction interaction = loadCurrentOther(daoSession.getInteractionDao(), cursor, offset);
        entity.setInteraction(interaction);

        return entity;    
    }

    public InteractionInitiator loadDeep(Long key) {
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
    public List<InteractionInitiator> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<InteractionInitiator> list = new ArrayList<InteractionInitiator>(count);
        
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
    
    protected List<InteractionInitiator> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<InteractionInitiator> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
