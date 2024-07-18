package com.client.staff.libs.web.services;

import com.client.staff.libs.data.models.IdentifiableEntity;

import java.util.Optional;

public interface UpdateService<Model extends IdentifiableEntity<Id>, Id> extends RepositoryService<Model, Id> {
    default Optional<Model> update(Model model) {
        if (getRepository().existsById(model.getId()))
            return Optional.of((Model)getRepository().save(model));
        return Optional.empty();
    }
}
