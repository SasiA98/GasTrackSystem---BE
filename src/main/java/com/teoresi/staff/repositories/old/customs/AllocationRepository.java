package com.teoresi.staff.repositories.old.customs;

import com.teoresi.staff.entities.old.Allocation;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AllocationRepository extends CrudRepository<Allocation, Long> {

    @Transactional
    @Query("SELECT a FROM allocation a WHERE resource_id = :id")
    List<Allocation> findAllByResourceId(Long id);

    @Transactional
    @Query("SELECT a FROM allocation a WHERE project_id = :id")
    List<Allocation> findAllByProjectId(Long id);

    @Transactional
    @Query("SELECT a FROM allocation a WHERE resource_id = :resourceId AND project_id IN (:projectIds)")
    List<Allocation> findAllByResourceIdAndProjectIdIn(Long resourceId, List<Long> projectIds);

}