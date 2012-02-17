package com.missionhub.api.model.sql;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ORGANIZATIONAL_ROLE.
 */
public class OrganizationalRole {

    private Long id;
    private Long person_id;
    private Long org_id;
    private String role;
    private String name;
    private Boolean primary;

    public OrganizationalRole() {
    }

    public OrganizationalRole(Long id) {
        this.id = id;
    }

    public OrganizationalRole(Long id, Long person_id, Long org_id, String role, String name, Boolean primary) {
        this.id = id;
        this.person_id = person_id;
        this.org_id = org_id;
        this.role = role;
        this.name = name;
        this.primary = primary;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPerson_id() {
        return person_id;
    }

    public void setPerson_id(Long person_id) {
        this.person_id = person_id;
    }

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

}
