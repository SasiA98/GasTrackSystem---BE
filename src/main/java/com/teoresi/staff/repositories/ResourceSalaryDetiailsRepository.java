package com.teoresi.staff.repositories;

import com.teoresi.staff.entities.ResourceSalaryDetails;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ResourceSalaryDetiailsRepository extends CrudRepository<ResourceSalaryDetails, Long> {

    @Transactional
    @Query("SELECT rs FROM resource_salary_details rs WHERE resource_id = :id")
    List<ResourceSalaryDetails> findAllByResourceId(Long id);
}