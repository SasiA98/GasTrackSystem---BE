package com.teoresi.staff.services.customs;

import com.teoresi.staff.dtos.CustomsDTO.ResourceLoadDTO;
import com.teoresi.staff.entities.Resource;
import com.teoresi.staff.entities.customs.WeeklyResourceLoad;
import com.teoresi.staff.libs.utils.Holiday;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import com.teoresi.staff.repositories.ResourceRepository;
import com.teoresi.staff.repositories.customs.DailyResourceLoadRepository;
import com.teoresi.staff.repositories.customs.WeeklyResourceLoadRepository;
import com.teoresi.staff.security.services.SessionService;
import com.teoresi.staff.shared.models.Role;
import com.teoresi.staff.shared.services.BasicService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
@EnableAsync
public class ResourceLoadService extends BasicService {
    @Autowired
    private EntityManager entityManager;
    private final DailyResourceLoadRepository dailyResourceLoadRepository;
    private final ResourceRepository resourceRepository;
    private final WeeklyResourceLoadRepository weeklyResourceLoadRepository;

    public ResourceLoadService(DailyResourceLoadRepository dailyResourceLoadRepository, SessionService sessionService, ResourceRepository resourceRepository, WeeklyResourceLoadRepository weeklyResourceLoadRepository) {
        super(sessionService, LoggerFactory.getLogger(ResourceLoadService.class));
        this.dailyResourceLoadRepository = dailyResourceLoadRepository;
        this.resourceRepository = resourceRepository;
        this.weeklyResourceLoadRepository = weeklyResourceLoadRepository;
    }

    public PageDTO<ResourceLoadDTO> getResourcesLoadsByUnitIdYearAndPreAllocation(Long unitId, int year, boolean isPreAllocation, Pageable Pageable){
        List<WeeklyResourceLoad> weeklyResourcesLoads = weeklyResourceLoadRepository.findByYearAndUnitId(unitId, year, isPreAllocation);

        return loadWeeklyResourcesLoadsInfoView(weeklyResourcesLoads, year, Pageable);
    }

    private PageDTO<ResourceLoadDTO> loadWeeklyResourcesLoadsInfoView(List<WeeklyResourceLoad> weeklyResourcesLoads, int year, Pageable pageable){

        Set<Resource> resources = weeklyResourcesLoads.stream()
                .map(WeeklyResourceLoad::getResource)
                .collect(Collectors.toCollection(TreeSet::new));

        List<Resource> paginatedResources = getPaginatedResource(resources, pageable);

        List<ResourceLoadDTO> resourcesLoads = new ArrayList<>();

        for (Resource resource : paginatedResources) {

            Set<WeeklyResourceLoad> weeklyResourceLoads = weeklyResourcesLoads.stream()
                    .filter(w -> w.getResource().equals(resource))
                    .collect(Collectors.toSet());

            Map<Integer,Map<Integer,Integer>> saleLoad = computeWeeklyResourceLoadsInfo(weeklyResourceLoads,false);
            Map<Integer,Map<Integer,Integer>> realLoad = computeWeeklyResourceLoadsInfo(weeklyResourceLoads, true);

            ResourceLoadDTO weeklyResourceLoadsInfoDTO = ResourceLoadDTO.builder()
                    .unitTrigram(resource.getUnit().getTrigram())
                    .fullName(resource.getName().charAt(0) + ". " + resource.getSurname())
                    .year(year)
                    .weeklyRealCommitmentPct(realLoad)
                    .weeklySaleCommitmentPct(saleLoad)
                    .build();

            resourcesLoads.add(weeklyResourceLoadsInfoDTO);
        }

        return getPageDTO(resourcesLoads, pageable, resources.size());
    }

