package com.teoresi.staff.services.old;

import com.teoresi.staff.dtos.old.CustomsDTO.ResourceTimesheetInfoDTO;
import com.teoresi.staff.entities.old.Resource;
import com.teoresi.staff.entities.old.Timesheet;
import com.teoresi.staff.entities.old.TimesheetProject;
import com.teoresi.staff.entities.old.Unit;
import com.teoresi.staff.libs.utils.TimesheetExcelHelper;
import com.teoresi.staff.repositories.old.customs.ResourceRepository;
import com.teoresi.staff.repositories.old.customs.TimesheetRepository;
import com.teoresi.staff.security.services.SessionService;
import com.teoresi.staff.shared.models.Role;
import com.teoresi.staff.shared.services.BasicService;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TimesheetService extends BasicService {

    private final ResourceRepository resourceRepository;
    private final TimesheetRepository timesheetRepository;
    private final TimesheetProjectService timesheetProjectService;

    private static final String TIMESHEET_ID_NOT_FOUND =  "Timesheet with id %d not found";

    public TimesheetService(ResourceRepository resourceRepository, SessionService sessionService, TimesheetRepository timesheetRepository, TimesheetProjectService timesheetProjectService, TimesheetExcelHelper timesheetExcelHelper) {
        super(sessionService, LoggerFactory.getLogger(TimesheetService.class));
        this.resourceRepository = resourceRepository;
        this.timesheetRepository = timesheetRepository;
        this.timesheetProjectService = timesheetProjectService;
    }

    public Timesheet create(Resource resource, LocalDate startDate, LocalDate endDate){

        Optional<Timesheet> timesheetOpt = timesheetRepository.findByDateAndResourceId(resource.getId(), startDate.getMonthValue(), startDate.getYear());

        if(timesheetOpt.isPresent())
            return timesheetOpt.get();

        Timesheet timesheet = new Timesheet(startDate, endDate, resource);
        timesheet = save(timesheetRepository, timesheet);
        timesheetProjectService.addTimeOffProject(timesheet);
        timesheetProjectService.addHolidaysProject(timesheet);

        return timesheet;
    }

    public Timesheet update(Timesheet timesheet) {

        if (!timesheetRepository.existsById(timesheet.getId()))
            throw buildEntityWithIdNotFoundException(timesheet.getId(), TIMESHEET_ID_NOT_FOUND);

        Timesheet storedTimesheet = getById(timesheetRepository, timesheet.getId(), TIMESHEET_ID_NOT_FOUND);

        if (isEndDateChanged(storedTimesheet, timesheet))
            updateTimesheetProjectIfEndDateChanged(timesheet);

        return save(timesheetRepository, timesheet);
    }

    public Timesheet getById(Long id) {
        return getById(timesheetRepository, id, TIMESHEET_ID_NOT_FOUND);
    }

    private void updateTimesheetProjectIfEndDateChanged(Timesheet timesheet){

        Timesheet storedTimesheet = getById(timesheetRepository, timesheet.getId(), TIMESHEET_ID_NOT_FOUND);
        for (TimesheetProject timesheetProject : storedTimesheet.getRelatedProjects())
            if(!timesheetProject.getProject().isSpecial())
                timesheetProjectService.update(timesheetProject);
    }


    public void generateTimesheetsResource(Resource resource){

        LocalDate resourceHiringDate = getLocalDate(resource.getHiringDate());
        LocalDate sixMonthsAgoFirstDay  = LocalDate.now().minusMonths(6).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate currentDate = LocalDate.now();
        LocalDate timesheetsStartDate = resourceHiringDate.isAfter(sixMonthsAgoFirstDay) ? resourceHiringDate : sixMonthsAgoFirstDay;

        // baseline
        LocalDate timesheetsEndDate = currentDate.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        long monthDifference = ChronoUnit.MONTHS.between(timesheetsStartDate, timesheetsEndDate);

        // corner case
        if(resource.getLeaveDate()!=null){
            LocalDate leaveDate = getLocalDate(resource.getLeaveDate());

            if(leaveDate.isBefore(timesheetsEndDate)) {
                timesheetsEndDate = leaveDate;
                monthDifference = ChronoUnit.MONTHS.between(timesheetsStartDate, leaveDate);
            }
        }


        for(int i=0; i <= monthDifference; i++) {
            LocalDate startDateTmp = timesheetsStartDate.plusMonths(i).with(TemporalAdjusters.firstDayOfMonth());
            LocalDate endDateTmp = timesheetsStartDate.plusMonths(i).with(TemporalAdjusters.lastDayOfMonth());

            if(startDateTmp.isBefore(resourceHiringDate))
                startDateTmp = resourceHiringDate;

            if(endDateTmp.isAfter(timesheetsEndDate))
                endDateTmp = timesheetsEndDate;

            create(resource, startDateTmp, endDateTmp);
        }
    }

    public void initializeTimesheetsForExistingResources(){
        List<Resource> resources = resourceRepository.findAll();

        for(Resource resource : resources){
            if (!resource.getRoles().contains(Role.ADMIN))
                generateTimesheetsResource(resource);
        }
    }

    @Transactional
    public void createTimesheetsForAllResourcesMonthly() {

        List<Resource> resources = resourceRepository.findAll();

        for (Resource resource : resources) {

            LocalDate nextMonthFirstDay = LocalDate.now().plusMonths(1);
            LocalDate nextMonthLastDay = LocalDate.now().plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());

            LocalDate leaveDate = resource.getLeaveDate() != null ? getLocalDate(resource.getLeaveDate()) : null;
            LocalDate resourceHiringDate = getLocalDate(resource.getHiringDate());

            LocalDate timesheetEndDate = nextMonthLastDay;

            if (!resource.getRoles().contains(Role.ADMIN) && resourceHiringDate.isBefore(nextMonthLastDay))
                if (leaveDate != null && !leaveDate.isBefore(nextMonthFirstDay) && !leaveDate.isAfter(nextMonthLastDay))
                    timesheetEndDate = leaveDate;

            Timesheet timesheet = create(resource, nextMonthFirstDay, timesheetEndDate);
            save(timesheetRepository, timesheet);

        }
    }

    public List<ResourceTimesheetInfoDTO> getResourcesTimesheetsInfoByUnitIdAndDate(Long unitId, LocalDate date) {

        List<Timesheet> timesheets = timesheetRepository.findByDate(date.getMonthValue(), date.getYear());
        timesheets = timesheets.stream().filter(t -> t.getResource().getUnit().getId().equals(unitId)).collect(Collectors.toList());
        filterTimesheetsWithInProgressProjects(timesheets);

        return loadResourcesTimesheetsInfo(timesheets);
    }

    public List<ResourceTimesheetInfoDTO> getAllResourcesTimesheetsInfoByDate(LocalDate date) {

        List<Timesheet> timesheets = timesheetRepository.findByDate(date.getMonthValue(), date.getYear());
        filterTimesheetsWithInProgressProjects(timesheets);
        return loadResourcesTimesheetsInfo(timesheets);
    }

    private boolean isEndDateChanged(Timesheet storedTimesheet, Timesheet timesheet){
        LocalDate oldEndDate = storedTimesheet.getEndDate() != null ? storedTimesheet.getEndDate() : null;
        LocalDate endDate = timesheet.getEndDate() != null ? timesheet.getEndDate() : null;

        return (oldEndDate != null && endDate != null && !endDate.equals(oldEndDate));
    }

    private void updateTimesheetProjectOnEndDateChanged(Timesheet timesheet){

        Timesheet storedTimesheet = getById(timesheet.getId());
        for (TimesheetProject timesheetProject : storedTimesheet.getRelatedProjects())
            if(!timesheetProject.getProject().isSpecial())
                timesheetProjectService.update(timesheetProject);
    }

    private  List<ResourceTimesheetInfoDTO> loadResourcesTimesheetsInfo(List<Timesheet> timesheets) {
        List<ResourceTimesheetInfoDTO> resourceTimesheetInfoDTOS = new ArrayList<>();

        for (Timesheet timesheet : timesheets){
            Unit unit = timesheet.getResource().getUnit();
            Resource timesheetResource = timesheet.getResource();
            resourceTimesheetInfoDTOS.add(new ResourceTimesheetInfoDTO(unit.getTrigram(), unit.getId(), timesheetResource.getId(), timesheetResource.getName(), timesheetResource.getSurname(), timesheet.getRemainingWorkHours()));
        }
        return resourceTimesheetInfoDTOS;
    }

    public Timesheet getTimesheetByDateAndResourceId(Long id, int month, int year) {
        Optional<Timesheet> timesheet = timesheetRepository.findByDateAndResourceId(id, month, year);

        return timesheet.map(this::applyInProgressFilter).orElse(null);
    }


    private void filterTimesheetsWithInProgressProjects(List<Timesheet> timesheets) {
        for (Timesheet t : timesheets)
            applyInProgressFilter(t);
    }

    private Timesheet applyInProgressFilter(Timesheet timesheet){
        if(timesheet==null)
            return null;

        Set<TimesheetProject> tps = timesheet.getRelatedProjects();
        tps = tps.stream().filter(TimesheetProject::isInProgress).collect(Collectors.toSet());
        timesheet.setRelatedProjects(tps);
        return timesheet;
    }

 }