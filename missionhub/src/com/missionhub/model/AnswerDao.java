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

import com.missionhub.model.Answer;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ANSWER.
*/
public class AnswerDao extends AbstractDao<Answer, Long> {

    public static final String TABLENAME = "ANSWER";

    /**
     * Properties of entity Answer.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Answer_sheet_id = new Property(1, Long.class, "answer_sheet_id", false, "ANSWER_SHEET_ID");
        public final static Property Question_id = new Property(2, Long.class, "question_id", false, "QUESTION_ID");
        public final static Property Value = new Property(3, String.class, "value", false, "VALUE");
    };

    private DaoSession daoSession;

    private Query<Answer> answerSheet_AnswerListQuery;

    public AnswerDao(DaoConfig config) {
        super(config);
    }
    
    public AnswerDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ANSWER' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'ANSWER_SHEET_ID' INTEGER," + // 1: answer_sheet_id
                "'QUESTION_ID' INTEGER," + // 2: question_id
                "'VALUE' TEXT);"); // 3: value
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ANSWER'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Answer entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long answer_sheet_id = entity.getAnswer_sheet_id();
        if (answer_sheet_id != null) {
            stmt.bindLong(2, answer_sheet_id);
        }
 
        Long question_id = entity.getQuestion_id();
        if (question_id != null) {
            stmt.bindLong(3, question_id);
        }
 
        String value = entity.getValue();
        if (value != null) {
            stmt.bindString(4, value);
        }
    }

    @Override
    protected void attachEntity(Answer entity) {
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
    public Answer readEntity(Cursor cursor, int offset) {
        Answer entity = new Answer( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // answer_sheet_id
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // question_id
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // value
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Answer entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setAnswer_sheet_id(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setQuestion_id(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setValue(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Answer entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Answer entity) {
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
    
    /** Internal query to resolve the "answerList" to-many relationship of AnswerSheet. */
    public List<Answer> _queryAnswerSheet_AnswerList(Long answer_sheet_id) {
        synchronized (this) {
            if (answerSheet_AnswerListQuery == null) {
                QueryBuilder<Answer> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.Answer_sheet_id.eq(null));
                answerSheet_AnswerListQuery = queryBuilder.build();
            }
        }
        Query<Answer> query = answerSheet_AnswerListQuery.forCurrentThread();
        query.setParameter(0, answer_sheet_id);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getAnswerSheetDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getQuestionDao().getAllColumns());
            builder.append(" FROM ANSWER T");
            builder.append(" LEFT JOIN ANSWER_SHEET T0 ON T.'ANSWER_SHEET_ID'=T0.'_id'");
            builder.append(" LEFT JOIN QUESTION T1 ON T.'QUESTION_ID'=T1.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Answer loadCurrentDeep(Cursor cursor, boolean lock) {
        Answer entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        AnswerSheet answerSheet = loadCurrentOther(daoSession.getAnswerSheetDao(), cursor, offset);
        entity.setAnswerSheet(answerSheet);
        offset += daoSession.getAnswerSheetDao().getAllColumns().length;

        Question question = loadCurrentOther(daoSession.getQuestionDao(), cursor, offset);
        entity.setQuestion(question);

        return entity;    
    }

    public Answer loadDeep(Long key) {
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
    public List<Answer> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Answer> list = new ArrayList<Answer>(count);
        
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
    
    protected List<Answer> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Answer> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
