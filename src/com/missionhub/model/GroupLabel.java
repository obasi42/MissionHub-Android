package com.missionhub.model;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table GROUP_LABEL.
 */
public class GroupLabel {

	private Long id;
	private String name;
	private Long organization_id;
	private String ancestry;
	private java.util.Date created_at;
	private java.util.Date updated_at;
	private Integer group_labelings_count;

	/** Used to resolve relations */
	private transient DaoSession daoSession;

	/** Used for active entity operations. */
	private transient GroupLabelDao myDao;

	private Organization organization;
	private Long organization__resolvedKey;

	private List<GroupLabels> GroupLabels;

	public GroupLabel() {}

	public GroupLabel(final Long id) {
		this.id = id;
	}

	public GroupLabel(final Long id, final String name, final Long organization_id, final String ancestry, final java.util.Date created_at,
			final java.util.Date updated_at, final Integer group_labelings_count) {
		this.id = id;
		this.name = name;
		this.organization_id = organization_id;
		this.ancestry = ancestry;
		this.created_at = created_at;
		this.updated_at = updated_at;
		this.group_labelings_count = group_labelings_count;
	}

	/** called by internal mechanisms, do not call yourself. */
	public void __setDaoSession(final DaoSession daoSession) {
		this.daoSession = daoSession;
		myDao = daoSession != null ? daoSession.getGroupLabelDao() : null;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(final Long organization_id) {
		this.organization_id = organization_id;
	}

	public String getAncestry() {
		return ancestry;
	}

	public void setAncestry(final String ancestry) {
		this.ancestry = ancestry;
	}

	public java.util.Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(final java.util.Date created_at) {
		this.created_at = created_at;
	}

	public java.util.Date getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(final java.util.Date updated_at) {
		this.updated_at = updated_at;
	}

	public Integer getGroup_labelings_count() {
		return group_labelings_count;
	}

	public void setGroup_labelings_count(final Integer group_labelings_count) {
		this.group_labelings_count = group_labelings_count;
	}

	/** To-one relationship, resolved on first access. */
	public Organization getOrganization() {
		if (organization__resolvedKey == null || !organization__resolvedKey.equals(organization_id)) {
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			final OrganizationDao targetDao = daoSession.getOrganizationDao();
			organization = targetDao.load(organization_id);
			organization__resolvedKey = organization_id;
		}
		return organization;
	}

	public void setOrganization(final Organization organization) {
		this.organization = organization;
		organization_id = organization == null ? null : organization.getId();
		organization__resolvedKey = organization_id;
	}

	/**
	 * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted,
	 * make changes to the target entity.
	 */
	public synchronized List<GroupLabels> getGroupLabels() {
		if (GroupLabels == null) {
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			final GroupLabelsDao targetDao = daoSession.getGroupLabelsDao();
			GroupLabels = targetDao._queryGroupLabel_GroupLabels(id);
		}
		return GroupLabels;
	}

	/** Resets a to-many relationship, making the next get call to query for a fresh result. */
	public synchronized void resetGroupLabels() {
		GroupLabels = null;
	}

	/** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
	public void delete() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.delete(this);
	}

	/** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
	public void update() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.update(this);
	}

	/** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
	public void refresh() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.refresh(this);
	}

}
