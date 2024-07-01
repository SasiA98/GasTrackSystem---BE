package com.teoresi.staff.services.old.customs;

import com.teoresi.staff.dtos.old.CustomsDTO.ProjectGanttDTO;
import com.teoresi.staff.entities.old.Project;
import com.teoresi.staff.entities.old.WeeklyProjectCost;
import com.teoresi.staff.libs.utils.Holiday;
import com.teoresi.staff.libs.utils.Pair;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import com.teoresi.staff.repositories.old.customs.DailyProjectCostRepository;
import com.teoresi.staff.repositories.old.customs.WeeklyProjectCostRepository;
import com.teoresi.staff.security.services.SessionService;
import com.teoresi.staff.services.old.ProjectService;
import com.teoresi.staff.shared.services.BasicService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableAsync
public class ProjectCostService extends BasicService {
    @Autowired
    private EntityManager entityManager;

    private final ProjectService projectService;
    private final WeeklyProjectCostRepository weeklyProjectCostRepository;
    private final DailyProjectCostRepository dailyProjectCostRepository;

    public ProjectCostService(@Lazy ProjectService projectService, SessionService sessionService, WeeklyProjectCostRepository weeklyProjectCostRepository, DailyProjectCostRepository dailyProjectCostRepository) {
        super(sessionService, LoggerFactory.getLogger(ProjectCostService.class));
        this.projectService = projectService;
        this.weeklyProjectCostRepository = weeklyProjectCostRepository;
        this.dailyProjectCostRepository = dailyProjectCostRepository;
    }

    public PageDTO<ProjectGanttDTO> getProjectsGanttByUnitIdYearAndProjectStatus(Long unitId, int year, Set<String> projectStatuses, Pageable pageable){

        List<WeeklyProjectCost> weeklyProjectsCosts = weeklyProjectCostRepository.findByUnitIdYear(unitId, year);

        if(projectStatuses != null && !projectStatuses.isEmpty())
            weeklyProjectsCosts = weeklyProjectsCosts.stream()
                    .filter(wPCs -> projectStatuses.contains(wPCs.getProject().getStatus())).collect(Collectors.toList());

        return loadProjectsGanttView(weeklyProjectsCosts, year, pageable);
    }

    private PageDTO<ProjectGanttDTO> loadProjectsGanttView(List<WeeklyProjectCost> weeklyProjectsLoads, int year, Pageable pageable){

        Set<Project> projects = weeklyProjectsLoads.stream()
                .map(WeeklyProjectCost::getProject)
                .collect(Collectors.toCollection(TreeSet::new));

        List<Project> paginatedResources = getPaginatedProjects(projects, pageable);

        List<ProjectGanttDTO> projectsGantt = new ArrayList<>();

        for (Project project : paginatedResources) {

            Set<WeeklyProjectCost> weeklyProjectCosts = weeklyProjectsLoads.stream()
                    .filter(w -> w.getProject().equals(project))
                    .collect(Collectors.toSet());

            Pair<Map<Integer, Map<Integer,Integer>>,Map<Integer, Map<Integer,Integer>>> espCost = computeWeeklyProjectCostsInfo(weeklyProjectCosts,false);
            Pair<Map<Integer, Map<Integer,Integer>>,Map<Integer, Map<Integer,Integer>>> actCost = computeWeeklyProjectCostsInfo(weeklyProjectCosts, true);

            ProjectGanttDTO projectGanttDTO = ProjectGanttDTO.builder()
                    .unitTrigram(project.getUnit().getTrigram())
                    .status(project.getStatus())
                    .name(project.getName())
                    .year(year)
                    .estimatedCost(espCost.getFirst())
                    .estimatedCostPct(espCost.getSecond())
                    .actualCost(actCost.getFirst())
                    .actualCostPct(actCost.getSecond())
                    .build();

            projectsGantt.add(projectGanttDTO);
        }

        return getPageDTO(projectsGantt, pageable, projects.size());
    }


    private PageDTO<ProjectGanttDTO> getPageDTO(List<ProjectGanttDTO> context, Pageable pageable, int totalElements) {
        Page<ProjectGanttDTO> page = new PageImpl<>(context, pageable, totalElements);

        return PageDTO.<ProjectGanttDTO>builder()
                .content(page.getContent())
                .first(page.isFirst())
                .last(page.isLast())
                .number(page.getNumber())
                .numberOfElements(page.getNumberOfElements())
                .size(page.getSize())
                .sort(page.getSort().toList())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }


