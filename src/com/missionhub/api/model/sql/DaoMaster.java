package com.missionhub.api.model.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.IdentityScopeType;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/**
 * Master of DAO (schema version 1): knows all DAOs.
 */
public class DaoMaster extends AbstractDaoMaster {
	public static final int SCHEMA_VERSION = 1;

	/** Creates underlying database table using DAOs. */
	public static void createAllTables(final SQLiteDatabase db, final boolean ifNotExists) {
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
	public static void dropAllTables(final SQLiteDatabase db, final boolean ifExists) {
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

		public OpenHelper(final Context context, final String name, final CursorFactory factory) {
			super(context, name, factory, SCHEMA_VERSION);
		}

		@Override
		public void onCreate(final SQLiteDatabase db) {
			Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
			createAllTables(db, false);
		}
	}

	/** WARNING: Drops all table on Upgrade! Use only during development. */
	public static class DevOpenHelper extends OpenHelper {
		public DevOpenHelper(final Context context, final String name, final CursorFactory factory) {
			super(context, name, factory);
		}

		@Override
		public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
			Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
			dropAllTables(db, true);
			onCreate(db);
		}
	}

	public DaoMaster(final SQLiteDatabase db) {
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

	@Override
	public DaoSession newSession() {
		return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
	}

	@Override
	public DaoSession newSession(final IdentityScopeType type) {
		return new DaoSession(db, type, daoConfigMap);
	}

}
