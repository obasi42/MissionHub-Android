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
import de.greenrobot.dao.Query;
import de.greenrobot.dao.QueryBuilder;

import com.missionhub.api.model.sql.Question;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table QUESTION.
*/
public class QuestionDao extends AbstractDao<Question, Integer> {

    public static final String TABLENAME = "QUESTION";

    public static class Properties {
        public final static Property _id = new Property(0, Integer.class, "_id", true, "_ID");
        public final static Property Keyword_id = new Property(1, Integer.class, "keyword_id", false, "KEYWORD_ID");
        public final static Property Label = new Property(2, String.class, "label", false, "LABEL");
        public final static Property Kind = new Property(3, String.class, "kind", false, "KIND");
        public final static Property Style = new Property(4, String.class, "style", false, "STYLE");
        public final static Property Required = new Property(5, Boolean.class, "required", false, "REQUIRED");
        public final static Property Active = new Property(6, Boolean.class, "active", false, "ACTIVE");
        public final static Property Question_id = new Property(7, Integer.class, "question_id", false, "QUESTION_ID");
    };

    private DaoSession daoSession;

    private Query<Question> keyword_QuestionsQuery;
    private Query<Question> question_QuestionQuery;

    public QuestionDao(DaoConfig config) {
        super(config);
    }
    
    public QuestionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String sql = "CREATE TABLE " + (ifNotExists? "IF NOT EXISTS ": "") + "'QUESTION' (" + //
                "'_ID' INTEGER PRIMARY KEY ," + // 0: _id
                "'KEYWORD_ID' INTEGER," + // 1: keyword_id
                "'LABEL' TEXT," + // 2: label
                "'KIND' TEXT," + // 3: kind
                "'STYLE' TEXT," + // 4: style
                "'REQUIRED' INTEGER," + // 5: required
                "'ACTIVE' INTEGER," + // 6: active
                "'QUESTION_ID' INTEGER);"; // 7: question_id
        db.execSQL(sql);
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'QUESTION'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Question entity) {
        stmt.clearBindings();
 
        Integer _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        Integer keyword_id = entity.getKeyword_id();
        if (keyword_id != null) {
            stmt.bindLong(2, keyword_id);
        }
 
        String label = entity.getLabel();
        if (label != null) {
            stmt.bindString(3, label);
        }
 
        String kind = entity.getKind();
        if (kind != null) {
            stmt.bindString(4, kind);
        }
 
        String style = entity.getStyle();
        if (style != null) {
            stmt.bindString(5, style);
        }
 
        Boolean required = entity.getRequired();
        if (required != null) {
            stmt.bindLong(6, required ? 1l: 0l);
        }
 
        Boolean active = entity.getActive();
        if (active != null) {
            stmt.bindLong(7, active ? 1l: 0l);
        }
    }

    @Override
    protected void attachEntity(Question entity) {
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
    public Question readEntity(Cursor cursor, int offset) {
        Question entity = new Question( //
            cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // keyword_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // label
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // kind
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // style
            cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0, // required
            cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0 // active
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Question entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0));
        entity.setKeyword_id(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setLabel(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setKind(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setStyle(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setRequired(cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0);
        entity.setActive(cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0);
     }
    
    @Override
    protected Integer updateKeyAfterInsert(Question entity, long rowId) {
        // TODO XXX Only Long PKs are supported currently
        return null;
    }
    
    /** @inheritdoc */
    @Override
    public Integer getKey(Question entity) {
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
    
    /** Internal query to resolve the "questions" to-many relationship of Keyword. */
    public synchronized List<Question> _queryKeyword_Questions(Integer keyword_id) {
        if (keyword_QuestionsQuery == null) {
            QueryBuilder<Question> queryBuilder = queryBuilder();
            queryBuilder.where(Properties.Keyword_id.eq(keyword_id));
            keyword_QuestionsQuery = queryBuilder.build();
        } else {
            keyword_QuestionsQuery.setParameter(0, keyword_id);
        }
        return keyword_QuestionsQuery.list();
    }

    /** Internal query to resolve the "question" to-many relationship of Question. */
    public synchronized List<Question> _queryQuestion_Question(Integer question_id) {
        if (question_QuestionQuery == null) {
            QueryBuilder<Question> queryBuilder = queryBuilder();
            queryBuilder.where(Properties.Question_id.eq(question_id));
            question_QuestionQuery = queryBuilder.build();
        } else {
            question_QuestionQuery.setParameter(0, question_id);
        }
        return question_QuestionQuery.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getKeywordDao().getAllColumns());
            builder.append(" FROM QUESTION T");
            builder.append(" LEFT JOIN KEYWORD T0 ON T.'KEYWORD_ID'=T0.'_ID'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Question loadCurrentDeep(Cursor cursor, boolean lock) {
        Question entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Keyword keyword = loadCurrentOther(daoSession.getKeywordDao(), cursor, offset);
        entity.setKeyword(keyword);

        return entity;    
    }

    public Question loadDeep(Long key) {
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
    public List<Question> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Question> list = new ArrayList<Question>(count);
        
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
    
    protected List<Question> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Question> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