    private Pair<Map<Integer, Map<Integer, Integer>>, Map<Integer, Map<Integer, Integer>>> computeWeeklyProjectCostsInfo(Set<WeeklyProjectCost> weeklyProjectCosts, boolean isActualCost) {

        Map<Integer,Map<Integer,Integer>> annualLoad = new HashMap<>();
        Map<Integer,Map<Integer,Integer>> annualLoadPct = new HashMap<>();


        for(int month=1; month<=12; month++){
            int finalMonth = month;

            Map<Integer,Integer> monthlyLoad = new HashMap<>();
            Map<Integer,Integer> monthlyLoadPct = new HashMap<>();

            Set<WeeklyProjectCost> monthlyProjectCost = weeklyProjectCosts.stream()
                    .filter(w -> w.getMonth() == finalMonth)
                    .collect(Collectors.toSet());

            for(WeeklyProjectCost weeklyProjectCost : monthlyProjectCost)
                if(isActualCost) {
                    monthlyLoad.put(weeklyProjectCost.getWeekNumber(), weeklyProjectCost.getCumulativeActualCost());
                    monthlyLoadPct.put(weeklyProjectCost.getWeekNumber(), weeklyProjectCost.getCumulativeActualCostPct());
                }else {
                    monthlyLoad.put(weeklyProjectCost.getWeekNumber(), weeklyProjectCost.getCumulativeEstimatedCost());
                    monthlyLoadPct.put(weeklyProjectCost.getWeekNumber(), weeklyProjectCost.getCumulativeEstimatedCostPct());
                }
            annualLoad.put(month,monthlyLoad);
            annualLoadPct.put(month,monthlyLoadPct);
        }
        return new Pair<>(annualLoad, annualLoadPct);
    }

    private List<Project> getPaginatedProjects(Set<Project> projects, Pageable pageable){

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Project> paginatedProjects;

        if (projects.size() < startItem)
            return Collections.emptyList();

        int toIndex = Math.min(startItem + pageSize, projects.size());
        paginatedProjects = new ArrayList<>(projects).subList(startItem, toIndex);

        return paginatedProjects;
    }


    public void refreshGanttForAllProjects(int year){

        List<Project> projects = projectService.getAll();

        for (Project project : projects)
            if(!project.isSpecial() &&
                    (project.getEndDate() == null || project.getEndDate().getYear() >= year))

                asyncRefreshProjectGantt(project);
    }

    @Async
    public void asyncRefreshProjectGantt(Project project){
        if(project.isSpecial())
           return;

        callInitProjectCostProcedure(project);
        removeHolidaysToDailyProjectCost(project);
        callRefreshDailyProjectCostProcedure(project);
        callRefreshWeeklyProjectCostProcedure(project);
    }

    public void syncRefreshProjectGantt(Project project){
        if(project.isSpecial())
            return;

        callInitProjectCostProcedure(project);
        removeHolidaysToDailyProjectCost(project);
        callRefreshDailyProjectCostProcedure(project);
        callRefreshWeeklyProjectCostProcedure(project);
    }

    private void callInitProjectCostProcedure(Project project){

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("init_project_cost");
        query.registerStoredProcedureParameter("project_id", Long.class, ParameterMode.IN);

        query.setParameter("project_id", project.getId());
        query.execute();
    }

    private void callRefreshDailyProjectCostProcedure(Project project){

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("refresh_daily_project_cost");
        query.registerStoredProcedureParameter("project_id", Long.class, ParameterMode.IN);

        query.setParameter("project_id", project.getId());
        query.execute();
    }

    private void callRefreshWeeklyProjectCostProcedure(Project project){

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("refresh_weekly_project_cost");
        query.registerStoredProcedureParameter("project_id", Long.class, ParameterMode.IN);

        query.setParameter("project_id", project.getId());
        query.execute();
    }

    private void removeHolidaysToDailyProjectCost(Project project) {
        project = projectService.getById(project.getId());

        LocalDate startDate = getLocalDate(project.getStartDate());
        LocalDate endDate = getLocalDate(project.getEstimatedEndDate());

        Set<Holiday> holidays = retrieveHolidays(startDate, endDate);

        for (LocalDate tmpDate = startDate; !tmpDate.isAfter(endDate); tmpDate = tmpDate.plusDays(1))

            if (!isWeekend(tmpDate) && holidayManagementService.isHoliday(holidays, tmpDate))
                dailyProjectCostRepository.removeHoliday(project, tmpDate.getYear(), tmpDate.getDayOfYear());
    }
}
