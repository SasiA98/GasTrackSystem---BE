package com.client.staff.libs.web.services;

import com.client.staff.libs.data.models.IdentifiableEntity;
import com.client.staff.libs.data.repositories.CrudRepository;

public interface RepositoryService<Model extends IdentifiableEntity<Id>, Id> {
    CrudRepository<Model, Id> getRepository();
}
