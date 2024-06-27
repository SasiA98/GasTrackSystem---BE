package com.teoresi.staff.repositories;

import com.teoresi.staff.entities.Timesheet;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimesheetRepository extends CrudRepository<Timesheet, Long> {

    @Transactional
    @Query("SELECT t\n" +
            "FROM time_sheet t\n" +
            "where resource_id = :id\n" +
            "AND start_date >= :minDate and start_date <= :maxDate")
    List<Timesheet> findLastSixTimesheetsByResourceId(LocalDate minDate, LocalDate maxDate, Long id);

    @Transactional
    @Query("SELECT t\n" +
            "FROM time_sheet t\n" +
            "where resource_id = :id\n" +
            "AND MONTH(start_date) = :month and YEAR(start_date) = :year")
    Optional<Timesheet> findByDateAndResourceId(Long id, int month, int year);

    @Transactional
    @Query("SELECT t\n" +
           "FROM time_sheet t\n" +
           "WHERE MONTH(start_date) = :month and YEAR(start_date) = :year")
    List<Timesheet> findByDate(int month, int year);

}
