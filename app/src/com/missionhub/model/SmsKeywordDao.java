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

import com.missionhub.model.SmsKeyword;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table SMS_KEYWORD.
 */
public class SmsKeywordDao extends AbstractDao<SmsKeyword, Long> {

    public static final String TABLENAME = "SMS_KEYWORD";

    /**
     * Properties of entity SmsKeyword.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Keyword = new Property(1, String.class, "keyword", false, "KEYWORD");
        public final static Property Organization_id = new Property(2, Long.class, "organization_id", false, "ORGANIZATION_ID");
        public final static Property User_id = new Property(3, Long.class, "user_id", false, "USER_ID");
        public final static Property Explanation = new Property(4, String.class, "explanation", false, "EXPLANATION");
        public final static Property State = new Property(5, String.class, "state", false, "STATE");
        public final static Property Initial_response = new Property(6, String.class, "initial_response", false, "INITIAL_RESPONSE");
        public final static Property Survey_id = new Property(7, Long.class, "survey_id", false, "SURVEY_ID");
        public final static Property Updated_at = new Property(8, String.class, "updated_at", false, "UPDATED_AT");
        public final static Property Created_at = new Property(9, String.class, "created_at", false, "CREATED_AT");
    }

    ;

    private DaoSession daoSession;

    private Query<SmsKeyword> organization_KeywordsQuery;
    private Query<SmsKeyword> user_SmsKeywordListQuery;
    private Query<SmsKeyword> survey_SmsKeywordListQuery;

    public SmsKeywordDao(DaoConfig config) {
        super(config);
    }

    public SmsKeywordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'SMS_KEYWORD' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'KEYWORD' TEXT," + // 1: keyword
                "'ORGANIZATION_ID' INTEGER," + // 2: organization_id
                "'USER_ID' INTEGER," + // 3: user_id
                "'EXPLANATION' TEXT," + // 4: explanation
                "'STATE' TEXT," + // 5: state
                "'INITIAL_RESPONSE' TEXT," + // 6: initial_response
                "'SURVEY_ID' INTEGER," + // 7: survey_id
                "'UPDATED_AT' TEXT," + // 8: updated_at
                "'CREATED_AT' TEXT);"); // 9: created_at
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'SMS_KEYWORD'";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, SmsKeyword entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        String keyword = entity.getKeyword();
        if (keyword != null) {
            stmt.bindString(2, keyword);
        }

        Long organization_id = entity.getOrganization_id();
        if (organization_id != null) {
            stmt.bindLong(3, organization_id);
        }

        Long user_id = entity.getUser_id();
        if (user_id != null) {
            stmt.bindLong(4, user_id);
        }

        String explanation = entity.getExplanation();
        if (explanation != null) {
            stmt.bindString(5, explanation);
        }

        String state = entity.getState();
        if (state != null) {
            stmt.bindString(6, state);
        }

        String initial_response = entity.getInitial_response();
        if (initial_response != null) {
            stmt.bindString(7, initial_response);
        }

        Long survey_id = entity.getSurvey_id();
        if (survey_id != null) {
            stmt.bindLong(8, survey_id);
        }

        String updated_at = entity.getUpdated_at();
        if (updated_at != null) {
            stmt.bindString(9, updated_at);
        }

        String created_at = entity.getCreated_at();
        if (created_at != null) {
            stmt.bindString(10, created_at);
        }
    }

    @Override
    protected void attachEntity(SmsKeyword entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    public SmsKeyword readEntity(Cursor cursor, int offset) {
        SmsKeyword entity = new SmsKeyword( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // keyword
                cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // organization_id
                cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // user_id
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // explanation
                cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // state
                cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // initial_response
                cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // survey_id
                cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // updated_at
                cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // created_at
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, SmsKeyword entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setKeyword(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setOrganization_id(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setUser_id(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setExplanation(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setState(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setInitial_response(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setSurvey_id(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setUpdated_at(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setCreated_at(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(SmsKeyword entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(SmsKeyword entity) {
        if (entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

    /**
     * Internal query to resolve the "keywords" to-many relationship of Organization.
     */
    public List<SmsKeyword> _queryOrganization_Keywords(Long organization_id) {
        synchronized (this) {
            if (organization_KeywordsQuery == null) {
                QueryBuilder<SmsKeyword> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.Organization_id.eq(null));
                organization_KeywordsQuery = queryBuilder.build();
            }
        }
        Query<SmsKeyword> query = organization_KeywordsQuery.forCurrentThread();
        query.setParameter(0, organization_id);
        return query.list();
    }

    /**
     * Internal query to resolve the "smsKeywordList" to-many relationship of User.
     */
    public List<SmsKeyword> _queryUser_SmsKeywordList(Long user_id) {
        synchronized (this) {
            if (user_SmsKeywordListQuery == null) {
                QueryBuilder<SmsKeyword> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.User_id.eq(null));
                user_SmsKeywordListQuery = queryBuilder.build();
            }
        }
        Query<SmsKeyword> query = user_SmsKeywordListQuery.forCurrentThread();
        query.setParameter(0, user_id);
        return query.list();
    }

    /**
     * Internal query to resolve the "smsKeywordList" to-many relationship of Survey.
     */
    public List<SmsKeyword> _querySurvey_SmsKeywordList(Long survey_id) {
        synchronized (this) {
            if (survey_SmsKeywordListQuery == null) {
                QueryBuilder<SmsKeyword> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.Survey_id.eq(null));
                survey_SmsKeywordListQuery = queryBuilder.build();
            }
        }
        Query<SmsKeyword> query = survey_SmsKeywordListQuery.forCurrentThread();
        query.setParameter(0, survey_id);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getOrganizationDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getSurveyDao().getAllColumns());
            builder.append(" FROM SMS_KEYWORD T");
            builder.append(" LEFT JOIN ORGANIZATION T0 ON T.'ORGANIZATION_ID'=T0.'_id'");
            builder.append(" LEFT JOIN SURVEY T1 ON T.'SURVEY_ID'=T1.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }

    protected SmsKeyword loadCurrentDeep(Cursor cursor, boolean lock) {
        SmsKeyword entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Organization organization = loadCurrentOther(daoSession.getOrganizationDao(), cursor, offset);
        entity.setOrganization(organization);
        offset += daoSession.getOrganizationDao().getAllColumns().length;

        Survey survey = loadCurrentOther(daoSession.getSurveyDao(), cursor, offset);
        entity.setSurvey(survey);

        return entity;
    }

    public SmsKeyword loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();

        String[] keyArray = new String[]{key.toString()};
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

    /**
     * Reads all available rows from the given cursor and returns a list of new ImageTO objects.
     */
    public List<SmsKeyword> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<SmsKeyword> list = new ArrayList<SmsKeyword>(count);

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

    protected List<SmsKeyword> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }


    /**
     * A raw-style query where you can pass any WHERE clause and arguments.
     */
    public List<SmsKeyword> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }

}
