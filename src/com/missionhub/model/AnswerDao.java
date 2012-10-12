package com.missionhub.model;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoConfig;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.Query;
import de.greenrobot.dao.QueryBuilder;
import de.greenrobot.dao.SqlUtils;

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
		public final static Property Person_id = new Property(1, Long.class, "person_id", false, "PERSON_ID");
		public final static Property Organization_id = new Property(2, Long.class, "organization_id", false, "ORGANIZATION_ID");
		public final static Property Question_id = new Property(3, Long.class, "question_id", false, "QUESTION_ID");
		public final static Property Answer = new Property(4, String.class, "answer", false, "ANSWER");
	};

	private DaoSession daoSession;

	private Query<Answer> person_AnswerListQuery;
	private Query<Answer> organization_AnswerListQuery;

	public AnswerDao(final DaoConfig config) {
		super(config);
	}

	public AnswerDao(final DaoConfig config, final DaoSession daoSession) {
		super(config, daoSession);
		this.daoSession = daoSession;
	}

	/** Creates the underlying database table. */
	public static void createTable(final SQLiteDatabase db, final boolean ifNotExists) {
		final String constraint = ifNotExists ? "IF NOT EXISTS " : "";
		db.execSQL("CREATE TABLE " + constraint + "'ANSWER' (" + //
				"'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
				"'PERSON_ID' INTEGER," + // 1: person_id
				"'ORGANIZATION_ID' INTEGER," + // 2: organization_id
				"'QUESTION_ID' INTEGER," + // 3: question_id
				"'ANSWER' TEXT);"); // 4: answer
	}

	/** Drops the underlying database table. */
	public static void dropTable(final SQLiteDatabase db, final boolean ifExists) {
		final String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ANSWER'";
		db.execSQL(sql);
	}

	/** @inheritdoc */
	@Override
	protected void bindValues(final SQLiteStatement stmt, final Answer entity) {
		stmt.clearBindings();

		final Long id = entity.getId();
		if (id != null) {
			stmt.bindLong(1, id);
		}

		final Long person_id = entity.getPerson_id();
		if (person_id != null) {
			stmt.bindLong(2, person_id);
		}

		final Long organization_id = entity.getOrganization_id();
		if (organization_id != null) {
			stmt.bindLong(3, organization_id);
		}

		final Long question_id = entity.getQuestion_id();
		if (question_id != null) {
			stmt.bindLong(4, question_id);
		}

		final String answer = entity.getAnswer();
		if (answer != null) {
			stmt.bindString(5, answer);
		}
	}

	@Override
	protected void attachEntity(final Answer entity) {
		super.attachEntity(entity);
		entity.__setDaoSession(daoSession);
	}

	/** @inheritdoc */
	@Override
	public Long readKey(final Cursor cursor, final int offset) {
		return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
	}

	/** @inheritdoc */
	@Override
	public Answer readEntity(final Cursor cursor, final int offset) {
		final Answer entity = new Answer( //
				cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
				cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // person_id
				cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // organization_id
				cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // question_id
				cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // answer
		);
		return entity;
	}

	/** @inheritdoc */
	@Override
	public void readEntity(final Cursor cursor, final Answer entity, final int offset) {
		entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
		entity.setPerson_id(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
		entity.setOrganization_id(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
		entity.setQuestion_id(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
		entity.setAnswer(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
	}

	/** @inheritdoc */
	@Override
	protected Long updateKeyAfterInsert(final Answer entity, final long rowId) {
		entity.setId(rowId);
		return rowId;
	}

	/** @inheritdoc */
	@Override
	public Long getKey(final Answer entity) {
		if (entity != null) {
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

	/** Internal query to resolve the "answerList" to-many relationship of Person. */
	public synchronized List<Answer> _queryPerson_AnswerList(final Long person_id) {
		if (person_AnswerListQuery == null) {
			final QueryBuilder<Answer> queryBuilder = queryBuilder();
			queryBuilder.where(Properties.Person_id.eq(person_id));
			person_AnswerListQuery = queryBuilder.build();
		} else {
			person_AnswerListQuery.setParameter(0, person_id);
		}
		return person_AnswerListQuery.list();
	}

	/** Internal query to resolve the "answerList" to-many relationship of Organization. */
	public synchronized List<Answer> _queryOrganization_AnswerList(final Long organization_id) {
		if (organization_AnswerListQuery == null) {
			final QueryBuilder<Answer> queryBuilder = queryBuilder();
			queryBuilder.where(Properties.Organization_id.eq(organization_id));
			organization_AnswerListQuery = queryBuilder.build();
		} else {
			organization_AnswerListQuery.setParameter(0, organization_id);
		}
		return organization_AnswerListQuery.list();
	}

	private String selectDeep;

	protected String getSelectDeep() {
		if (selectDeep == null) {
			final StringBuilder builder = new StringBuilder("SELECT ");
			SqlUtils.appendColumns(builder, "T", getAllColumns());
			builder.append(',');
			SqlUtils.appendColumns(builder, "T0", daoSession.getQuestionDao().getAllColumns());
			builder.append(" FROM ANSWER T");
			builder.append(" LEFT JOIN QUESTION T0 ON T.'QUESTION_ID'=T0.'_id'");
			builder.append(' ');
			selectDeep = builder.toString();
		}
		return selectDeep;
	}

	protected Answer loadCurrentDeep(final Cursor cursor, final boolean lock) {
		final Answer entity = loadCurrent(cursor, 0, lock);
		final int offset = getAllColumns().length;

		final Question question = loadCurrentOther(daoSession.getQuestionDao(), cursor, offset);
		entity.setQuestion(question);

		return entity;
	}

	public Answer loadDeep(final Long key) {
		assertSinglePk();
		if (key == null) {
			return null;
		}

		final StringBuilder builder = new StringBuilder(getSelectDeep());
		builder.append("WHERE ");
		SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
		final String sql = builder.toString();

		final String[] keyArray = new String[] { key.toString() };
		final Cursor cursor = db.rawQuery(sql, keyArray);

		try {
			final boolean available = cursor.moveToFirst();
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
	public List<Answer> loadAllDeepFromCursor(final Cursor cursor) {
		final int count = cursor.getCount();
		final List<Answer> list = new ArrayList<Answer>(count);

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

	protected List<Answer> loadDeepAllAndCloseCursor(final Cursor cursor) {
		try {
			return loadAllDeepFromCursor(cursor);
		} finally {
			cursor.close();
		}
	}

	/** A raw-style query where you can pass any WHERE clause and arguments. */
	public List<Answer> queryDeep(final String where, final String... selectionArg) {
		final Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
		return loadDeepAllAndCloseCursor(cursor);
	}

}
