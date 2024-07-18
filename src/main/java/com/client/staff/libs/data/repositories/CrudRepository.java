package com.client.staff.libs.data.repositories;

import com.client.staff.libs.data.models.IdentifiableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CrudRepository<Model extends IdentifiableEntity<Id>, Id> extends JpaRepository<Model, Id>, JpaSpecificationExecutor<Model> {}
