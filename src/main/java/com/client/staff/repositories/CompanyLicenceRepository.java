package com.client.staff.repositories;

import com.client.staff.entities.CompanyLicence;
import com.client.staff.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CompanyLicenceRepository extends CrudRepository<CompanyLicence, Long> {

    @Transactional
    @Query("SELECT u FROM company_licence u WHERE company_id = :id")
    List<CompanyLicence> findAllByCompanyId(Long id);
}
