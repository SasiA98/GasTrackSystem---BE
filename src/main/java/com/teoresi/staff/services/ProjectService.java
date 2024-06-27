package com.teoresi.staff.services;

import com.teoresi.staff.components.ProjectSpecificationsFactory;
import com.teoresi.staff.entities.Allocation;
import com.teoresi.staff.entities.Project;
import com.teoresi.staff.entities.Resource;
import com.teoresi.staff.entities.User;
import com.teoresi.staff.libs.data.models.Filter;
import com.teoresi.staff.libs.utils.ComparableWrapper;
import com.teoresi.staff.libs.utils.Pair;
import com.teoresi.staff.repositories.ProjectRepository;
import com.teoresi.staff.security.services.SessionService;
import com.teoresi.staff.services.customs.ProjectCostService;
import com.teoresi.staff.services.customs.ResourceLoadService;
import com.teoresi.staff.shared.models.Role;
import com.teoresi.staff.shared.services.BasicService;
import org.hibernate.exception.GenericJDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProjectService extends BasicService {

    private final ProjectRepository projectRepository;
    private final ProjectSpecificationsFactory projectSpecificationsFactory;
    private final SessionService sessionService;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    private static final String SPECIAL_PROJECT_NOT_EDITABLE = "This project cannot be modified";
    private static final String ESTIMATED_END_DATE_NOT_EDITABLE = "The estimated end date depends on allocation end dates, thus it cannot be directly modified";
    private static final String PROJECT_STATUS_NOT_EDITABLE = "The project cannot move to this state";
    private static final String PRE_SALE_FIXED_COST_NOT_EDITABLE = "The pre-sale fixed cost cannot be modified in this project status";
    private static final String PROJECT_ID_NOT_FOUND = "No project found with ID %d";
    private static final String ROLES_PERMISSIONS = "Users with roles PSE, PSM, or PSL can only modify the project if its status is 'pre sale'.";

    private final Map<String, Function<Project, ComparableWrapper>> sortingFields = new HashMap<>() {{
        put("dumTrigram", project -> project.getDUM() != null ?  new ComparableWrapper(project.getDumTrigram()) : null);
        put("presaleTrigram", project -> project.getPRESALE() != null ? new ComparableWrapper(project.getPresaleTrigram()) : null);
        put("pmTrigram", project -> project.getPM() != null ? new ComparableWrapper(project.getPmTrigram()) : null);
        put("currentEstimatedCost", project -> project.getCurrentEstimatedCost() != null ? new ComparableWrapper(project.getCurrentEstimatedCost()) : null);
        put("actualCost", project -> project.getActualCost() != null ? new ComparableWrapper(project.getActualCost()) : null);
    }};



    public ProjectService(ProjectRepository projectRepository, ProjectSpecificationsFactory projectSpecificationsFactory, SessionService sessionService, ProjectCostService projectCostService, ResourceLoadService resourceLoadService, UserService userService) {
        super(resourceLoadService, projectCostService, sessionService, LoggerFactory.getLogger(ProjectService.class));
        this.projectRepository = projectRepository;
        this.projectSpecificationsFactory = projectSpecificationsFactory;
        this.sessionService = sessionService;
        this.userService = userService;
    }


    public Project create(Project project) {
        project.setId(null);

        if(areDateInconsistent(project))
            throw buildDatesNotConsistentException();

        setEstimatedEndDate(null, project);
        setFixedCost(project);
        project = save(project);

        refreshProjectCost(project, false);
        return project;
    }

    public Project update(Project project) {
        Project oldProject = getById(project.getId());

        if (isEstimatedEndDateInconsistent(oldProject, project))
            throw buildEstimatedEndDateException();

        if(isProjectStatusChanged(oldProject, project) && !canCurrentUserModifyProjectStatus(oldProject, project))
            throw buildProjectStatusException();

        if(isPreSaleFixedCostInconsistent(oldProject, project))
            throw buildPreSaleFixedCostException();

        Set<Role> roles = sessionService.getCurrentUser().getAuthorities();

        boolean isRolePSE_PSM_PSL = roles.stream()
                .anyMatch(role -> role.equals(Role.PSE) || role.equals(Role.PSM) || role.equals(Role.PSL));

        boolean isProjectInPreSale = oldProject.getStatus().equals("Pre-Sale");

        // Permetti la modifica del progetto se il ruolo è PSE, PSM, o PSL e il progetto è in pre-sale,
        // oppure se l'utente ha un altro ruolo (non PSE, PSM, PSL).
        if (isRolePSE_PSM_PSL && !isProjectInPreSale) {
            throw buildRolesDontHavePermissionsException();
        }

        project = updateEntity(project);
        refreshProjectCost(project, false);

        if (isProjectStatusChanged(oldProject, project))
            updateResourceLoads(project);

        return project;
    }


    private Project save(Project project) {
        try {
            if(sessionService.getCurrentUser() != null) logger.info(USER_DESCRIPTION, sessionService.getCurrentUser().getUsername());

            logger.info(SAVING_ENTITY, project.toString());
            project = projectRepository.save(project);
            logger.info(SAVED_ENTITY);
            return project;

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
                        if(detailedMessage.contains("pre_sale_fixed_cost_domain"))
                            errorMessage = "The pre sale fixed cost must be greater than 0";
                        if(detailedMessage.contains("current_fixed_cost_domain"))
                            errorMessage = "The fixed cost must be greater than 0";
                        if(detailedMessage.contains("start_date_le_pre_sale_scheduled_end_date"))
                            errorMessage = "The pre sale scheduled start date must be earlier than the pre sale scheduled end date";
                        if(detailedMessage.contains("start_date_le_estimated_end_date"))
                            errorMessage = "The pre sale scheduled start date must be earlier than the end date";
                        if(detailedMessage.contains("kom_date_le_end_date"))
                            errorMessage = "The KoM date must be earlier than the end date";

                        if(detailedMessage.contains("dates_check"))
                            errorMessage = "The KoM date must be earlier than the pre sale scheduled end date and the estimated end date";

                        if(detailedMessage.contains("project_start_date_le_end_date"))
                            errorMessage = "The pre sale start date must be earlier than the end date";

                    }
                }
            }
            logger.error(errorMessage);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);

        } catch (DataIntegrityViolationException ex) {
            String message = CONSTRAINT_VIOLATION;

            if(ex.getCause() != null) {
                String detailedMessage = ex.getCause().getCause().getMessage();
                if (detailedMessage.contains("project_id_unique"))
                    message = "This project id has already been associated with another project";
            }
            logger.error(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);

        }
    }


    private boolean canCurrentUserModifyProjectStatus(Project oldProject, Project project) {
        Set<Role> roles = sessionService.getCurrentUser().getAuthorities();
        String oldStatus = oldProject.getStatus();
        String newStatus = project.getStatus();
        if (roles.contains(Role.PSM) || roles.contains(Role.PSE) || roles.contains(Role.PSL)) {
            if (oldStatus.equals("Pre-Sale")) {
                return newStatus.equals("Lost") || newStatus.equals("Cancelled") || newStatus.equals("To Start");
            } else return false;
        }
        return true;
    }

    public Project updateEntity(Project project) {
        Project oldProject = getById(project.getId());

        if(project.isSpecial())
            throw buildSpecialProjectException();

        setFixedCost(project);
        setEstimatedEndDate(oldProject, project);
        project = save(project);
        return project;
    }



    public boolean exists(Project project){
        if (!projectRepository.existsById(project.getId())) {
            throw buildEntityWithIdNotFoundException(project.getId(), PROJECT_ID_NOT_FOUND);
        }
        return true;
    }

    public boolean existsByProjectId(String projectId){
        Optional<Project> optionalProject = projectRepository.findProjectByProjectId(projectId);
        return optionalProject.isPresent();
    }

    public Project getById(Long id) {
        return getById(projectRepository, id, PROJECT_ID_NOT_FOUND);
    }

    public void deleteById(Long id) {
        deleteById(projectRepository, id, PROJECT_ID_NOT_FOUND);
    }

    public List<Project> getAll(){
        return getAll(projectRepository);
    }





    private void updateResourceLoads(Project project){
        project = getById(project.getId());

        Set<Resource> distinctResources = project.getAllocations().stream()
                .map(Allocation::getResource)
                .collect(Collectors.toSet());

        for (Resource resource : distinctResources)
            refreshResourceLoad(LocalDate.now().getYear(), resource);
    }

    private boolean isProjectStatusChanged(Project oldProject, Project project){
        return !oldProject.getStatus().equals(project.getStatus());
    }

    private boolean isEstimatedEndDateInconsistent(Project oldProject, Project project){
        Date oldEstDate = new Date(oldProject.getEstimatedEndDate().getTime());
        return !oldEstDate.equals(project.getEstimatedEndDate());
    }

    private boolean isPreSaleFixedCostInconsistent(Project oldProject, Project project){
        if(project.getStatus().equals("Pre-Sale") || project.getStatus().equals("To Start"))
            return false;

        return (oldProject.getPreSaleFixedCost()!=null
                    && !oldProject.getPreSaleFixedCost().equals(project.getPreSaleFixedCost()));

    }


    private boolean areDateInconsistent(Project project){
        LocalDate preScheduledEndDate = getLocalDate(project.getPreSaleScheduledEndDate());
        LocalDate startDate = getLocalDate(project.getStartDate());

        return areDatesWeekendOrHolidays(startDate, preScheduledEndDate);
    }

    private void setEstimatedEndDate(Project oldProject, Project project){
        if(project.getStatus().equals("Pre-Sale") || project.getStatus().equals("To Start"))
            project.setEstimatedEndDate(project.getPreSaleScheduledEndDate());

        else if(oldProject != null && oldProject.getStatus().equals("To Start")
                && project.getStatus().equals("In Progress")){

            Date lastAllocationDate = null;
            for (Allocation allocation : oldProject.getAllocations())
                if(lastAllocationDate == null)
                    lastAllocationDate = allocation.getEndDate();
                else
                    if(allocation.getEndDate().after(lastAllocationDate))
                        lastAllocationDate = allocation.getEndDate();

            if(lastAllocationDate != null)
                project.setEstimatedEndDate(lastAllocationDate);
        }
    }

    private void setFixedCost(Project project){
        if(project.getStatus().equals("Pre-Sale") || project.getStatus().equals("To Start"))
            project.setCurrentFixedCost(project.getPreSaleFixedCost());
    }

    @Transactional
    public Page<Project> searchAdvanced(Optional<Filter<Project>> filter, Pageable pageable) {
        try {

            Pair<Boolean, String> sortingInfo = isSortedOnNonDirectlyMappedField(sortingFields, pageable);
            boolean isSorted = sortingInfo.getFirst();
            String sortingProperty = sortingInfo.getSecond();
            Page<Project> projectsPage;

            if(isSorted) {
                List<Project> projects = filter.map(projectFilter ->
                        projectRepository.findAll(getSpecificationForAdvancedSearch(projectFilter))
                ).orElseGet(projectRepository::findAll);

                projectsPage = getPage(sortingFields, projects, pageable, sortingProperty);

            } else {
                projectsPage = filter.map(projectFilter ->
                        projectRepository.findAll(getSpecificationForAdvancedSearch(projectFilter), pageable)
                ).orElseGet(() -> projectRepository.findAll(pageable));

            }

            projectsPage = removeDuplicates(projectsPage);
            return applyVisibilityFilter(projectsPage);

        } catch (PropertyReferenceException ex) {
            String message = String.format(INVALID_SEARCH_CRITERIA, ex.getMessage());
            logger.debug(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }



    private Page<Project> applyVisibilityFilter(Page<Project> projectsPage) {

        List<Project> filteredProjects = new ArrayList<>();

        for (Project project : projectsPage.getContent()) {
            if (!project.isSpecial())
                filteredProjects.add(project);
        }
        return new PageImpl<>(filteredProjects, projectsPage.getPageable(), projectsPage.getTotalElements());
    }

    private boolean checkPermissionForAllProjectsVisibility(Set<Role> userRoles){
        return userRoles.contains(Role.ADMIN) || userRoles.contains(Role.GDM) || userRoles.contains(Role.DUM)
                || userRoles.contains(Role.PSM) || userRoles.contains(Role.PSE) || userRoles.contains(Role.PSL);
    }

    private Specification<Project> getSpecificationForAdvancedSearch(Filter<Project> projectFilter){


        Set<Role> roles = sessionService.getCurrentUser().getAuthorities();
        if ((roles.contains(Role.PM) || roles.contains(Role.DTL)) && !checkPermissionForAllProjectsVisibility(roles)) {
            Long userId = sessionService.getCurrentUser().getId();
            User user = userService.getById(userId);

            return projectFilter.toSpecification(projectSpecificationsFactory).and(
                    projectSpecificationsFactory.buildProjectsOfAResourceByIdAndRole(user.getResource().getId(), Role.PM).or(
                    projectSpecificationsFactory.buildProjectsOfAResourceByIdAndRole(user.getResource().getId(), Role.DTL)));

        }
        return projectFilter.toSpecification(projectSpecificationsFactory);
    }




    private ResponseStatusException buildSpecialProjectException() {
        String message = String.format(SPECIAL_PROJECT_NOT_EDITABLE);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseStatusException buildEstimatedEndDateException() {
        String message = String.format(ESTIMATED_END_DATE_NOT_EDITABLE);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseStatusException buildProjectStatusException() {
        String message = String.format(PROJECT_STATUS_NOT_EDITABLE);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseStatusException buildPreSaleFixedCostException() {
        String message = String.format(PRE_SALE_FIXED_COST_NOT_EDITABLE);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseStatusException buildEndDatesException (){
        String message = String.format(DATES_NOT_CONSISTENT);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private ResponseStatusException buildRolesDontHavePermissionsException() {
        String message = String.format(ROLES_PERMISSIONS);
        logger.debug(message);
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

}
