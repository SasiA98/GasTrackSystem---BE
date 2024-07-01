package com.teoresi.staff.repositories.old.customs;

import com.teoresi.staff.entities.old.Resource;
import com.teoresi.staff.entities.old.TimesheetProject;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimesheetProjectRepository  extends CrudRepository<TimesheetProject, Long> {

    @Transactional
    @Query("select s " +
            "from time_sheet_project s join time_sheet t on s.timesheet = t join project p on s.project = p " +
            "where t.resource = :resource and p.status = 'In Progress' and p.isSpecial = false " +
            "and (s.hours is not null or s.hours <> 0) and s.startDate >= :startDate")
    List<TimesheetProject> findAllByResourceIdAndStartDate(Resource resource, LocalDate startDate);

}
