package com.missionhub.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

import com.missionhub.model.UserDao;
import com.missionhub.model.PersonDao;
import com.missionhub.model.AddressDao;
import com.missionhub.model.EmailAddressDao;
import com.missionhub.model.PhoneNumberDao;
import com.missionhub.model.OrganizationDao;
import com.missionhub.model.LabelDao;
import com.missionhub.model.PermissionDao;
import com.missionhub.model.OrganizationalLabelDao;
import com.missionhub.model.OrganizationalPermissionDao;
import com.missionhub.model.ContactAssignmentDao;
import com.missionhub.model.InteractionDao;
import com.missionhub.model.InteractionInitiatorDao;
import com.missionhub.model.InteractionTypeDao;
import com.missionhub.model.QuestionDao;
import com.missionhub.model.SurveyDao;
import com.missionhub.model.SmsKeywordDao;
import com.missionhub.model.AnswerSheetDao;
import com.missionhub.model.AnswerDao;
import com.missionhub.model.SettingDao;
import com.missionhub.model.UserSettingDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 2): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 2;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        UserDao.createTable(db, ifNotExists);
        PersonDao.createTable(db, ifNotExists);
        AddressDao.createTable(db, ifNotExists);
        EmailAddressDao.createTable(db, ifNotExists);
        PhoneNumberDao.createTable(db, ifNotExists);
        OrganizationDao.createTable(db, ifNotExists);
        LabelDao.createTable(db, ifNotExists);
        PermissionDao.createTable(db, ifNotExists);
        OrganizationalLabelDao.createTable(db, ifNotExists);
        OrganizationalPermissionDao.createTable(db, ifNotExists);
        ContactAssignmentDao.createTable(db, ifNotExists);
        InteractionDao.createTable(db, ifNotExists);
        InteractionInitiatorDao.createTable(db, ifNotExists);
        InteractionTypeDao.createTable(db, ifNotExists);
        QuestionDao.createTable(db, ifNotExists);
        SurveyDao.createTable(db, ifNotExists);
        SmsKeywordDao.createTable(db, ifNotExists);
        AnswerSheetDao.createTable(db, ifNotExists);
        AnswerDao.createTable(db, ifNotExists);
        SettingDao.createTable(db, ifNotExists);
        UserSettingDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        UserDao.dropTable(db, ifExists);
        PersonDao.dropTable(db, ifExists);
        AddressDao.dropTable(db, ifExists);
        EmailAddressDao.dropTable(db, ifExists);
        PhoneNumberDao.dropTable(db, ifExists);
        OrganizationDao.dropTable(db, ifExists);
        LabelDao.dropTable(db, ifExists);
        PermissionDao.dropTable(db, ifExists);
        OrganizationalLabelDao.dropTable(db, ifExists);
        OrganizationalPermissionDao.dropTable(db, ifExists);
        ContactAssignmentDao.dropTable(db, ifExists);
        InteractionDao.dropTable(db, ifExists);
        InteractionInitiatorDao.dropTable(db, ifExists);
        InteractionTypeDao.dropTable(db, ifExists);
        QuestionDao.dropTable(db, ifExists);
        SurveyDao.dropTable(db, ifExists);
        SmsKeywordDao.dropTable(db, ifExists);
        AnswerSheetDao.dropTable(db, ifExists);
        AnswerDao.dropTable(db, ifExists);
        SettingDao.dropTable(db, ifExists);
        UserSettingDao.dropTable(db, ifExists);
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
        registerDaoClass(UserDao.class);
        registerDaoClass(PersonDao.class);
        registerDaoClass(AddressDao.class);
        registerDaoClass(EmailAddressDao.class);
        registerDaoClass(PhoneNumberDao.class);
        registerDaoClass(OrganizationDao.class);
        registerDaoClass(LabelDao.class);
        registerDaoClass(PermissionDao.class);
        registerDaoClass(OrganizationalLabelDao.class);
        registerDaoClass(OrganizationalPermissionDao.class);
        registerDaoClass(ContactAssignmentDao.class);
        registerDaoClass(InteractionDao.class);
        registerDaoClass(InteractionInitiatorDao.class);
        registerDaoClass(InteractionTypeDao.class);
        registerDaoClass(QuestionDao.class);
        registerDaoClass(SurveyDao.class);
        registerDaoClass(SmsKeywordDao.class);
        registerDaoClass(AnswerSheetDao.class);
        registerDaoClass(AnswerDao.class);
        registerDaoClass(SettingDao.class);
        registerDaoClass(UserSettingDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}