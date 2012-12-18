package com.missionhub.model;

import com.missionhub.model.DaoMaster.OpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class MissionHubOpenHelper extends OpenHelper {

	public static final String TAG = MissionHubOpenHelper.class.getSimpleName();

	public MissionHubOpenHelper(final Context context, final String name, final CursorFactory factory) {
		super(context, name, factory);
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		Log.i(TAG, "Creating tables for schema version " + DaoMaster.SCHEMA_VERSION);
		DaoMaster.createAllTables(db, true);
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		Log.i(TAG, "Upgrading schema from version " + oldVersion + " to " + newVersion);
		DaoMaster.dropAllTables(db, true);
		onCreate(db);
	}
}