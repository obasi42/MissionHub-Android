package com.missionhub.sql;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table INTEREST (schema version 1).
 */
public class Interest {

    private Integer _id;
    private Integer person_id;
    private String name;
    private String interest_id;
    private String category;
    private String provider;

    public Interest() {
    }

    public Interest(Integer _id) {
        this._id = _id;
    }

    public Interest(Integer _id, Integer person_id, String name, String interest_id, String category, String provider) {
        this._id = _id;
        this.person_id = person_id;
        this.name = name;
        this.interest_id = interest_id;
        this.category = category;
        this.provider = provider;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public Integer getPerson_id() {
        return person_id;
    }

    public void setPerson_id(Integer person_id) {
        this.person_id = person_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInterest_id() {
        return interest_id;
    }

    public void setInterest_id(String interest_id) {
        this.interest_id = interest_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

}
