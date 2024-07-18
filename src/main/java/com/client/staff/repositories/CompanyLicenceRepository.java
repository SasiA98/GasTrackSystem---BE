package com.client.staff.repositories;

import com.client.staff.entities.CompanyLicence;
import com.client.staff.libs.data.repositories.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyLicenceRepository extends CrudRepository<CompanyLicence, Long> {

}
