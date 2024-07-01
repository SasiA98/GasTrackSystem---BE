package com.teoresi.staff.repositories;

import com.teoresi.staff.entities.CompanyLicence;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyLicenceRepository extends CrudRepository<CompanyLicence, Long> {

}
