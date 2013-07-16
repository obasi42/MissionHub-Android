package com.missionhub.model;

/**
 * Created by croemmich on 7/15/13.
 */
public interface TimestampedEntity {

    public String getUpdated_at();

    public void setUpdated_at(String updated_at);

    public String getCreated_at();

    public void setCreated_at(String created_at);

}
