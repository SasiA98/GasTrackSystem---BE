package com.teoresi.staff.repositories.old.customs;

import com.teoresi.staff.entities.old.ResourceHourlyCost;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ResourceHourlyCostRepository extends CrudRepository<ResourceHourlyCost, Long> {

    @Transactional
    @Query("SELECT rh FROM resource_hourly_cost rh WHERE resource_id = :id")
    List<ResourceHourlyCost> findAllByResourceId(Long id);
}
