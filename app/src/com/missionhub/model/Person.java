package com.missionhub.model;

import android.view.View;

import java.util.*;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.missionhub.application.Application;
import com.missionhub.application.Session;
import com.missionhub.model.gson.GEmailAddress;
import com.missionhub.model.gson.GPerson;
import com.missionhub.model.gson.GPhoneNumber;
import com.missionhub.util.IntentHelper;
import com.missionhub.util.TreeDataStructure;
import com.missionhub.util.U;
import com.missionhub.util.U.FollowupStatus;
import com.missionhub.util.U.Gender;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.WhereCondition;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
// KEEP INCLUDES END

/**
 * Entity mapped to table PERSON.
 */
public class Person {

    private Long id;
    private String first_name;
    private String last_name;
    private String gender;
    private String campus;
    private String year_in_school;
    private String major;
    private String minor;
    private java.util.Date birth_date;
    private java.util.Date date_became_christian;
    private java.util.Date graduation_date;
    private String picture;
    private Long user_id;
    private Long fb_uid;
    private java.util.Date updated_at;
    private java.util.Date created_at;

    /**
     * Used to resolve relations
     */
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    private transient PersonDao myDao;

    private User user;
    private Long user__resolvedKey;

    private List<Address> addressList;
    private List<EmailAddress> emailAddressList;
    private List<PhoneNumber> phoneNumberList;
    private List<OrganizationalRole> organizationalRoleList;
    private List<ContactAssignment> assigned_to;
    private List<ContactAssignment> assigned_to_me;
    private List<FollowupComment> comments_on_me;
    private List<FollowupComment> followup_comments;
    private List<Rejoicable> rejoicableList;
    private List<AnswerSheet> answerSheetList;

    // KEEP FIELDS - put your custom fields here
    private SetMultimap<Long, Long> mLabels; // organizationId, label
    private SetMultimap<Long, RoleEntry> mRoleCache;
    private TreeDataStructure<Long> mOrganizationHierarchy;
    private Map<Long, FollowupStatus> mStatuses;
    private EmailAddress mPrimaryEmailAddress;
    private PhoneNumber mPrimaryPhoneNumber;
    private Map<Long, ContactAssignment> mContactAssignments;
    private PersonViewCache mPersonViewCache;
    // KEEP FIELDS END

    public Person() {
    }

    public Person(Long id) {
        this.id = id;
    }

    public Person(Long id, String first_name, String last_name, String gender, String campus, String year_in_school, String major, String minor, java.util.Date birth_date, java.util.Date date_became_christian, java.util.Date graduation_date, String picture, Long user_id, Long fb_uid, java.util.Date updated_at, java.util.Date created_at) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.campus = campus;
        this.year_in_school = year_in_school;
        this.major = major;
        this.minor = minor;
        this.birth_date = birth_date;
        this.date_became_christian = date_became_christian;
        this.graduation_date = graduation_date;
        this.picture = picture;
        this.user_id = user_id;
        this.fb_uid = fb_uid;
        this.updated_at = updated_at;
        this.created_at = created_at;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getYear_in_school() {
        return year_in_school;
    }

