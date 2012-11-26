package com.missionhub.model;

import java.util.List;
import com.missionhub.model.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import java.util.ArrayList;
import java.util.Iterator;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import de.greenrobot.dao.QueryBuilder;
import com.missionhub.util.TreeDataStructure;

// KEEP INCLUDES END
/**
 * Entity mapped to table PERSON.
 */
public class Person {

	private Long id;
	private String name;
	private String gender;
	private String fb_id;
	private String picture;
	private String status;
	private String first_name;
	private String last_name;
	private String phone_number;
	private String email_address;
	private String birthday;
	private String locale;
	private String num_contacts;

	/** Used to resolve relations */
	private transient DaoSession daoSession;

	/** Used for active entity operations. */
	private transient PersonDao myDao;

	private List<Assignment> assigned_contacts;
	private List<Assignment> assigned_to_contacts;
	private List<Interest> interestList;
	private List<Education> educationList;
	private List<Location> locationList;
	private List<OrganizationalRole> organizationalRoleList;
	private List<GroupMembership> groups;
	private List<FollowupComment> followup_comments;
	private List<FollowupComment> posted_comments;
	private List<Answer> answerList;

	// KEEP FIELDS - put your custom fields here
	/** system labels. */
	public static final String LABEL_ADMIN = "admin";
	public static final String LABEL_CONTACT = "contact";
	public static final String LABEL_INVOLVED = "involved";
	public static final String LABEL_LEADER = "leader";
	public static final String LABEL_ALUMNI = "alumni";

	private long mPrimaryOrganizationId = -1;
	private SetMultimap<Long, String> mLabels; // organizationId, label
	private TreeDataStructure<Long> mOrganizationHierarchy;

	// KEEP FIELDS END

	public Person() {}

	public Person(Long id) {
		this.id = id;
	}

	public Person(Long id, String name, String gender, String fb_id, String picture, String status, String first_name, String last_name, String phone_number, String email_address, String birthday,
			String locale, String num_contacts) {
		this.id = id;
		this.name = name;
		this.gender = gender;
		this.fb_id = fb_id;
		this.picture = picture;
		this.status = status;
		this.first_name = first_name;
		this.last_name = last_name;
		this.phone_number = phone_number;
		this.email_address = email_address;
		this.birthday = birthday;
		this.locale = locale;
		this.num_contacts = num_contacts;
	}

