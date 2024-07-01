package com.teoresi.staff.repositories.old.customs;

import com.teoresi.staff.entities.old.OperationManager;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationManagerRepository extends CrudRepository<OperationManager, Long> {

}
