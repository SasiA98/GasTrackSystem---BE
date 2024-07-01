package com.teoresi.staff.repositories.old.customs;

import com.teoresi.staff.entities.old.Resource;
import com.teoresi.staff.entities.old.DailyResourceLoad;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DailyResourceLoadRepository extends CrudRepository<DailyResourceLoad, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE daily_resource_load r SET commitment_pct = 100, hours_pct = 100, is_holiday = 1 " +
            "WHERE r.resource = :resourceId " +
            "AND r.year = :year " +
            "AND r.dayNumber = :dayNumber")
    void updateByUniqueFields(Resource resourceId,
                              Integer year,
                              Integer dayNumber);

    @Transactional
    int removeByYearAndResource(Integer year, Resource resource);
}