	/** called by internal mechanisms, do not call yourself. */
	public void __setDaoSession(DaoSession daoSession) {
		this.daoSession = daoSession;
		myDao = daoSession != null ? daoSession.getPersonDao() : null;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getFb_id() {
		return fb_id;
	}

	public void setFb_id(String fb_id) {
		this.fb_id = fb_id;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public String getEmail_address() {
		return email_address;
	}

	public void setEmail_address(String email_address) {
		this.email_address = email_address;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getNum_contacts() {
		return num_contacts;
	}

	public void setNum_contacts(String num_contacts) {
		this.num_contacts = num_contacts;
	}

	/**
	 * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted,
	 * make changes to the target entity.
	 */
	public synchronized List<Assignment> getAssigned_contacts() {
		if (assigned_contacts == null) {
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			AssignmentDao targetDao = daoSession.getAssignmentDao();
			assigned_contacts = targetDao._queryPerson_Assigned_contacts(id);
		}
		return assigned_contacts;
	}

	/** Resets a to-many relationship, making the next get call to query for a fresh result. */
	public synchronized void resetAssigned_contacts() {
		assigned_contacts = null;
	}

	/**
	 * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted,
	 * make changes to the target entity.
	 */
	public synchronized List<Assignment> getAssigned_to_contacts() {
		if (assigned_to_contacts == null) {
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			AssignmentDao targetDao = daoSession.getAssignmentDao();
			assigned_to_contacts = targetDao._queryPerson_Assigned_to_contacts(id);
		}
		return assigned_to_contacts;
	}

	/** Resets a to-many relationship, making the next get call to query for a fresh result. */
	public synchronized void resetAssigned_to_contacts() {
		assigned_to_contacts = null;
	}

	/**
	 * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted,
	 * make changes to the target entity.
	 */
	public synchronized List<Interest> getInterestList() {
		if (interestList == null) {
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			InterestDao targetDao = daoSession.getInterestDao();
			interestList = targetDao._queryPerson_InterestList(id);
		}
		return interestList;
	}

	/** Resets a to-many relationship, making the next get call to query for a fresh result. */
	public synchronized void resetInterestList() {
		interestList = null;
	}

	/**
	 * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted,
	 * make changes to the target entity.
	 */
	public synchronized List<Education> getEducationList() {
		if (educationList == null) {
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			EducationDao targetDao = daoSession.getEducationDao();
			educationList = targetDao._queryPerson_EducationList(id);
		}
		return educationList;
	}

	/** Resets a to-many relationship, making the next get call to query for a fresh result. */
	public synchronized void resetEducationList() {
		educationList = null;
	}

	/**
	 * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted,
	 * make changes to the target entity.
	 */
	public synchronized List<Location> getLocationList() {
		if (locationList == null) {
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			LocationDao targetDao = daoSession.getLocationDao();
			locationList = targetDao._queryPerson_LocationList(id);
		}
		return locationList;
	}

	/** Resets a to-many relationship, making the next get call to query for a fresh result. */
	public synchronized void resetLocationList() {
		locationList = null;
	}

	/**
	 * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted,
	 * make changes to the target entity.
	 */
	public synchronized List<OrganizationalRole> getOrganizationalRoleList() {
		if (organizationalRoleList == null) {
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			OrganizationalRoleDao targetDao = daoSession.getOrganizationalRoleDao();
			organizationalRoleList = targetDao._queryPerson_OrganizationalRoleList(id);
		}
		return organizationalRoleList;
	}

	/** Resets a to-many relationship, making the next get call to query for a fresh result. */
	public synchronized void resetOrganizationalRoleList() {
		organizationalRoleList = null;
	}

	/**
	 * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted,
	 * make changes to the target entity.
	 */
	public synchronized List<GroupMembership> getGroups() {
		if (groups == null) {
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			GroupMembershipDao targetDao = daoSession.getGroupMembershipDao();
			groups = targetDao._queryPerson_Groups(id);
		}
		return groups;
	}

	/** Resets a to-many relationship, making the next get call to query for a fresh result. */
	public synchronized void resetGroups() {
		groups = null;
	}

	/**
	 * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted,
	 * make changes to the target entity.
	 */
	public synchronized List<FollowupComment> getFollowup_comments() {
		if (followup_comments == null) {
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			FollowupCommentDao targetDao = daoSession.getFollowupCommentDao();
			followup_comments = targetDao._queryPerson_Followup_comments(id);
		}
		return followup_comments;
	}

	/** Resets a to-many relationship, making the next get call to query for a fresh result. */
	public synchronized void resetFollowup_comments() {
		followup_comments = null;
	}

	/**
	 * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted,
	 * make changes to the target entity.
	 */
	public synchronized List<FollowupComment> getPosted_comments() {
		if (posted_comments == null) {
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			FollowupCommentDao targetDao = daoSession.getFollowupCommentDao();
			posted_comments = targetDao._queryPerson_Posted_comments(id);
		}
		return posted_comments;
	}

	/** Resets a to-many relationship, making the next get call to query for a fresh result. */
	public synchronized void resetPosted_comments() {
		posted_comments = null;
	}

	/**
	 * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted,
	 * make changes to the target entity.
	 */
	public synchronized List<Answer> getAnswerList() {
		if (answerList == null) {
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			AnswerDao targetDao = daoSession.getAnswerDao();
			answerList = targetDao._queryPerson_AnswerList(id);
		}
		return answerList;
	}

	/** Resets a to-many relationship, making the next get call to query for a fresh result. */
	public synchronized void resetAnswerList() {
		answerList = null;
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

	// KEEP METHODS - put your custom methods here
	public SetMultimap<Long, String> getLabelsCopy() {
		if (mLabels == null) {
			resetLabels();
		}
		return HashMultimap.create(getLabels());
	}

	private SetMultimap<Long, String> getLabels() {
		if (mLabels == null) {
			resetLabels();
		}
		return mLabels;
	}

	public void resetLabels() {
		final SetMultimap<Long, String> labelsTemp = Multimaps.synchronizedSetMultimap(HashMultimap.<Long, String> create());
		resetOrganizationalRoleList();
		try {
			final List<OrganizationalRole> roles = getOrganizationalRoleList();
			for (final OrganizationalRole role : roles) {
				role.refresh();
				labelsTemp.put(role.getOrganization_id(), role.getRole());

				if (role.getPrimary() || mPrimaryOrganizationId < 0) {
					mPrimaryOrganizationId = role.getOrganization_id();
				}
			}
		} catch (DaoException e) {
			resetLabels();
		}
		mLabels = labelsTemp;
	}

	/**
	 * Checks if a user has one of the given labels (role)
	 * 
	 * @param label
	 * @param organizationId
	 * @return true if they have the label
	 */
	public synchronized boolean hasLabel(final long organizationId, final String... label) {
		boolean has = false;
		for (final String l : label) {
			if (getLabels().containsEntry(organizationId, l)) {
				has = true;
			}
		}
		return has;
	}

	/**
	 * Checks if a user has all of the given labels (role)
	 * 
	 * @param label
	 * @param organizationId
	 * @return true if they have the label
	 */
	public synchronized boolean hasLabels(final long organizationId, final String... label) {
		boolean has = true;
		for (final String l : label) {
			if (!getLabels().containsEntry(organizationId, l)) {
				has = false;
			}
		}
		return has;
	}

	/**
	 * Returns true if the user is an admin or leader in the given organizationId
	 * 
	 * @param organizationId
	 * @return
	 */
	public synchronized boolean isAdminOrLeader(final long organizationId) {
		return isAdmin(organizationId) || isLeader(organizationId);
	}

	/**
	 * Returns true if the user is an admin in the given organizationId
	 * 
	 * @param organizationId
	 * @return
	 */
	public synchronized boolean isAdmin(final long organizationId) {
		return hasLabel(organizationId, LABEL_ADMIN);
	}

	/**
	 * Returns true if the user is a leader in the given organization
	 * 
	 * @param organizationId
	 * @return
	 */
	public synchronized boolean isLeader(final long organizationId) {
		return hasLabel(organizationId, LABEL_LEADER);
	}

	/**
	 * Resets the person's primary organization id
	 */
	public void resetOrganizationId() {
		resetLabels();
	}

	/**
	 * Returns the person's primary organization id
	 * 
	 * @return
	 */
	public long getPrimaryOrganizationId() {
		if (mPrimaryOrganizationId < 0) {
			resetLabels();
		}
		return mPrimaryOrganizationId;
	}

	public Organization getPrimaryOrganization() {
		if (daoSession == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		final OrganizationDao targetDao = daoSession.getOrganizationDao();
		return targetDao.load(getPrimaryOrganizationId());
	}

	public synchronized void resetOrganizationHierarchy() {
		mOrganizationHierarchy = null;
		getOrganizationHierarchy();
	}
	
	/**
	 * Returns a tree of the user's organizations
	 * 
	 * @return
	 */
	public synchronized TreeDataStructure<Long> getOrganizationHierarchy() {
		if (mOrganizationHierarchy != null) {
			return mOrganizationHierarchy;
		}

		if (daoSession == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		final QueryBuilder<OrganizationalRole> builder = daoSession.getOrganizationalRoleDao().queryBuilder();
		final List<String> adminRoles = new ArrayList<String>();
		adminRoles.add(Person.LABEL_ADMIN);
		adminRoles.add(Person.LABEL_LEADER);
		builder.where(OrganizationalRoleDao.Properties.Person_id.eq(getId()), OrganizationalRoleDao.Properties.Role.in(adminRoles));
		final List<OrganizationalRole> roles = builder.where(OrganizationalRoleDao.Properties.Person_id.eq(getId()), OrganizationalRoleDao.Properties.Role.in(adminRoles)).list();

		// build a tree from organization ancestry
		final TreeDataStructure<Long> tree = new TreeDataStructure<Long>(0l);

		final Iterator<OrganizationalRole> roleItr = roles.iterator();
		while (roleItr.hasNext()) {
			final OrganizationalRole role = roleItr.next();
			final Organization org = role.getOrganization();
			if (role.getOrganization().getAncestry() != null) {
				TreeDataStructure<Long> parent = tree;
				for (final String ancestor : role.getOrganization().getAncestry().trim().split("/")) {
					final Long a = Long.parseLong(ancestor);
					if (parent.getTree(a) == null) {
						parent = parent.addLeaf(a);
					} else {
						parent = parent.getTree(a);
					}
				}
				if (parent.getTree(org.getId()) == null) {
					parent.addLeaf(org.getId());
				}
			} else {
				tree.addLeaf(org.getId());
			}
		}

		mOrganizationHierarchy = tree;
		return mOrganizationHierarchy;
	}
	// KEEP METHODS END

}
