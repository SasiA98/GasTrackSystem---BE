package com.client.staff.repositories;

import com.client.staff.entities.Company;
import com.client.staff.libs.data.repositories.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends CrudRepository<Company, Long> {

}
