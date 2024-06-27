package com.teoresi.staff.libs.web.services;

import com.teoresi.staff.libs.data.models.IdentifiableEntity;
import com.teoresi.staff.libs.data.repositories.CrudRepository;

public interface RepositoryService<Model extends IdentifiableEntity<Id>, Id> {
    CrudRepository<Model, Id> getRepository();
}
