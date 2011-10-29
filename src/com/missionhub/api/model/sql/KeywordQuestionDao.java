package com.missionhub.api.model.sql;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.SqlUtils;

import com.missionhub.api.model.sql.KeywordQuestion;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table KEYWORD_QUESTION (schema version 1).
*/
public class KeywordQuestionDao extends AbstractDao<KeywordQuestion, Integer> {

    public static final String TABLENAME = "KEYWORD_QUESTION";

    public static class Properties {
        public final static Property _id = new Property(0, Integer.class, "_id", true, "_ID");
        public final static Property Question_id = new Property(1, Integer.class, "question_id", false, "QUESTION_ID");
        public final static Property Keyword_id = new Property(2, Integer.class, "keyword_id", false, "KEYWORD_ID");
    };

    private DaoSession daoSession;


    public KeywordQuestionDao(DaoConfig config) {
        super(config);
    }
    
    public KeywordQuestionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String sql = "CREATE TABLE " + (ifNotExists? "IF NOT EXISTS ": "") + "'KEYWORD_QUESTION' (" + //
                "'_ID' INTEGER PRIMARY KEY ," + // 0: _id
                "'QUESTION_ID' INTEGER," + // 1: question_id
                "'KEYWORD_ID' INTEGER);"; // 2: keyword_id
        db.execSQL(sql);
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'KEYWORD_QUESTION'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, KeywordQuestion entity) {
        stmt.clearBindings();
 
        Integer _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        Integer question_id = entity.getQuestion_id();
        if (question_id != null) {
            stmt.bindLong(2, question_id);
        }
 
        Integer keyword_id = entity.getKeyword_id();
        if (keyword_id != null) {
            stmt.bindLong(3, keyword_id);
        }
    }

    @Override
    protected void attachEntity(KeywordQuestion entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Integer readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public KeywordQuestion readEntity(Cursor cursor, int offset) {
        KeywordQuestion entity = new KeywordQuestion( //
            cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // question_id
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2) // keyword_id
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, KeywordQuestion entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0));
        entity.setQuestion_id(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setKeyword_id(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
     }
    
    @Override
    protected Integer updateKeyAfterInsert(KeywordQuestion entity, long rowId) {
        // TODO XXX Only Long PKs are supported currently
        return null;
    }
    
    /** @inheritdoc */
    @Override
    public Integer getKey(KeywordQuestion entity) {
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
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getQuestionDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getKeywordDao().getAllColumns());
            builder.append(" FROM KEYWORD_QUESTION T");
            builder.append(" LEFT JOIN QUESTION T0 ON T.'QUESTION_ID'=T0.'_ID'");
            builder.append(" LEFT JOIN KEYWORD T1 ON T.'KEYWORD_ID'=T1.'_ID'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected KeywordQuestion loadCurrentDeep(Cursor cursor, boolean lock) {
        KeywordQuestion entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Question question = loadCurrentOther(daoSession.getQuestionDao(), cursor, offset);
        entity.setQuestion(question);
        offset += daoSession.getQuestionDao().getAllColumns().length;

        Keyword keyword = loadCurrentOther(daoSession.getKeywordDao(), cursor, offset);
        entity.setKeyword(keyword);

        return entity;    
    }

    public KeywordQuestion loadDeep(Long key) {
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
    public List<KeywordQuestion> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<KeywordQuestion> list = new ArrayList<KeywordQuestion>(count);
        
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
    
    protected List<KeywordQuestion> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<KeywordQuestion> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}