package com.teoresi.staff.repositories.old.customs;

import com.teoresi.staff.entities.old.Project;
import com.teoresi.staff.entities.old.DailyProjectCost;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DailyProjectCostRepository extends CrudRepository<DailyProjectCost, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM daily_project_cost p WHERE p.project = :project AND p.year = :year AND p.dayNumber = :dayNumber")
    void removeHoliday(Project project, Integer year, Integer dayNumber);
}