    public void setYear_in_school(String year_in_school) {
        this.year_in_school = year_in_school;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public java.util.Date getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(java.util.Date birth_date) {
        this.birth_date = birth_date;
    }

    public java.util.Date getDate_became_christian() {
        return date_became_christian;
    }

    public void setDate_became_christian(java.util.Date date_became_christian) {
        this.date_became_christian = date_became_christian;
    }

    public java.util.Date getGraduation_date() {
        return graduation_date;
    }

    public void setGraduation_date(java.util.Date graduation_date) {
        this.graduation_date = graduation_date;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getFb_uid() {
        return fb_uid;
    }

    public void setFb_uid(Long fb_uid) {
        this.fb_uid = fb_uid;
    }

    public java.util.Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(java.util.Date updated_at) {
        this.updated_at = updated_at;
    }

    public java.util.Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(java.util.Date created_at) {
        this.created_at = created_at;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    public User getUser() {
        Long __key = this.user_id;
        if (user__resolvedKey == null || !user__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User userNew = targetDao.load(__key);
            synchronized (this) {
                user = userNew;
                user__resolvedKey = __key;
            }
        }
        return user;
    }

    public void setUser(User user) {
        synchronized (this) {
            this.user = user;
            user_id = user == null ? null : user.getId();
            user__resolvedKey = user_id;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity.
     */
    public List<Address> getAddressList() {
        if (addressList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AddressDao targetDao = daoSession.getAddressDao();
            List<Address> addressListNew = targetDao._queryPerson_AddressList(id);
            synchronized (this) {
                if (addressList == null) {
                    addressList = addressListNew;
                }
            }
        }
        return addressList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    public synchronized void resetAddressList() {
        addressList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity.
     */
    public List<EmailAddress> getEmailAddressList() {
        if (emailAddressList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            EmailAddressDao targetDao = daoSession.getEmailAddressDao();
            List<EmailAddress> emailAddressListNew = targetDao._queryPerson_EmailAddressList(id);
            synchronized (this) {
                if (emailAddressList == null) {
                    emailAddressList = emailAddressListNew;
                }
            }
        }
        return emailAddressList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    public synchronized void resetEmailAddressList() {
        emailAddressList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity.
     */
    public List<PhoneNumber> getPhoneNumberList() {
        if (phoneNumberList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PhoneNumberDao targetDao = daoSession.getPhoneNumberDao();
            List<PhoneNumber> phoneNumberListNew = targetDao._queryPerson_PhoneNumberList(id);
            synchronized (this) {
                if (phoneNumberList == null) {
                    phoneNumberList = phoneNumberListNew;
                }
            }
        }
        return phoneNumberList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    public synchronized void resetPhoneNumberList() {
        phoneNumberList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity.
     */
    public List<OrganizationalRole> getOrganizationalRoleList() {
        if (organizationalRoleList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            OrganizationalRoleDao targetDao = daoSession.getOrganizationalRoleDao();
            List<OrganizationalRole> organizationalRoleListNew = targetDao._queryPerson_OrganizationalRoleList(id);
            synchronized (this) {
                if (organizationalRoleList == null) {
                    organizationalRoleList = organizationalRoleListNew;
                }
            }
        }
        return organizationalRoleList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    public synchronized void resetOrganizationalRoleList() {
        organizationalRoleList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity.
     */
    public List<ContactAssignment> getAssigned_to() {
        if (assigned_to == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ContactAssignmentDao targetDao = daoSession.getContactAssignmentDao();
            List<ContactAssignment> assigned_toNew = targetDao._queryPerson_Assigned_to(id);
            synchronized (this) {
                if (assigned_to == null) {
                    assigned_to = assigned_toNew;
                }
            }
        }
        return assigned_to;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    public synchronized void resetAssigned_to() {
        assigned_to = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity.
     */
    public List<ContactAssignment> getAssigned_to_me() {
        if (assigned_to_me == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ContactAssignmentDao targetDao = daoSession.getContactAssignmentDao();
            List<ContactAssignment> assigned_to_meNew = targetDao._queryPerson_Assigned_to_me(id);
            synchronized (this) {
                if (assigned_to_me == null) {
                    assigned_to_me = assigned_to_meNew;
                }
            }
        }
        return assigned_to_me;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    public synchronized void resetAssigned_to_me() {
        assigned_to_me = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity.
     */
    public List<FollowupComment> getComments_on_me() {
        if (comments_on_me == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FollowupCommentDao targetDao = daoSession.getFollowupCommentDao();
            List<FollowupComment> comments_on_meNew = targetDao._queryPerson_Comments_on_me(id);
            synchronized (this) {
                if (comments_on_me == null) {
                    comments_on_me = comments_on_meNew;
                }
            }
        }
        return comments_on_me;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    public synchronized void resetComments_on_me() {
        comments_on_me = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity.
     */
    public List<FollowupComment> getFollowup_comments() {
        if (followup_comments == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            FollowupCommentDao targetDao = daoSession.getFollowupCommentDao();
            List<FollowupComment> followup_commentsNew = targetDao._queryPerson_Followup_comments(id);
            synchronized (this) {
                if (followup_comments == null) {
                    followup_comments = followup_commentsNew;
                }
            }
        }
        return followup_comments;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    public synchronized void resetFollowup_comments() {
        followup_comments = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity.
     */
    public List<Rejoicable> getRejoicableList() {
        if (rejoicableList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RejoicableDao targetDao = daoSession.getRejoicableDao();
            List<Rejoicable> rejoicableListNew = targetDao._queryPerson_RejoicableList(id);
            synchronized (this) {
                if (rejoicableList == null) {
                    rejoicableList = rejoicableListNew;
                }
            }
        }
        return rejoicableList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    public synchronized void resetRejoicableList() {
        rejoicableList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity.
     */
    public List<AnswerSheet> getAnswerSheetList() {
        if (answerSheetList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AnswerSheetDao targetDao = daoSession.getAnswerSheetDao();
            List<AnswerSheet> answerSheetListNew = targetDao._queryPerson_AnswerSheetList(id);
            synchronized (this) {
                if (answerSheetList == null) {
                    answerSheetList = answerSheetListNew;
                }
            }
        }
        return answerSheetList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    public synchronized void resetAnswerSheetList() {
        answerSheetList = null;
    }

    /**
     * Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context.
     */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context.
     */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context.
     */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    public synchronized SetMultimap<Long, Long> getLabels() {
        if (mLabels == null) {
            final SetMultimap<Long, Long> labelsTemp = Multimaps.synchronizedSetMultimap(HashMultimap.<Long, Long>create());
            resetOrganizationalRoleList();
            try {
                final List<OrganizationalRole> roles = getOrganizationalRoleList();
                for (final OrganizationalRole role : roles) {
                    role.refresh();
                    labelsTemp.put(role.getOrganization_id(), role.getRole_id());
                }
            } catch (final DaoException e) {
                getLabels();
            }
            mLabels = labelsTemp;
        }
        return mLabels;
    }

    public synchronized List<Long> getLables(long organizationId) {
        return new ArrayList<Long>(getLabels().get(organizationId));
    }

    public synchronized void resetLabels() {
        mLabels = null;
    }

    /**
     * Checks if a user has one of the given labels
     *
     * @param label
     * @param organizationId
     * @return true if they have the label
     */
    public synchronized boolean hasLabel(final long organizationId, final long... label) {
        boolean has = false;
        for (final long l : label) {
            if (getLabels().containsEntry(organizationId, l)) {
                has = true;
            }
        }
        return has;
    }

    public synchronized boolean hasRole(U.Role role) {
        return hasRole(role, Session.getInstance().getOrganizationId());
    }

    public static class RoleEntry extends AbstractMap.SimpleEntry<U.Role, Boolean> {
        public RoleEntry(U.Role role, Boolean permission) {
            super(role, permission);
        }
    }

    public synchronized boolean hasRole(U.Role role, final long organizationId) {
        if (mRoleCache == null) {
            mRoleCache = Multimaps.synchronizedSetMultimap(HashMultimap.<Long, RoleEntry>create());
        }

        if (mRoleCache.containsKey(organizationId)) {
            Set<RoleEntry> roles = mRoleCache.get(organizationId);
            for (RoleEntry entry : roles) {
                if (entry.getKey() == role) {
                    return entry.getValue();
                }
            }
        }

        if (hasLabel(organizationId, role.id())) {
            mRoleCache.put(organizationId, new RoleEntry(role, true));
            return true;
        }

        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }

        // check sub orgs for admins and leaders
        if (role == U.Role.admin || role == U.Role.leader) {
            Organization organization = daoSession.getOrganizationDao().load(organizationId);
            if (organization != null && !U.isNullEmpty(organization.getAncestry())) {

                // find the parent with a permission
                List<String> ancestors = Arrays.asList(organization.getAncestry().trim().split("/"));
                Collections.reverse(ancestors);
                Organization parent = null;
                for (String ancestor : ancestors) {
                    if (hasLabel(Long.parseLong(ancestor), role.id())) {
                        parent = Application.getDb().getOrganizationDao().load(Long.parseLong(ancestor));
                        break;
                    }
                }
                if (parent != null && parent.getShow_sub_orgs()) {
                    mRoleCache.put(organizationId, new RoleEntry(role, true));
                    return true;
                } else {
                    mRoleCache.put(organizationId, new RoleEntry(role, false));
                    return false;
                }
            }
        }

        return false;
    }

    public synchronized void resetRoleCache() {
        mRoleCache = null;
    }

    public synchronized boolean isAdminOrLeader() {
        return isAdminOrLeader(Session.getInstance().getOrganizationId());
    }

    public synchronized boolean isAdminOrLeader(final long organizationId) {
        return isAdmin(organizationId) || isLeader(organizationId);
    }

    public synchronized boolean isAdmin() {
        return isAdmin(Session.getInstance().getOrganizationId());
    }

    public synchronized boolean isLeader() {
        return isLeader(Session.getInstance().getOrganizationId());
    }

    public synchronized boolean isAdmin(final long organizationId) {
        return hasRole(U.Role.admin, organizationId);
    }

    public synchronized boolean isLeader(final long organizationId) {
        return hasRole(U.Role.leader, organizationId);
    }

    /**
     * Returns the person's primary organization id
     *
     * @return
     */
    public synchronized Long getPrimaryOrganizationId() {
        if (getUser() != null) {
            return getUser().getPrimary_organization_id();
        }
        return -1l;
    }

    public synchronized Organization getPrimaryOrganization() {
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        final OrganizationDao targetDao = daoSession.getOrganizationDao();
        return targetDao.load(getPrimaryOrganizationId());
    }

    public synchronized void resetOrganizationHierarchy() {
        mOrganizationHierarchy = null;
    }

    /**
     * Returns a tree of the user's privileged organizations
     *
     * @return
     */
    public synchronized TreeDataStructure<Long> getOrganizationHierarchy() {
        if (mOrganizationHierarchy == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }

            // build a tree from organization ancestry
            final TreeDataStructure<Long> tree = new TreeDataStructure<Long>(0l);

            final List<Organization> organizations = Application.getDb()
                    .getOrganizationDao()
                    .queryBuilder()
                    .where(new WhereCondition.StringCondition(OrganizationDao.Properties.Id.columnName + " IN " + "(SELECT " + OrganizationalRoleDao.TABLENAME + "."
                            + OrganizationalRoleDao.Properties.Organization_id.columnName + " FROM " + OrganizationalRoleDao.TABLENAME + " WHERE " + OrganizationalRoleDao.TABLENAME + "."
                            + OrganizationalRoleDao.Properties.Person_id.columnName + " = " + getId() + " AND " + OrganizationalRoleDao.TABLENAME + "."
                            + OrganizationalRoleDao.Properties.Role_id.columnName + " IN (" + U.Role.admin.id() + "," + U.Role.leader.id() + ")" + ")")).orderAsc(OrganizationDao.Properties.Name).build()
                    .list();

            for (final Organization organization : organizations) {
                recursiveBuildOrganizationHierarchy(tree, organization);
            }

            mOrganizationHierarchy = tree;
        }
        return mOrganizationHierarchy;
    }

    private synchronized void recursiveBuildOrganizationHierarchy(final TreeDataStructure<Long> tree, final Organization organization) {
        if (organization.getAncestry() != null) {
            TreeDataStructure<Long> parent = tree;
            boolean hasPermissions = false;
            for (final String ancestor : organization.getAncestry().trim().split("/")) {
                final Long a = Long.parseLong(ancestor);
                if (hasLabel(a, U.Role.admin.id()) || hasLabel(a, U.Role.leader.id())) {
                    hasPermissions = true;
                }
                if (hasPermissions) {
                    if (parent.getTree(a) == null) {
                        parent = parent.addLeaf(a);
                    } else {
                        parent = parent.getTree(a);
                    }
                }
            }
            if (parent.getTree(organization.getId()) == null) {
                parent.addLeaf(organization.getId());
            }
        } else {
            if (tree.getTree(organization.getId()) == null) {
                if (hasLabel(organization.getId(), U.Role.admin.id()) || hasLabel(organization.getId(), U.Role.leader.id())) {
                    tree.addLeaf(organization.getId());
                }
            }
        }

        // parse sub orgs
        if (organization.getShow_sub_orgs()) {
            final List<Organization> subOrgs = organization.getSubOrganizations();
            for (final Organization subOrg : subOrgs) {
                recursiveBuildOrganizationHierarchy(tree, subOrg);
            }
        }
    }

    /**
     * @return the person's full first and last name
     */
    public synchronized String getName() {
        String name = "";
        if (!U.isNullEmpty(getFirst_name())) {
            name += getFirst_name();
        }
        if (!U.isNullEmpty(getLast_name())) {
            name += (" " + getLast_name());
        }
        return name.trim();
    }

    public synchronized Address getCurrentAddress() {
        for (final Address address : getAddressList()) {
            return address;
        }
        return null;
    }

    public synchronized void deleteWithRelations() {
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }

        daoSession.getContactAssignmentDao().deleteByKeyInTx(daoSession.getContactAssignmentDao().queryBuilder().whereOr(ContactAssignmentDao.Properties.Assigned_to_id.eq(id), ContactAssignmentDao.Properties.Person_id.eq(id)).<Long>listKeys());
        daoSession.getAddressDao().deleteByKeyInTx(daoSession.getAddressDao().queryBuilder().where(AddressDao.Properties.Person_id.eq(id)).<Long>listKeys());
        daoSession.getEmailAddressDao().deleteByKeyInTx(daoSession.getEmailAddressDao().queryBuilder().where(EmailAddressDao.Properties.Person_id.eq(id)).<Long>listKeys());
        daoSession.getFollowupCommentDao().deleteByKeyInTx(daoSession.getFollowupCommentDao().queryBuilder().whereOr(FollowupCommentDao.Properties.Contact_id.eq(id), FollowupCommentDao.Properties.Commenter_id.eq(id)).<Long>listKeys());
        daoSession.getOrganizationalRoleDao().deleteByKeyInTx(daoSession.getOrganizationalRoleDao().queryBuilder().where(OrganizationalRoleDao.Properties.Person_id.eq(id)).<Long>listKeys());
        daoSession.getPhoneNumberDao().deleteByKeyInTx(daoSession.getPhoneNumberDao().queryBuilder().where(PhoneNumberDao.Properties.Person_id.eq(id)).<Long>listKeys());
        daoSession.getRejoicableDao().deleteByKeyInTx(daoSession.getRejoicableDao().queryBuilder().whereOr(RejoicableDao.Properties.Person_id.eq(id), RejoicableDao.Properties.Created_by_id.eq(id)).<Long>listKeys());
        daoSession.getUserDao().deleteByKeyInTx(daoSession.getUserDao().queryBuilder().where(UserDao.Properties.Person_id.eq(id)).<Long>listKeys());

        delete();
    }

    public synchronized GPerson getGModel() {
        final GPerson p = new GPerson();

        p.id = getId();
        p.first_name = getFirst_name();
        p.last_name = getLast_name();
        p.gender = getGender();

        final List<GEmailAddress> emails = new ArrayList<GEmailAddress>();
        final List<EmailAddress> addresses = getEmailAddressList();
        for (final EmailAddress address : addresses) {
            emails.add(GEmailAddress.createFromEmailAddress(address));
        }
        p.email_addresses = emails.toArray(new GEmailAddress[emails.size()]);

        final List<GPhoneNumber> numbers = new ArrayList<GPhoneNumber>();
        final List<PhoneNumber> phones = getPhoneNumberList();
        for (final PhoneNumber number : phones) {
            numbers.add(GPhoneNumber.createFromPhoneNumber(number));
        }
        p.phone_numbers = numbers.toArray(new GPhoneNumber[numbers.size()]);

        final Address address = getCurrentAddress();
        if (address != null) {
            p.current_address = address.getGModel();
        }

        return p;
    }

    public synchronized void resetStatus() {
        mStatuses = null;
    }

    public synchronized FollowupStatus getStatus() {
        return getStatus(Session.getInstance().getOrganizationId());
    }

    public synchronized FollowupStatus getStatus(final long organizationId) {
        if (mStatuses == null) {
            mStatuses = new HashMap<Long, FollowupStatus>();
        }

        if (mStatuses.get(organizationId) == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }

            OrganizationalRole role = daoSession
                    .getOrganizationalRoleDao()
                    .queryBuilder()
                    .where(OrganizationalRoleDao.Properties.Person_id.eq(getId()), OrganizationalRoleDao.Properties.Role_id.eq(U.Role.contact.id()),
                            OrganizationalRoleDao.Properties.Organization_id.eq(organizationId)).limit(1).unique();
            if (role == null || role.getFollowup_status() == null) {
                role = daoSession
                        .getOrganizationalRoleDao()
                        .queryBuilder()
                        .where(OrganizationalRoleDao.Properties.Person_id.eq(getId()), OrganizationalRoleDao.Properties.Followup_status.isNotNull(),
                                OrganizationalRoleDao.Properties.Organization_id.eq(organizationId)).limit(1).unique();
            }

            if (role != null) {
                mStatuses.put(organizationId, U.FollowupStatus.valueOf(role.getFollowup_status()));
            }
        }
        return mStatuses.get(organizationId);
    }

    public synchronized PhoneNumber getPrimaryPhoneNumber() {
        if (mPrimaryPhoneNumber != null) return mPrimaryPhoneNumber;

        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }

        mPrimaryPhoneNumber = daoSession.getPhoneNumberDao().queryBuilder().where(PhoneNumberDao.Properties.Person_id.eq(getId()), PhoneNumberDao.Properties.Primary.eq(true)).limit(1).unique();
        return mPrimaryPhoneNumber;
    }

    public synchronized void resetPrimaryPhoneNumber() {
        mPrimaryPhoneNumber = null;
    }

    public synchronized EmailAddress getPrimaryEmailAddress() {
        if (mPrimaryEmailAddress != null) return mPrimaryEmailAddress;

        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }

        mPrimaryEmailAddress = daoSession.getEmailAddressDao().queryBuilder().where(EmailAddressDao.Properties.Person_id.eq(getId()), EmailAddressDao.Properties.Primary.eq(true)).limit(1).unique();
        return mPrimaryEmailAddress;
    }

    public synchronized void resetPrimaryEmailAddress() {
        mPrimaryEmailAddress = null;
    }

    public synchronized ContactAssignment getContactAssignment() {
        return getContactAssignment(Session.getInstance().getOrganizationId());
    }

    public synchronized ContactAssignment getContactAssignment(final long organizationId) {
        if (mContactAssignments == null) {
            mContactAssignments = new HashMap<Long, ContactAssignment>();
        }

        if (mContactAssignments.get(organizationId) == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            mContactAssignments.put(
                    organizationId,
                    Application.getDb().getContactAssignmentDao().queryBuilder()
                            .where(ContactAssignmentDao.Properties.Person_id.eq(getId()), ContactAssignmentDao.Properties.Organization_id.eq(Session.getInstance().getOrganizationId()))
                            .orderDesc(ContactAssignmentDao.Properties.Updated_at).limit(1).unique());
        }
        return mContactAssignments.get(organizationId);
    }

    public synchronized void resetContactAssignments() {
        mContactAssignments = null;
    }

    public synchronized Gender getGenderEnum() {
        if (U.isNullEmpty(getGender())) {
            return null;
        }
        try {
            return Gender.valueOf(getGender());
        } catch (final Exception e) {
            return null;
        }
    }

    public synchronized void refreshAll() {
        refresh();
        resetStatus();
        resetPrimaryEmailAddress();
        resetPrimaryPhoneNumber();
        resetContactAssignments();
        invalidateViewCache();
    }

    public PersonViewCache getViewCache() {
        if (mPersonViewCache == null) {
            mPersonViewCache = new PersonViewCache();
            mPersonViewCache.name = getName();
            if (getGenderEnum() != null) {
                mPersonViewCache.gender = getGenderEnum().toString();
            }
            if (getStatus() != null) {
                mPersonViewCache.status = getStatus().toString();
            }
            if (getPrimaryEmailAddress() != null) {
                mPersonViewCache.email = getPrimaryEmailAddress().getEmail();
                mPersonViewCache.emailClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        IntentHelper.sendEmail(getPrimaryEmailAddress().getEmail(), null, null);
                    }
                };
            }
            final Phonenumber.PhoneNumber number = U.parsePhoneNumber(getPrimaryPhoneNumber());
            if (number != null) {
                if (PhoneNumberUtil.getInstance().isPossibleNumber(number)) {
                    mPersonViewCache.phone = PhoneNumberUtil.getInstance().format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                    mPersonViewCache.phoneClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            IntentHelper.dialNumber(number);
                        }
                    };
                }
            }
            if (getCreated_at() != null) {
                mPersonViewCache.dateCreated = getCreated_at().toString();
            }
        }

        return mPersonViewCache;
    }

    public void invalidateViewCache() {
        mPersonViewCache = null;
    }

    public static class PersonViewCache {
        public String name;
        public String gender;
        public String status;
        public String email;
        public View.OnClickListener emailClickListener;
        public String phone;
        public View.OnClickListener phoneClickListener;
        public String permission; // TODO: implement permissions
        public String dateCreated;
    }
    // KEEP METHODS END

}
