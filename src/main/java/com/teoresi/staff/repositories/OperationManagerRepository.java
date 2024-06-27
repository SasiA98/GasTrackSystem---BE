package com.teoresi.staff.repositories;

import com.teoresi.staff.entities.OperationManager;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationManagerRepository extends CrudRepository<OperationManager, Long> {

}
