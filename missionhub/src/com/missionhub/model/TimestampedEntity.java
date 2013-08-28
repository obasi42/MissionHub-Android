package com.missionhub.model;

public interface TimestampedEntity {

    public String getUpdated_at();

    public void setUpdated_at(String updated_at);

    public String getCreated_at();

    public void setCreated_at(String created_at);

}