    private PageDTO<ResourceLoadDTO> getPageDTO(List<ResourceLoadDTO> context, Pageable pageable, int totalElements) {
        Page<ResourceLoadDTO> page = new PageImpl<>(context, pageable, totalElements);

        return PageDTO.<ResourceLoadDTO>builder()
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

    private Map<Integer, Map<Integer, Integer>> computeWeeklyResourceLoadsInfo(Set<WeeklyResourceLoad> weeklyResourceLoads, boolean isRealCommitment) {

        Map<Integer,Map<Integer,Integer>> annualLoad = new HashMap<>();

        for(int month=1; month<=12; month++){
            int finalMonth = month;

            Map<Integer,Integer> monthlyLoad = new HashMap<>();

            Set<WeeklyResourceLoad> monthlyResourceLoad = weeklyResourceLoads.stream()
                    .filter(w -> w.getMonth() == finalMonth)
                    .collect(Collectors.toSet());

            for(WeeklyResourceLoad weeklyResourceLoad : monthlyResourceLoad)
                if(isRealCommitment)
                    monthlyLoad.put(weeklyResourceLoad.getWeekNumber(), weeklyResourceLoad.getMeanCommitmentPct());
                else
                    monthlyLoad.put(weeklyResourceLoad.getWeekNumber(), weeklyResourceLoad.getHoursCommitmentPct());

            annualLoad.put(month,monthlyLoad);
        }
        return annualLoad;
    }

    private List<Resource> getPaginatedResource(Set<Resource> resources, Pageable pageable){


        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Resource> paginatedResources;

        if (resources.size() < startItem)
            return Collections.emptyList();

        int toIndex = Math.min(startItem + pageSize, resources.size());
        paginatedResources = new ArrayList<>(resources).subList(startItem, toIndex);

        return paginatedResources;
    }

    public void refreshTheLoadForAllResources(int year){
        List<Resource> resources = resourceRepository.findAll();

        for (Resource resource : resources){
            LocalDate leaveDate = resource.getLeaveDate() != null ? getLocalDate(resource.getLeaveDate()) : null;

            if (!resource.getRoles().contains(Role.ADMIN) && (leaveDate == null ||  leaveDate.getYear() >= year))
                refreshResourceLoad(year, resource);
        }

    }


    @Async
    public void refreshResourceLoad(int year, Resource resource){
        callInitResourceLoadProcedure(year,resource);
        addHolidaysToDailyResourceLoad(year, resource);
        callRefreshResourceLoadProcedure(year, resource);
        callRefreshWeeklyResourceLoadProcedure(year, resource);
    }


    private void callRefreshResourceLoadProcedure(int year, Resource resource){

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("refresh_resource_load");
        query.registerStoredProcedureParameter("year", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("resource_id", Long.class, ParameterMode.IN);

        query.setParameter("year", year);
        query.setParameter("resource_id", resource.getId());
        query.execute();
    }


    private void callInitResourceLoadProcedure(int year, Resource resource){

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("init_resource_load");
        query.registerStoredProcedureParameter("year", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("resource_id", Long.class, ParameterMode.IN);

        query.setParameter("year", year);
        query.setParameter("resource_id", resource.getId());
        query.execute();
    }


    private void callRefreshWeeklyResourceLoadProcedure(int year, Resource resource){

        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("refresh_weekly_resource_load");
        query.registerStoredProcedureParameter("year", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("resource_id", Long.class, ParameterMode.IN);

        query.setParameter("year", year);
        query.setParameter("resource_id", resource.getId());
        query.execute();
    }

    private void addHolidaysToDailyResourceLoad(int year, Resource resource) {
        LocalDate resourceHiringDate = resource.getHiringDate() != null ? getLocalDate(resource.getHiringDate()) : null;
        LocalDate resourceLeaveDate = resource.getLeaveDate() != null ? getLocalDate(resource.getLeaveDate()) : null;

        Set<Holiday> holidays = retrieveHolidays(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));

        for (Holiday holiday : holidays) {
            LocalDate date = holiday.getDate();

            if(!(date.getDayOfWeek() == DayOfWeek.SATURDAY) && !(date.getDayOfWeek() == DayOfWeek.SUNDAY)
                    && !date.isBefore(resourceHiringDate)
                    && (resourceLeaveDate == null || !date.isAfter(resourceLeaveDate)))
                dailyResourceLoadRepository.updateByUniqueFields(resource, year,  date.getDayOfYear());
        }
    }

}
