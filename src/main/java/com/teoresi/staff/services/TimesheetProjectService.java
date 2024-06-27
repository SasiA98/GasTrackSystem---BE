package com.teoresi.staff.services;

import com.teoresi.staff.entities.*;
import com.teoresi.staff.libs.utils.Holiday;
import com.teoresi.staff.libs.utils.Pair;
import com.teoresi.staff.repositories.TimesheetProjectRepository;
import com.teoresi.staff.security.services.SessionService;
import com.teoresi.staff.services.customs.ProjectCostService;
import com.teoresi.staff.shared.services.BasicService;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Service
public class TimesheetProjectService extends BasicService {

    private final TimesheetProjectRepository timesheetProjectRepository;
    private final TimesheetService timesheetService;
    private final ProjectService projectService;
    private static final Long TIMEOFF_PROJECT_ID = 1L;
    private static final Long HOLIDAYS_PROJECT_ID = 2L;
    private final Logger logger = LoggerFactory.getLogger(TimesheetProjectService.class);
    private static final String TIMESHEET_PROJECT_ID_NOT_FOUND =  "Timesheet project with ID %d not found.";
    private static final String HOURLY_COST_NOT_FOUND =  "Hourly cost for the resource not found.";
    private static final String SPECIAL_PROJECT_NOT_EDITABLE = "The project cannot be modified.";
    private static final String SPECIAL_PROJECT_NOT_CREATABLE = "The project cannot be added.";
    private static final String WORK_DAYS_WITHOUT_HOLIDAYS_NOT_AVAILABLE = "The resource does not have any working days for this month.";
    private static final String PROJECT_IS_NOT_IN_PROGRESS = "The project is not \"In-Progress\"";

    public TimesheetProjectService(TimesheetProjectRepository timesheetProjectRepository, SessionService sessionService, ProjectCostService projectCostService, @Lazy TimesheetService timesheetService, @Lazy ProjectService projectService) {
        super(projectCostService, sessionService, LoggerFactory.getLogger(TimesheetProjectService.class));
        this.timesheetProjectRepository = timesheetProjectRepository;
        this.timesheetService = timesheetService;
        this.projectService = projectService;
    }

    public TimesheetProject create(TimesheetProject timesheetProject) {

        timesheetProject.setId(null);
        Project storedProject = projectService.getById(timesheetProject.getProjectId());

        if(storedProject.isSpecial())
            throw buildSpecialProjectException(SPECIAL_PROJECT_NOT_CREATABLE);

        if(!storedProject.getStatus().equals("In Progress"))
            throw buildProjectNotInProgressException();

        Timesheet timesheet = timesheetService.getById(timesheetProject.getTimesheet().getId());
        setDates(timesheetProject, timesheet, null);
        setCostAndDailyCostQuota(timesheetProject, timesheet);


        timesheetProject = save(timesheetProject);
        refreshProjectCost(timesheetProject.getProject(), true);

        return timesheetProject;
    }

    public TimesheetProject update(TimesheetProject timesheetProject) {

        timesheetProject = updateEntity(timesheetProject);

        if(timesheetProject.getHours() != null)
            refreshProjectCost(timesheetProject.getProject(), true);

        return timesheetProject;
    }

    @Async
    public void asyncUpdateEntity(TimesheetProject timesheetProject) {
        updateEntity(timesheetProject);
    }

    public TimesheetProject updateEntity(TimesheetProject timesheetProject) {

        TimesheetProject storedTimesheetProject = getById(timesheetProject.getId());
        Project project = storedTimesheetProject.getProject();

        if(project.getId().equals(HOLIDAYS_PROJECT_ID))
            throw buildSpecialProjectException(SPECIAL_PROJECT_NOT_EDITABLE);

        if(project.isSpecial())
            return save(timesheetProject);

        setDates(timesheetProject, storedTimesheetProject.getTimesheet(), storedTimesheetProject.getAllocation());

        if(timesheetProject.getHours() != null) {
            setCostAndDailyCostQuota(timesheetProject, storedTimesheetProject.getTimesheet());
            timesheetProject = save(timesheetProject);
        }
        return timesheetProject;
    }


