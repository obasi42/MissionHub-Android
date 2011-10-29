package com.missionhub.api.model.sql;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table KEYWORD (schema version 1).
 */
public class Keyword {

    private Integer _id;
    private Integer organization_id;
    private String name;
    private String state;

    public Keyword() {
    }

    public Keyword(Integer _id) {
        this._id = _id;
    }

    public Keyword(Integer _id, Integer organization_id, String name, String state) {
        this._id = _id;
        this.organization_id = organization_id;
        this.name = name;
        this.state = state;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public Integer getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(Integer organization_id) {
        this.organization_id = organization_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}