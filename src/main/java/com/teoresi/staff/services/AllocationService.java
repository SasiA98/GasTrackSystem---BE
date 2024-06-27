package com.teoresi.staff.services;

import com.teoresi.staff.entities.*;
import com.teoresi.staff.libs.utils.Holiday;
import com.teoresi.staff.repositories.AllocationRepository;
import com.teoresi.staff.security.services.SessionService;
import com.teoresi.staff.services.customs.ProjectCostService;
import com.teoresi.staff.services.customs.ResourceLoadService;
import com.teoresi.staff.shared.models.Role;
import com.teoresi.staff.shared.services.BasicService;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AllocationService extends BasicService {

    private final AllocationRepository allocationRepository;
    private final ProjectService projectService;
    private final TimesheetProjectService timesheetProjectService;
    private final ResourceService resourceService;
    private final ResourceLoadService resourceLoadService;
    private final SessionService sessionService;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(AllocationService.class);
    private static final String ALLOCATION_ID_NOT_FOUND = "Allocation with id %d not found.";

    public AllocationService(AllocationRepository allocationRepository, ProjectService projectService, TimesheetProjectService timesheetProjectService, ResourceService resourceService, ResourceLoadService resourceLoadService, ProjectCostService projectCostService, SessionService sessionService, UserService userService) {
        super(resourceLoadService, projectCostService, sessionService, LoggerFactory.getLogger(AllocationService.class));
        this.allocationRepository = allocationRepository;
        this.projectService = projectService;
        this.timesheetProjectService = timesheetProjectService;
        this.resourceService = resourceService;
        this.resourceLoadService = resourceLoadService;
        this.sessionService = sessionService;
        this.userService = userService;
    }

    public Allocation create(Allocation allocation) {
        allocation.setId(null);

        Long projectId = allocation.getProject().getId();
        Project relatedOldProject = projectService.getById(projectId);

        setEndDateIfNotProvided(allocation, relatedOldProject);

        if (areDatesInconsistent(allocation))
            throw buildAllocationDatesException();


        setDailyWorkHoursQuota(allocation);
        allocation = save(allocation);

        refreshResourceLoad(allocation);

        Project relatedProject = projectService.getById(projectId);
        allocation = getById(allocation.getId());

        updateProjectIfEndDateChanged(relatedOldProject,relatedProject);
        updateTimesheetProjects(allocation.getTimesheetProjects());

        refreshProjectCost(relatedProject, false);

        return allocation;
    }

    public Allocation update(Allocation allocation) {

        allocation = updateEntity(allocation);
        Long projectId = allocation.getProject().getId();
        Project relatedProject = projectService.getById(projectId);

        refreshResourceLoad(allocation);
        refreshProjectCost(relatedProject, false);

        return allocation;
    }

    public Allocation updateEntity(Allocation allocation){
        Long projectId = allocation.getProject().getId();
        Allocation oldAllocation = getById(allocation.getId());
        Project relatedOldProject = projectService.getById(projectId);

        if(areDatesInconsistent(allocation))
            throw buildAllocationDatesException();

        setDailyWorkHoursQuota(allocation);
        allocation = save(allocation);
        allocation = getById(allocation.getId());

        Project relatedProject = projectService.getById(projectId);

        allocation = getById(allocation.getId());
        updateProjectIfEndDateChanged(relatedOldProject,relatedProject);
        Set<TimesheetProject> timesheetProjects =
                makeTimesheetProjectsConsistent(oldAllocation.getTimesheetProjects(), allocation.getTimesheetProjects());

        updateTimesheetProjects(timesheetProjects);

        return allocation;
    }

    private Allocation save(Allocation allocation) {
        try {
            if(sessionService.getCurrentUser() != null) logger.info(USER_DESCRIPTION, sessionService.getCurrentUser().getUsername());

            logger.info(SAVING_ENTITY, allocation.toString());
            allocation = allocationRepository.save(allocation);
            logger.info(SAVED_ENTITY);
            return allocation;

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
                        if(detailedMessage.contains("commitment_percentage_domain"))
                            errorMessage = "The commitment percentage must be in a range between 1 and 100";
                        if(detailedMessage.contains("allocation_hours_domain"))
                            errorMessage = "The hours must be greater than 0";

                        if(detailedMessage.contains("start_date_le_end_date"))
                            errorMessage = "The start date must be earlier than the end date";
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

    private Set<TimesheetProject> makeTimesheetProjectsConsistent(Set<TimesheetProject> oldTs, Set<TimesheetProject> ts) {
        if(oldTs.size() <= ts.size())
            return ts;
        else {
            Set<TimesheetProject> tsWithoutAllocation = new HashSet<>(oldTs);
            Set<TimesheetProject> newTs = new HashSet<>(ts);

            tsWithoutAllocation.removeAll(ts);

            Set<TimesheetProject> modifiedProjects = tsWithoutAllocation.stream()
                    .map(timesheetProject -> {
                        timesheetProject.setAllocation(null);
                        return timesheetProject;
                    })
                    .collect(Collectors.toSet());

            newTs.addAll(modifiedProjects);

            return newTs;
        }
    }

    public void delete(Long allocationId) {

        Allocation oldAllocation = getById(allocationId);
        Long projectId = oldAllocation.getProject().getId();

        Project relatedProject = projectService.getById(projectId);

        deleteEntity(allocationId);

        refreshResourceLoad(oldAllocation);
        refreshProjectCost(relatedProject, false);
    }

    public void deleteEntity(Long allocationId){

        Allocation oldAllocation = getById(allocationId);
        Long projectId = oldAllocation.getProject().getId();
        Project relatedOldProject = projectService.getById(projectId);

        deleteById(allocationId);

        Project relatedProject = projectService.getById(projectId);

        updateProjectIfEndDateChanged(relatedOldProject,relatedProject);
    }

    public Allocation getById(Long id) {
        return getById(allocationRepository, id, ALLOCATION_ID_NOT_FOUND);
    }

    public void deleteById(Long id) {
        deleteById(allocationRepository, id, ALLOCATION_ID_NOT_FOUND);
    }

    public List<Allocation> getAll(){
        return getAll(allocationRepository);
    }

    private boolean areDatesInconsistent(Allocation allocation){
        LocalDate startDate = getLocalDate(allocation.getStartDate());
        LocalDate endDate = getLocalDate(allocation.getEndDate());

        return areDatesWeekendOrHolidays(startDate,endDate);
    }

    private void refreshResourceLoad (Allocation allocation){

        LocalDate startDate = getLocalDate(allocation.getStartDate());
        LocalDate endDate = getLocalDate(allocation.getEndDate());
        int currentYear = LocalDate.now().getYear();

        if ((startDate.getYear() <= currentYear) && (endDate.getYear() >= currentYear)) {
            Resource detailedResource = resourceService.getById(allocation.getResource().getId());
            resourceLoadService.refreshResourceLoad(currentYear, detailedResource);
        }
    }

    private void updateProjectIfEndDateChanged(Project oldProject, Project project){
        if (!oldProject.getEstimatedEndDate().equals(project.getEstimatedEndDate()))
            projectService.updateEntity(project);
    }

    private void updateTimesheetProjects(Set<TimesheetProject> timesheetProjects){
        for (TimesheetProject timesheetProject : timesheetProjects)
            timesheetProjectService.asyncUpdateEntity(timesheetProject);
    }

    private void setEndDateIfNotProvided (Allocation allocation, Project project){
        if (allocation.getEndDate() == null)
                allocation.setEndDate(project.getEstimatedEndDate());
    }

    private void setDailyWorkHoursQuota (Allocation allocation){

        if (!allocation.isRealCommitment()) {
            LocalDate startDate = getLocalDate(allocation.getStartDate());
            LocalDate endDate = getLocalDate(allocation.getEndDate());

            Set<Holiday> holidays = retrieveHolidays(startDate, endDate);
            int workingDays= 0;

            for (LocalDate tmpDate = startDate; !tmpDate.isAfter(endDate); tmpDate = tmpDate.plusDays(1))

                if (!isWeekend(tmpDate) && !holidayManagementService.isHoliday(holidays, tmpDate))
                    workingDays += 1;

            allocation.setDailyWorkHoursQuota(workingDays != 0 ? ((float) allocation.getHours() / workingDays) : 0);
        }
    }

    private ResponseStatusException buildAllocationDatesException (){
        String message = String.format(DATES_NOT_CONSISTENT);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private boolean checkPermissionForAllocationsVisibility(Set<Role> userRoles){
        return userRoles.contains(Role.ADMIN) || userRoles.contains(Role.GDM) || userRoles.contains(Role.DUM)
                || userRoles.contains(Role.PSM) || userRoles.contains(Role.PSE) || userRoles.contains(Role.PSL);
    }

    public List<Allocation> getAllByResourceId(Long id) {
        Set<Role> roles = sessionService.getCurrentUser().getAuthorities();
        if (roles.contains(Role.PM) && !checkPermissionForAllocationsVisibility(roles)) {
            Long userId = sessionService.getCurrentUser().getId();
            User user = userService.getById(userId);
            Long resourceId = user.getResource().getId();

            // Ottengo i progetti di cui l'utente è PM
            List<Project> projects = projectService.getAll();

            List<Project> filteredProjects = projects.stream()
                    .filter(project -> {
                        Resource pmReal = project.getPMForAllocations(true);
                        Resource pmSales = project.getPMForAllocations(false);
                        return Objects.equals(pmReal != null ? pmReal.getId() : null, resourceId) ||
                                Objects.equals(pmSales != null ? pmSales.getId() : null, resourceId);
                    })
                    .collect(Collectors.toList());


            // Ottengo le allocazioni relative ai progetti dell'utente PM
            List<Long> projectIds = filteredProjects.stream().map(Project::getId).collect(Collectors.toList());

            return allocationRepository.findAllByResourceIdAndProjectIdIn(id, projectIds);
        }
        else
            return allocationRepository.findAllByResourceId(id);
    }

    public List<Allocation> getAllByProjectId(Long id) {
        return allocationRepository.findAllByProjectId(id);
    }

    public Allocation convertById(Long id, boolean fromRealToSale) {
        Allocation storedAllocation = getById(id);
        Allocation allocation;

        if(fromRealToSale)
            allocation = Allocation.builder()
                    .hours(getHoursFromCommitmentPercentage(storedAllocation))
                    .startDate(storedAllocation.getStartDate())
                    .endDate(storedAllocation.getEndDate())
                    .project(storedAllocation.getProject())
                    .resource(storedAllocation.getResource())
                    .role(storedAllocation.getRole())
                    .isRealCommitment(false)
                    .build();

        else
            allocation = Allocation.builder()
                    .commitmentPercentage(getCommitmentPercentageFromHours(storedAllocation))
                    .startDate(storedAllocation.getStartDate())
                    .endDate(storedAllocation.getEndDate())
                    .project(storedAllocation.getProject())
                    .resource(storedAllocation.getResource())
                    .role(storedAllocation.getRole())
                    .isRealCommitment(true)
                    .build();

        return create(allocation);
    }

    private int getHoursFromCommitmentPercentage(Allocation allocation){
        LocalDate startDate = getLocalDate(allocation.getStartDate());
        LocalDate endDate = getLocalDate(allocation.getEndDate());
        Resource resource = resourceService.getById(allocation.getResourceId());

        Set<Holiday> holidays = retrieveHolidays(startDate, endDate);

        float workingHours = 0;

        for (LocalDate tmpDate = startDate; !tmpDate.isAfter(endDate); tmpDate = tmpDate.plusDays(1))

            if (!isWeekend(tmpDate) && !holidayManagementService.isHoliday(holidays, tmpDate)){
                ResourceWorkingTime resourceWorkingTime = resource.getMostUpToDateWorkingTime(tmpDate);
                workingHours += resourceWorkingTime.getDailyWorkingTime();
            }

        return (int) ((workingHours * (float) allocation.getCommitmentPercentage()) / 100);
    }

    private int getCommitmentPercentageFromHours(Allocation allocation){
        LocalDate startDate = getLocalDate(allocation.getStartDate());
        LocalDate endDate = getLocalDate(allocation.getEndDate());
        Resource resource = resourceService.getById(allocation.getResourceId());

        Set<Holiday> holidays = retrieveHolidays(startDate, endDate);

        float workingHours = 0;

        for (LocalDate tmpDate = startDate; !tmpDate.isAfter(endDate); tmpDate = tmpDate.plusDays(1))

            if (!isWeekend(tmpDate) && !holidayManagementService.isHoliday(holidays, tmpDate)){
                ResourceWorkingTime resourceWorkingTime = resource.getMostUpToDateWorkingTime(tmpDate);
                workingHours += resourceWorkingTime.getDailyWorkingTime();
                }

        return (int) ((float) (allocation.getHours() * 100) / workingHours);
    }

}