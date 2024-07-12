package com.teoresi.staff.repositories;

import com.teoresi.staff.entities.Company;
import com.teoresi.staff.entities.Licence;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenceRepository extends CrudRepository<Licence, Long> {

}
