package com.missionhub.api.model.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.IdentityScopeType;

import com.missionhub.api.model.sql.PersonDao;
import com.missionhub.api.model.sql.AssignmentDao;
import com.missionhub.api.model.sql.InterestDao;
import com.missionhub.api.model.sql.EducationDao;
import com.missionhub.api.model.sql.LocationDao;
import com.missionhub.api.model.sql.OrganizationDao;
import com.missionhub.api.model.sql.OrganizationalRoleDao;
import com.missionhub.api.model.sql.GroupDao;
import com.missionhub.api.model.sql.LabelDao;
import com.missionhub.api.model.sql.GroupMembershipDao;
import com.missionhub.api.model.sql.FollowupCommentDao;
import com.missionhub.api.model.sql.RejoicableDao;
import com.missionhub.api.model.sql.KeywordDao;
import com.missionhub.api.model.sql.QuestionDao;
import com.missionhub.api.model.sql.AnswerDao;
import com.missionhub.api.model.sql.QuestionChoiceDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 1): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 1;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        PersonDao.createTable(db, ifNotExists);
        AssignmentDao.createTable(db, ifNotExists);
        InterestDao.createTable(db, ifNotExists);
        EducationDao.createTable(db, ifNotExists);
        LocationDao.createTable(db, ifNotExists);
        OrganizationDao.createTable(db, ifNotExists);
        OrganizationalRoleDao.createTable(db, ifNotExists);
        GroupDao.createTable(db, ifNotExists);
        LabelDao.createTable(db, ifNotExists);
        GroupMembershipDao.createTable(db, ifNotExists);
        FollowupCommentDao.createTable(db, ifNotExists);
        RejoicableDao.createTable(db, ifNotExists);
        KeywordDao.createTable(db, ifNotExists);
        QuestionDao.createTable(db, ifNotExists);
        AnswerDao.createTable(db, ifNotExists);
        QuestionChoiceDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        PersonDao.dropTable(db, ifExists);
        AssignmentDao.dropTable(db, ifExists);
        InterestDao.dropTable(db, ifExists);
        EducationDao.dropTable(db, ifExists);
        LocationDao.dropTable(db, ifExists);
        OrganizationDao.dropTable(db, ifExists);
        OrganizationalRoleDao.dropTable(db, ifExists);
        GroupDao.dropTable(db, ifExists);
        LabelDao.dropTable(db, ifExists);
        GroupMembershipDao.dropTable(db, ifExists);
        FollowupCommentDao.dropTable(db, ifExists);
        RejoicableDao.dropTable(db, ifExists);
        KeywordDao.dropTable(db, ifExists);
        QuestionDao.dropTable(db, ifExists);
        AnswerDao.dropTable(db, ifExists);
        QuestionChoiceDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(PersonDao.class);
        registerDaoClass(AssignmentDao.class);
        registerDaoClass(InterestDao.class);
        registerDaoClass(EducationDao.class);
        registerDaoClass(LocationDao.class);
        registerDaoClass(OrganizationDao.class);
        registerDaoClass(OrganizationalRoleDao.class);
        registerDaoClass(GroupDao.class);
        registerDaoClass(LabelDao.class);
        registerDaoClass(GroupMembershipDao.class);
        registerDaoClass(FollowupCommentDao.class);
        registerDaoClass(RejoicableDao.class);
        registerDaoClass(KeywordDao.class);
        registerDaoClass(QuestionDao.class);
        registerDaoClass(AnswerDao.class);
        registerDaoClass(QuestionChoiceDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}
