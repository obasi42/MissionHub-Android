package com.missionhub.model;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.Query;
import de.greenrobot.dao.QueryBuilder;

import com.missionhub.model.QuestionChoice;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table QUESTION_CHOICE.
*/
public class QuestionChoiceDao extends AbstractDao<QuestionChoice, Long> {

    public static final String TABLENAME = "QUESTION_CHOICE";

    /**
     * Properties of entity QuestionChoice.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Question_id = new Property(1, Long.class, "question_id", false, "QUESTION_ID");
        public final static Property Choice = new Property(2, String.class, "choice", false, "CHOICE");
    };

    private Query<QuestionChoice> question_ChoicesQuery;

    public QuestionChoiceDao(DaoConfig config) {
        super(config);
    }
    
    public QuestionChoiceDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'QUESTION_CHOICE' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'QUESTION_ID' INTEGER," + // 1: question_id
                "'CHOICE' TEXT);"); // 2: choice
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'QUESTION_CHOICE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, QuestionChoice entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long question_id = entity.getQuestion_id();
        if (question_id != null) {
            stmt.bindLong(2, question_id);
        }
 
        String choice = entity.getChoice();
        if (choice != null) {
            stmt.bindString(3, choice);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public QuestionChoice readEntity(Cursor cursor, int offset) {
        QuestionChoice entity = new QuestionChoice( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // question_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // choice
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, QuestionChoice entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setQuestion_id(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setChoice(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(QuestionChoice entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(QuestionChoice entity) {
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
    
    /** Internal query to resolve the "choices" to-many relationship of Question. */
    public synchronized List<QuestionChoice> _queryQuestion_Choices(Long question_id) {
        if (question_ChoicesQuery == null) {
            QueryBuilder<QuestionChoice> queryBuilder = queryBuilder();
            queryBuilder.where(Properties.Question_id.eq(question_id));
            question_ChoicesQuery = queryBuilder.build();
        } else {
            question_ChoicesQuery.setParameter(0, question_id);
        }
        return question_ChoicesQuery.list();
    }

}
