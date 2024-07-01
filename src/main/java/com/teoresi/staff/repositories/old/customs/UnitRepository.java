package com.teoresi.staff.repositories.old.customs;

import com.teoresi.staff.libs.data.repositories.CrudRepository;
import com.teoresi.staff.entities.old.Resource;
import com.teoresi.staff.entities.old.Unit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends CrudRepository<Unit, Long> {
    @Transactional
    @Query("SELECT R FROM resource R WHERE unit_id = :id AND leave_date IS NULL")
    List<Resource> findActiveResourcesById(Long id);

    @Transactional
    Optional<Unit> findByTrigram(String trigram);

    @Transactional
    @Query("SELECT R " +
            "FROM resource R " +
            "WHERE unit_id = :id " +
            "AND (leave_date IS NULL OR :minDate <= leave_date)")
    List<Resource> findActiveResourcesInLastSixMonthsById(Long id, LocalDate minDate);
}
