package com.teoresi.staff.repositories.customs;

import com.teoresi.staff.entities.customs.WeeklyResourceLoad;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface WeeklyResourceLoadRepository extends CrudRepository<WeeklyResourceLoad, Long> {

    @Transactional
    @Query("SELECT r FROM weekly_resource_load r WHERE (:unitId IS NULL OR unit_id = :unitId) and year = :year and pre_allocation = :preAllocation")
    List<WeeklyResourceLoad> findByYearAndUnitId(Long unitId, int year, boolean preAllocation);

}
