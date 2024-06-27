package com.teoresi.staff.shared.services;

import com.teoresi.staff.entities.Project;
import com.teoresi.staff.entities.Resource;
import com.teoresi.staff.libs.data.models.IdentifiableEntity;
import com.teoresi.staff.libs.data.repositories.CrudRepository;
import com.teoresi.staff.libs.utils.ComparableWrapper;
import com.teoresi.staff.libs.utils.Holiday;
import com.teoresi.staff.libs.utils.Pair;
import com.teoresi.staff.security.models.JwtAuthentication;
import com.teoresi.staff.security.services.SessionService;
import com.teoresi.staff.services.customs.ProjectCostService;
import com.teoresi.staff.services.customs.ResourceLoadService;
import com.teoresi.staff.shared.models.Role;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public abstract class BasicService {

    protected static final String ERROR_RETRIEVING_HOLIDAYS = "Error occurred during retrieval of holidays from JSON";
    protected static final String CONSTRAINT_VIOLATION = "One or more constraint violations occurred. %s";
    protected static final String USER_DEFINED_SQL_EXCEPTION = "45000";
    protected static final String COULD_NOT_EXECUTE_STATEMENT_ERROR = "HY000";
    protected static final String INVALID_SEARCH_CRITERIA = "Invalid search criteria: %s";
    protected static final String PERMISSION_VIOLATION = "User is not authorized to access this information";
    protected static final String DATES_NOT_CONSISTENT = "Please ensure that the dates fall on weekdays, considering public holidays";
    protected static final String DUM_CANNOT_MODIFY_PERMISSIONS = "User is not authorized to modify resources with GDM as role";
    protected static final String SAVING_ENTITY = "Saving entity: {}";
    protected static final String SAVED_ENTITY = "Entity saved successfully";
    protected static final String USER_DESCRIPTION = "User : {}";

    private ResourceLoadService resourceLoadService;
    private ProjectCostService projectCostService;

    protected final HolidayManagementService holidayManagementService = new HolidayManagementService();
    protected SessionService sessionService;
    protected final Logger logger;


    public BasicService(ResourceLoadService resourceLoadService, ProjectCostService projectCostService, Logger logger) {
        this.resourceLoadService = resourceLoadService;
        this.projectCostService = projectCostService;
        this.logger = logger;
    }

    public BasicService(ProjectCostService projectCostService, SessionService sessionService, Logger logger) {
        this.projectCostService = projectCostService;
        this.sessionService = sessionService;
        this.logger = logger;
    }

    public BasicService(ResourceLoadService resourceLoadService, ProjectCostService projectCostService, SessionService sessionService, Logger logger) {
        this.resourceLoadService = resourceLoadService;
        this.projectCostService = projectCostService;
        this.sessionService = sessionService;
        this.logger = logger;
    }

    public BasicService(ProjectCostService projectCostService, Logger logger) {
        this.projectCostService = projectCostService;
        this.logger = logger;
    }

    public BasicService(SessionService sessionService, Logger logger) {
        this.sessionService = sessionService;
        this.logger = logger;
    }

    public BasicService(Logger logger){
        this.logger = logger;
    }

    public BasicService(){
        logger = LoggerFactory.getLogger(BasicService.class);
    }

    protected  <T extends IdentifiableEntity<K>,K> T save (CrudRepository<T,K> repository, T entity){
        try {
            if(sessionService.getCurrentUser() != null) logger.info(USER_DESCRIPTION, sessionService.getCurrentUser().getUsername());

            logger.info(SAVING_ENTITY, entity.toString());
            entity = repository.save(entity);
            logger.info(SAVED_ENTITY);
            return entity;

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
                }
            }

            logger.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        } catch (DataIntegrityViolationException ex) {
            String message = String.format(CONSTRAINT_VIOLATION, "");
            logger.error(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    protected  <T extends IdentifiableEntity<K>,K> T getById(CrudRepository<T,K> repository, K id, String msg) {
        Optional<T> entity = repository.findById(id);
        if (entity.isEmpty())
            throw buildEntityWithIdNotFoundException(id, msg);

        return entity.get();
    }
    protected  <T extends IdentifiableEntity<K>,K> void deleteById(CrudRepository<T,K> repository, K id, String msg) {
        try {
            if(sessionService != null) logger.info("User : {}", sessionService.getCurrentUser().getUsername());

            logger.info("Deleting entity: {}", id);
            repository.deleteById(id);
            logger.info("Entity deleted successfully");

        } catch (DataIntegrityViolationException e) {
            throw buildEntityWithIdNotFoundException(id, msg);
        }
    }
    protected  <T extends IdentifiableEntity<K>,K>  List<T> getAll(CrudRepository<T,K> repository) {
        return repository.findAll();
    }



    protected boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    protected boolean areDatesWeekendOrHolidays(LocalDate startDate, LocalDate endDate){
        Set<Holiday> holidays = retrieveHolidays(startDate, endDate);

        return (isWeekend(startDate)|| holidayManagementService.isHoliday(holidays, startDate) ||
                isWeekend(endDate) || holidayManagementService.isHoliday(holidays, endDate));
    }

    protected boolean isDateWeekendOrHoliday(LocalDate date){
        Set<Holiday> holidays = retrieveHolidays(date, date);

        return (isWeekend(date)|| holidayManagementService.isHoliday(holidays, date));
    }


    protected Set<Holiday> retrieveHolidays (LocalDate startDate, LocalDate endDate){
        Set<Holiday> allHolidays = new HashSet<>();
        try {
            allHolidays = holidayManagementService.retrieveHolidays(startDate, endDate);
        } catch (IOException exception) {

            String message = String.format(ERROR_RETRIEVING_HOLIDAYS);
            logger.debug(message);
        }
        return allHolidays;
    }
    protected LocalDate getLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }




    protected <T> Pair<Boolean, String> isSortedOnNonDirectlyMappedField(Map<String, Function<T, ComparableWrapper>> sortingFields, Pageable pageable) {
        for (Sort.Order order : pageable.getSort())
            if(sortingFields.containsKey(order.getProperty()))
                return new Pair<>(true, (order.getProperty()));
        return new Pair<>(false, null);
    }

    protected <T> Page<T> getPage(List<T> context, Pageable pageable){
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int start = Math.min(pageNumber * pageSize, context.size());
        int end = Math.min((pageNumber + 1) * pageSize, context.size());
        return new PageImpl<>(context.subList(start, end), pageable, context.size());
    }

    protected <T> Page<T> getPage(Map<String, Function<T, ComparableWrapper>> sortingFields, List<T> entities, Pageable pageable, String sortingProperty) {

        if(sortingFields.containsKey(sortingProperty))
            return createSortedPage(entities, pageable, sortingProperty, sortingFields.get(sortingProperty));

        return new PageImpl<>(Collections.emptyList());
    }

    protected <T, R extends Comparable<? super R>> Page<T>  createSortedPage(List<T> entities, Pageable pageable, String sortingKey, Function<T, R> keyExtractor) {
        boolean isAscending = getSortingCriteria(sortingKey, pageable);

        Comparator<T> comparator = isAscending ?
                Comparator.comparing(keyExtractor, Comparator.nullsFirst(Comparator.naturalOrder())) :
                Comparator.comparing(keyExtractor, Comparator.nullsLast(Comparator.naturalOrder())).reversed();

        List<T> sortedEntities = entities.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        return getPage(sortedEntities,pageable);
    }

    protected boolean getSortingCriteria(String sortingKey, Pageable pageable){
        boolean ascending = true;
        for (Sort.Order order : pageable.getSort())
            if (order.getProperty().equals(sortingKey))
                ascending = order.isAscending();
        return ascending;
    }

    protected <T> Page<T> removeDuplicates(Page<T> page) {

        List<T> uniqueElements = new ArrayList<>();
        for (T t : page.getContent()) {

            if (!uniqueElements.contains(t))
                uniqueElements.add(t);
        }
        return new PageImpl<>(uniqueElements, page.getPageable(), page.getTotalElements());
    }




    protected void refreshResourceLoad (int year, Resource resource){ resourceLoadService.refreshResourceLoad(year, resource);
    }

    protected void refreshProjectCost (Project project, boolean sync){
        if(sync)
            projectCostService.syncRefreshProjectGantt(project);
        else
            projectCostService.asyncRefreshProjectGantt(project);
    }



    protected boolean hasUserPermissionToChangeRoles(Set<Role> resourcesRole) {
        JwtAuthentication jwtAuthentication = sessionService.getCurrentUser();

        if(jwtAuthentication == null)
            return false;

        Set<Role> currentUserRoles = jwtAuthentication.getAuthorities();

        boolean hasCurrentUserHigherAuthorityRole = currentUserRoles.contains(Role.GDM) || currentUserRoles.contains(Role.ADMIN);
        boolean modifyingHighAuthorityRole = resourcesRole.contains(Role.GDM) || resourcesRole.contains(Role.ADMIN);

        return hasCurrentUserHigherAuthorityRole || !modifyingHighAuthorityRole;
    }

    protected boolean hasUserPermissions(Set<Role> roles){
        return roles.size() > 1 || (!roles.contains(Role.CONSULTANT) && !roles.isEmpty());
    }

    protected boolean checkPermissionForSalaryDetailVisibility(Set<Role> userRoles) {
        return userRoles.contains(Role.ADMIN) || userRoles.contains(Role.GDM) || userRoles.contains(Role.DUM)
                || userRoles.contains(Role.PSM) || userRoles.contains(Role.PSL);
    }




    protected <K> ResponseStatusException buildEntityWithIdNotFoundException(K id, String msg) {
        String message = String.format(msg, id);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    protected ResponseStatusException advancedSearchWithNoPermissionException() {
        String message = String.format(PERMISSION_VIOLATION);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
    protected ResponseStatusException buildDumCannotModifyPermissionsException() {
        String message = String.format(DUM_CANNOT_MODIFY_PERMISSIONS);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
    protected ResponseStatusException buildDatesNotConsistentException(){
        String message = String.format(DATES_NOT_CONSISTENT);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}
