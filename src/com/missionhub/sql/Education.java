package com.missionhub.sql;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table EDUCATION (schema version 1).
 */
public class Education {

    private Integer _id;
    private Integer person_id;
    private String school_name;
    private String school_id;
    private String year_name;
    private String year_id;
    private String type;
    private String provider;

    public Education() {
    }

    public Education(Integer _id) {
        this._id = _id;
    }

    public Education(Integer _id, Integer person_id, String school_name, String school_id, String year_name, String year_id, String type, String provider) {
        this._id = _id;
        this.person_id = person_id;
        this.school_name = school_name;
        this.school_id = school_id;
        this.year_name = year_name;
        this.year_id = year_id;
        this.type = type;
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

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getSchool_id() {
        return school_id;
    }

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }

    public String getYear_name() {
        return year_name;
    }

    public void setYear_name(String year_name) {
        this.year_name = year_name;
    }

    public String getYear_id() {
        return year_id;
    }

    public void setYear_id(String year_id) {
        this.year_id = year_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

}
