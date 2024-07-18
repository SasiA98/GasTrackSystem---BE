package com.client.staff.libs.web.services;

import com.client.staff.libs.data.models.IdentifiableEntity;

public interface DeleteService<Model extends IdentifiableEntity<Id>, Id> extends RepositoryService<Model, Id> {
    default boolean deleteById(Id id) {
        if (getRepository().existsById(id)) {
            getRepository().deleteById(id);
            return true;
        }
        return false;
    }
}
