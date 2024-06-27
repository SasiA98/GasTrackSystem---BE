package com.teoresi.staff.services;

import com.teoresi.staff.components.ResourceSpecificationsFactory;
import com.teoresi.staff.dtos.ResourceSkillDTO;
import com.teoresi.staff.entities.*;
import com.teoresi.staff.libs.data.models.Filter;
import com.teoresi.staff.libs.utils.ComparableWrapper;
import com.teoresi.staff.libs.utils.Pair;
import com.teoresi.staff.repositories.ResourceRepository;
import com.teoresi.staff.repositories.ResourceSkillRepository;
import com.teoresi.staff.security.models.JwtAuthentication;
import com.teoresi.staff.security.services.SessionService;
import com.teoresi.staff.services.customs.ProjectCostService;
import com.teoresi.staff.services.customs.ResourceLoadService;
import com.teoresi.staff.shared.models.Role;
import com.teoresi.staff.shared.services.BasicService;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ResourceService extends BasicService {

    private final ResourceRepository resourceRepository;
    private final ResourceSpecificationsFactory resourceSpecificationsFactory;
    private final TimesheetProjectService timesheetProjectService;
    private final AllocationService allocationService;
    private final UserService userService;
    private final ResourceSkillRepository resourceSkillRepository;
    private final TimesheetService timesheetService;
    private final SkillService skillService;
    private final Logger logger = LoggerFactory.getLogger(ResourceService.class);
    private static final String RESOURCE_ID_NOT_FOUND = "Resource with id %d not found";
    private static final String USER_DOES_NOT_HAVE_AUTH_ROLES = "This resource must have at least an authorized role";
    private static final String CURRENT_HOURLY_COST = "currentHourlyCost";


    private final Map<String, Function<Resource, ComparableWrapper>> sortingFields = new HashMap<>() {{
        put("currentHourlyCost", resource -> resource.getCurrentHourlyCost() != null ? new ComparableWrapper(resource.getCurrentHourlyCost()) : null);
        put("unit", resource -> resource.getUnitTrigram() != null ? new ComparableWrapper(resource.getUnitTrigram()) : null);
        put("roles", resource -> resource.getRoles() != null ? new ComparableWrapper(resource.getRoles()) : null);
    }};

    public ResourceService(@Lazy AllocationService allocationService, SkillService skillService, @Lazy UserService userService, ResourceSkillRepository resourceSkillRepository, ResourceRepository resourceRepository, ResourceSpecificationsFactory resourceSpecificationsFactory, SessionService sessionService, ResourceLoadService resourceLoadService, ProjectCostService projectCostService, TimesheetService timesheetService, TimesheetProjectService timesheetProjectService) {
        super(resourceLoadService, projectCostService, sessionService, LoggerFactory.getLogger(ResourceService.class));
        this.allocationService = allocationService;
        this.resourceRepository = resourceRepository;
        this.userService = userService;
        this.resourceSpecificationsFactory = resourceSpecificationsFactory;
        this.timesheetService = timesheetService;
        this.resourceSkillRepository = resourceSkillRepository;
        this.skillService = skillService;
        this.timesheetProjectService = timesheetProjectService;
    }

    public Resource create(Resource resource, boolean isFromImport) {
        resource.setId(null);

        resource = save(resource, isFromImport);
        timesheetService.generateTimesheetsResource(resource);
        refreshResourceLoad(LocalDate.now().getYear(), resource);
        return resource;
    }

    public Resource update(Resource resource) {

        if (!resourceRepository.existsById(resource.getId()))
            throw buildEntityWithIdNotFoundException(resource.getId(), RESOURCE_ID_NOT_FOUND);

        Resource oldResource = getById(resource.getId());

        if (!hasUserPermissionToChangeRoles(oldResource.getRoles()))
            throw buildDumCannotModifyPermissionsException();

        if (!areRolesConsistent(resource))
            throw buildUserDoesNotHaveAuthRolesException();

        resource = save(resource, false);

        if (isLeaveDateSet(oldResource, resource)) {

            if(isDateWeekendOrHoliday(getLocalDate(resource.getLeaveDate())))
                throw buildDatesNotConsistentException();

            updateTimesheetsOnLeaveDateChanged(resource);
            updateAllocationsOnLeaveDateChanged(resource);
        }

        if (isHourlyCostChanged(oldResource, resource))
            updateTimesheetProjectOnHourlyCostChanged(resource);


        refreshResourceLoad(LocalDate.now().getYear(), resource);
        return resource;
    }

    private Resource save(Resource resource, boolean isFromImport) {
        try {
            if(sessionService.getCurrentUser() != null) logger.info(USER_DESCRIPTION, sessionService.getCurrentUser().getUsername());

            logger.info(SAVING_ENTITY, resource.toString());
            resource = resourceRepository.save(resource);
            logger.info(SAVED_ENTITY);
            return resource;

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
                        if(detailedMessage.contains("last_working_time_domain"))
                            errorMessage = "The working time must be in a range between 1 and 40";
                        if(detailedMessage.contains("last_hourly_cost_domain"))
                            errorMessage = "The hourly cost value must be greater than 0";
                        if(detailedMessage.contains("hiring_date_le_leave_date"))
                            errorMessage = "The hiring date must be earlier than the leave date";
                        if(detailedMessage.contains("last_hourly_cost_start_date_le_leave_date"))
                            errorMessage = "The hourly cost start date must be earlier than the leave date";
                    }

                }
            }
            logger.error(errorMessage);

            if (isFromImport)
                throw new DataIntegrityViolationException(errorMessage);
            else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        } catch (DataIntegrityViolationException ex) {
            String message = CONSTRAINT_VIOLATION;

            if(ex.getCause() != null) {
                String detailedMessage = ex.getCause().getCause().getMessage();
                if (detailedMessage.contains("email_unique"))
                    message = "This email has already been associated with another resource";
                if (detailedMessage.contains("employee_id_unique"))
                    message = "This employee ID has already been associated with another resource";
            }
            logger.error(message);

            if (isFromImport)
                throw new DataIntegrityViolationException(message);
            else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }


    public Resource getById(Long id) {
        return getById(resourceRepository, id, RESOURCE_ID_NOT_FOUND);
    }

    public void deleteById(Long id) {
        deleteById(resourceRepository, id, RESOURCE_ID_NOT_FOUND);
    }

    public List<Resource> getAll() {
        return getAll(resourceRepository);
    }





    private boolean isHourlyCostChanged(Resource oldResource, Resource resource) {
        Float oldLastHourlyCost = oldResource.getLastHourlyCost() != null ? oldResource.getLastHourlyCost() : null;
        Float lastHourlyCost = resource.getLastHourlyCost() != null ? resource.getLastHourlyCost() : null;

        Date oldLastHourlyCostStartDate = oldResource.getLastHourlyCostStartDate();
        if (oldLastHourlyCostStartDate != null)
            oldLastHourlyCostStartDate = new Date(oldLastHourlyCostStartDate.getTime());

        Date lastHourlyCostStartDate = resource.getLastHourlyCostStartDate() != null ? resource.getLastHourlyCostStartDate() : null;

        return (lastHourlyCost != null && !lastHourlyCost.equals(oldLastHourlyCost)) ||
                (lastHourlyCostStartDate != null && !lastHourlyCostStartDate.equals(oldLastHourlyCostStartDate));
    }

    private boolean isLeaveDateSet(Resource oldResource, Resource resource) {
        LocalDate oldLeaveDate = oldResource.getLeaveDate() != null ? getLocalDate(oldResource.getLeaveDate()) : null;
        LocalDate leaveDate = resource.getLeaveDate() != null ? getLocalDate(resource.getLeaveDate()) : null;

        return (oldLeaveDate == null && leaveDate != null);
    }

    private boolean areRolesConsistent(Resource resource){
        if(!userService.existsByResourceId(resource.getId()))
            return true;

        return hasUserPermissions(resource.getRoles());
    }

    private void updateTimesheetsOnLeaveDateChanged(Resource resource) {
        LocalDate leaveDate = getLocalDate(resource.getLeaveDate());
        Timesheet timesheet = timesheetService.getTimesheetByDateAndResourceId(resource.getId(), leaveDate.getMonthValue(), leaveDate.getYear());

        if (timesheet != null){
            timesheet.setEndDate(leaveDate);
            timesheet.refreshTotWorkHours();
            timesheetService.update(timesheet);
        }
    }

    private void updateTimesheetProjectOnHourlyCostChanged(Resource resource) {

        LocalDate startDate = resource.getLastHourlyCostStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        List<TimesheetProject> timesheetProjects = timesheetProjectService.getAllByResourceAndStartDate(resource, startDate);

        Set<Project> projects = timesheetProjects.stream()
                .map(TimesheetProject::getProject)
                .collect(Collectors.toSet());

        for (TimesheetProject timesheetProject : timesheetProjects)
            timesheetProjectService.updateEntity(timesheetProject);

        for (Project project : projects)
            refreshProjectCost(project, false);
    }

    private void updateAllocationsOnLeaveDateChanged(Resource resource) {
        List<Allocation> allocations = allocationService.getAllByResourceId(resource.getId());
        Set<Allocation> filteredAllocations = allocations.stream().filter(a -> a.getEndDate().after(resource.getLeaveDate())).collect(Collectors.toSet());

        for (Allocation allocation : filteredAllocations) {

            if (allocation.getStartDate().after(resource.getLeaveDate()))
                allocationService.deleteEntity(allocation.getId());
            else {
                allocation.setEndDate(resource.getLeaveDate());
                allocationService.updateEntity(allocation);
            }
            refreshProjectCost(allocation.getProject(), false);
        }
    }




    public Resource addOrUpdateSkill(Long resourceId, Long skillId, ResourceSkillDTO resourceSkillDTO) {
        ResourceSkill r = ResourceSkill.builder()
                .resource(getById(resourceId))
                .skill(skillService.getById(skillId))
                .rating(resourceSkillDTO.getRating())
                .id(new ResourceSkillKey(resourceId, skillId))
                .build();

        resourceSkillRepository.save(r);
        return getById(resourceId);
    }

    public void deleteSkillById(Long resourceId, Long skillId) {
        resourceSkillRepository.deleteById(new ResourceSkillKey(resourceId, skillId));
    }




    @Transactional
    public Page<Resource> searchAdvanced(Optional<Filter<Resource>> filter, Pageable pageable) {
        try {

            Pair<Boolean, String> sortingInfo = isSortedOnNonDirectlyMappedField(sortingFields, pageable);
            boolean isSorted = sortingInfo.getFirst();
            String sortingProperty = sortingInfo.getSecond();
            Page<Resource> resourcesPage;

            if(isSorted) {
                List<Resource> resources = filter.map(resourceFilter ->
                        resourceRepository.findAll(getSpecificationForAdvancedSearch(resourceFilter))
                ).orElseGet(resourceRepository::findAll);

                resourcesPage = getPage(sortingFields, resources, pageable, sortingProperty);

            } else {
                resourcesPage = filter.map(resourceFilter ->
                        resourceRepository.findAll(getSpecificationForAdvancedSearch(resourceFilter), pageable)
                ).orElseGet(() -> resourceRepository.findAll(pageable));

            }

            resourcesPage = removeDuplicates(resourcesPage);
            return applyRoleVisibilityFilter(resourcesPage);

        } catch (PropertyReferenceException ex) {
            String message = String.format(INVALID_SEARCH_CRITERIA, ex.getMessage());
            logger.debug(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private Specification<Resource> getSpecificationForAdvancedSearch(Filter<Resource> resourceFilter) {

        Set<Role> userRoles = sessionService.getCurrentUser().getAuthorities();
        if (!checkPermissionForSalaryDetailVisibility(userRoles)) {

            if (resourceFilter.containsField(CURRENT_HOURLY_COST))
                throw advancedSearchWithNoPermissionException();
        }
        return resourceFilter.toSpecification(resourceSpecificationsFactory);
    }

    private Page<Resource> applyRoleVisibilityFilter(Page<Resource> resourcesPage) {

        List<Resource> filteredResources = new ArrayList<>();

        for (Resource resource : resourcesPage.getContent()) {
            if (!resource.getRoles().contains(Role.ADMIN))
                filteredResources.add(resource);
        }
        return new PageImpl<>(filteredResources, resourcesPage.getPageable(), resourcesPage.getTotalElements());
    }




    public boolean existByEmployeeId(Integer employeeId) {
        Optional<Resource> optResource = resourceRepository.findByEmployeeId(employeeId);
        return optResource.isPresent();
    }

    public Optional<Resource> getByEmployeeId(Integer employeeId) {
        return resourceRepository.findByEmployeeId(employeeId);
    }

    public boolean currentUserHasSalaryDetailPermissions() {
        JwtAuthentication jwtAuthentication = sessionService.getCurrentUser();
        return checkPermissionForSalaryDetailVisibility(jwtAuthentication.getAuthorities());
    }

    public boolean existsHomonyms(String name, String surname) {
        Optional<Resource> optionalResource = resourceRepository.findByNameAndSurname(name, surname);
        return optionalResource.isPresent();
    }




    private ResponseStatusException buildUserDoesNotHaveAuthRolesException() {
        String message = String.format(USER_DOES_NOT_HAVE_AUTH_ROLES);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

}

