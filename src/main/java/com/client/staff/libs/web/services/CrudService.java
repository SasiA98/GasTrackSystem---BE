package com.client.staff.libs.web.services;

import com.client.staff.libs.data.models.IdentifiableEntity;

public interface CrudService<Model extends IdentifiableEntity<Id>, Id, SearchCriteria> extends CreateService<Model, Id>, DeleteService<Model, Id>, ReadService<Model, Id>, UpdateService<Model, Id>, SearchService<Model, SearchCriteria>, AdvancedSearchService<Model>, RepositoryService<Model, Id> {}