    private TimesheetProject save(TimesheetProject timesheetProject) {
        try {
            if(sessionService.getCurrentUser() != null) logger.info(USER_DESCRIPTION, sessionService.getCurrentUser().getUsername());

            logger.info(SAVING_ENTITY, timesheetProject.toString());
            timesheetProject = timesheetProjectRepository.save(timesheetProject);
            logger.info(SAVED_ENTITY);
            return timesheetProject;

        } catch (JpaSystemException ex) {
            Throwable cause = ex.getCause();
            String errorMessage = cause.getMessage();
            String errorState;

            if (cause instanceof GenericJDBCException) {
                errorState = ((GenericJDBCException) cause).getSQLException().getSQLState();

                if (errorState.equals(USER_DEFINED_SQL_EXCEPTION))
                    errorMessage = ((GenericJDBCException) cause).getSQLException().getMessage();
                else if(errorState.equals(COULD_NOT_EXECUTE_STATEMENT_ERROR)){
                    errorMessage = ((GenericJDBCException) cause).getSQLException().getMessage();

                    if(cause.getCause() != null) {
                        String detailedMessage = cause.getCause().getMessage();
                        if(detailedMessage.contains("hours_domain"))
                            errorMessage = "The hours must be greater than 0";
                    }
                }
            }
            logger.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        } catch (DataIntegrityViolationException ex) {
            String message = CONSTRAINT_VIOLATION;
            logger.error(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    public void delete(Long timesheetProjectId){
        TimesheetProject oldTimesheetProject = getById(timesheetProjectId);

        if(oldTimesheetProject.getProject().isSpecial())
            throw buildSpecialProjectException(SPECIAL_PROJECT_NOT_EDITABLE);

        deleteEntity(timesheetProjectId);

        if(oldTimesheetProject.getHours() != null)
            refreshProjectCost(oldTimesheetProject.getProject(), true);
    }

    public void deleteEntity(Long id) {

        TimesheetProject timesheetProject = getById(id);

        if(timesheetProject.getProject().isSpecial())
            throw buildSpecialProjectException(SPECIAL_PROJECT_NOT_EDITABLE);

        if(!timesheetProject.getProject().getStatus().equals("In Progress"))
            throw buildProjectNotInProgressException();

        deleteById(id);
    }

    public TimesheetProject getById(Long id) {
        return getById(timesheetProjectRepository, id, TIMESHEET_PROJECT_ID_NOT_FOUND);
    }

    public void deleteById(Long id) {
        deleteById(timesheetProjectRepository, id, TIMESHEET_PROJECT_ID_NOT_FOUND);
    }

    public List<TimesheetProject> getAll(){
        return getAll(timesheetProjectRepository);
    }

    private void setDates(TimesheetProject timesheetProject, Timesheet timesheet, Allocation allocation){


        LocalDate startDate = timesheet.getStartDate();
        LocalDate endDate = timesheet.getEndDate();

        if(allocation != null) {
            LocalDate allocationStartDate = getLocalDate(allocation.getStartDate());
            LocalDate allocationEndDate = getLocalDate(allocation.getEndDate());

            if (allocationStartDate.isAfter(startDate))
                startDate = allocationStartDate;

            if (allocationEndDate.isBefore(endDate))
                endDate = allocationEndDate;
        }

        timesheetProject.setStartDate(startDate);
        timesheetProject.setEndDate(endDate);
    }

    private void setCostAndDailyCostQuota(TimesheetProject timesheetProject, Timesheet timesheet){

        Integer hours = timesheetProject.getHours();

        if (hours == null)
            return;

        Resource resource = timesheet.getResource();
        LocalDate timesheetStartDate = timesheet.getStartDate();
        ResourceHourlyCost mostUpToDateCost = resource.getMostUpToDateHourlyCost(timesheetStartDate);

        if(mostUpToDateCost == null)
            throw buildHourlyCostNotFoundException();

        float cost = mostUpToDateCost.getCost() * hours;
        int workDaysWithoutHolidays =
                timesheet.computeWorkDaysWithoutHolidays(timesheetProject.getStartDate(), timesheetProject.getEndDate());

        if(workDaysWithoutHolidays == 0)
            throw  buildWorkDaysWithoutHolidaysNotAvailableException();

        timesheetProject.setCost(cost);
        timesheetProject.setDailyCostQuota(cost/workDaysWithoutHolidays);
    }

    public List<TimesheetProject> getAllByResourceAndStartDate(Resource resource, LocalDate startDate) {
        return timesheetProjectRepository.findAllByResourceIdAndStartDate(resource, startDate);
    }
    public void addHolidaysProject(Timesheet timesheet){

        LocalDate startDate = timesheet.getStartDate();
        LocalDate endDate = timesheet.getEndDate();
        Project project = projectService.getById(HOLIDAYS_PROJECT_ID);

        Pair<Integer,String> holidayDetails = computeHolidays(startDate, endDate);

        TimesheetProject holidayProject = TimesheetProject.builder()
                .timesheet(timesheet)
                .project(project)
                .hours(holidayDetails.getFirst())
                .note(holidayDetails.getSecond())
                .build();

        save(holidayProject);
    }

    public void addTimeOffProject(Timesheet timesheet){

        Project project = projectService.getById(TIMEOFF_PROJECT_ID);
        TimesheetProject timeOffProject = TimesheetProject.builder()
                .timesheet(timesheet)
                .project(project)
                .build();

        save(timeOffProject);
    }

    private Pair<Integer,String> computeHolidays(LocalDate startDate, LocalDate endDate){

        Set<Holiday> allHolidays = retrieveHolidays(startDate, endDate);
        StringBuilder note = new StringBuilder();
        int hours = 0;

        for (Holiday holiday: allHolidays) {

            LocalDate parsedDate = holiday.getDate();

            if (!parsedDate.isBefore(startDate) && !parsedDate.isAfter(endDate)) {

                if (parsedDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    if (note.length() != 0) note.append(" , ");
                    note.append(holiday.getDescription()).append(" (").append(DayOfWeek.SATURDAY).append(")");

                } else if (parsedDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    if (note.length() != 0) note.append(" , ");
                    note.append(holiday.getDescription()).append(" (").append(DayOfWeek.SUNDAY).append(")");

                } else {
                    if (note.length() != 0) note.append(" , ");
                    hours = hours + 8;
                    note.append(holiday.getDescription());
                }
            }
        }

        return new Pair<>(hours, note.toString());
    }


    private ResponseStatusException buildSpecialProjectException(String msg) {
        String message = String.format(msg);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseStatusException buildProjectNotInProgressException() {
        String message = String.format(PROJECT_IS_NOT_IN_PROGRESS);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseStatusException buildHourlyCostNotFoundException() {
        String message = String.format(HOURLY_COST_NOT_FOUND);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseStatusException buildWorkDaysWithoutHolidaysNotAvailableException() {
        String message = String.format(WORK_DAYS_WITHOUT_HOLIDAYS_NOT_AVAILABLE);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

}

