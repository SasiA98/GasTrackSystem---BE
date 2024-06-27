package com.teoresi.staff.repositories.customs;

import com.teoresi.staff.entities.customs.WeeklyProjectCost;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface WeeklyProjectCostRepository extends CrudRepository<WeeklyProjectCost, Long> {

    @Transactional
    @Query("SELECT r FROM weekly_project_cost r WHERE (:unitId IS NULL OR unit_id = :unitId) and year = :year and (:projectStatus IS NULL OR status = :projectStatus)")
    List<WeeklyProjectCost> findByUnitIdYearAndStatus(Long unitId, int year, String projectStatus);

    @Transactional
    @Query("SELECT r FROM weekly_project_cost r WHERE (:unitId IS NULL OR unit_id = :unitId) and year = :year")
    List<WeeklyProjectCost> findByUnitIdYear(Long unitId, int year);
}
