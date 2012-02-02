package com.missionhub.api.model.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;

import com.missionhub.api.model.sql.QuestionChoice;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table QUESTION_CHOICE.
*/
public class QuestionChoiceDao extends AbstractDao<QuestionChoice, Integer> {

    public static final String TABLENAME = "QUESTION_CHOICE";

    public static class Properties {
        public final static Property _id = new Property(0, Integer.class, "_id", true, "_ID");
        public final static Property Question_id = new Property(1, Integer.class, "question_id", false, "QUESTION_ID");
        public final static Property Choice = new Property(2, String.class, "choice", false, "CHOICE");
    };


    public QuestionChoiceDao(DaoConfig config) {
        super(config);
    }
    
    public QuestionChoiceDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String sql = "CREATE TABLE " + (ifNotExists? "IF NOT EXISTS ": "") + "'QUESTION_CHOICE' (" + //
                "'_ID' INTEGER PRIMARY KEY ," + // 0: _id
                "'QUESTION_ID' INTEGER," + // 1: question_id
                "'CHOICE' TEXT);"; // 2: choice
        db.execSQL(sql);
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
 
        Integer _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        Integer question_id = entity.getQuestion_id();
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
    public Integer readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public QuestionChoice readEntity(Cursor cursor, int offset) {
        QuestionChoice entity = new QuestionChoice( //
            cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // question_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // choice
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, QuestionChoice entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0));
        entity.setQuestion_id(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setChoice(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected Integer updateKeyAfterInsert(QuestionChoice entity, long rowId) {
        // TODO XXX Only Long PKs are supported currently
        return null;
    }
    
    /** @inheritdoc */
    @Override
    public Integer getKey(QuestionChoice entity) {
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
    
}
