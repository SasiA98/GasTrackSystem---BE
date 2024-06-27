package com.teoresi.staff.libs.web.services;

import com.teoresi.staff.libs.data.models.IdentifiableEntity;

import java.util.List;
import java.util.Optional;

public interface ReadService<Model extends IdentifiableEntity<Id>, Id> extends RepositoryService<Model, Id> {
    default Optional<Model> getById(Id id) {
        return getRepository().findById(id);
    }

    default List<Model> getAll() {
        return getRepository().findAll();
    }
}
