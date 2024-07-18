package com.client.staff.libs.data.models;

public interface IdentifiableEntity<Id> {
    Id getId();

    void setId(Id paramId);
}
