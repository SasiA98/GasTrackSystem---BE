package com.teoresi.staff.controllers.old;

import com.teoresi.staff.dtos.old.CustomsDTO.ProjectGanttDTO;
import com.teoresi.staff.dtos.old.CustomsDTO.ResourceLoadDTO;
import com.teoresi.staff.dtos.old.CustomsDTO.ResourceTimesheetInfoDTO;
import com.teoresi.staff.dtos.old.ResourceDTO;
import com.teoresi.staff.dtos.old.UnitDTO;
import com.teoresi.staff.entities.old.Unit;
import com.teoresi.staff.libs.data.models.Filter;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import com.teoresi.staff.mappers.old.ResourceMapper;
import com.teoresi.staff.mappers.old.TimesheetMapper;
import com.teoresi.staff.mappers.old.UnitMapper;
import com.teoresi.staff.services.old.TimesheetService;
import com.teoresi.staff.services.old.UnitService;
import com.teoresi.staff.services.old.customs.ProjectCostService;
import com.teoresi.staff.services.old.customs.ResourceLoadService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("units")
public class UnitController {

    private final UnitService unitService;
    private final UnitMapper unitMapper;
    private final ResourceMapper resourceMapper;

    private final TimesheetService timesheetService;
    private final TimesheetMapper timesheetMapper;

    private final ResourceLoadService resourceLoadService;
    private final ProjectCostService projectCostService;


    private final Logger logger = LoggerFactory.getLogger(UnitService.class);


    // ------------------------------  REQUIREMENTS SATISFIED ------------------------------ //

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public UnitDTO create(@Valid @RequestBody UnitDTO unitDTO) throws IOException, ParseException, InterruptedException {
        Unit unit = unitMapper.convertDtoToModel(unitDTO);
        return unitMapper.convertModelToDTO(unitService.create(unit));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public UnitDTO update(@PathVariable Long id, @Valid @RequestBody UnitDTO unitDTO) {
        unitDTO.setId(id);
        Unit unit = unitMapper.convertDtoToModel(unitDTO);
        return unitMapper.convertModelToDTO(unitService.update(unit));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PM', 'PSL', 'PSE', 'PSM')")
    @PostMapping("/advanced-search")
    public PageDTO<UnitDTO> searchAdvanced(
            @RequestBody(required = false) Optional<Filter<Unit>> filter,
            @PageableDefault Pageable pageable) {

        Page<Unit> resultPage = unitService.searchAdvanced(filter, pageable);
        return unitMapper.convertModelsPageToDtosPage(resultPage);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public void deleteById(@PathVariable Long id) {
        unitService.deleteById(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PM', 'PSL', 'PSE', 'PSM', 'STAFF')")
    public UnitDTO getById(@PathVariable Long id) {
        return unitMapper.convertModelToDTO(unitService.getById(id));
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PM', 'PSL', 'PSE', 'PSM', 'STAFF')")
    public List<UnitDTO> getAll() {
        return unitMapper.convertModelsToDtos(unitService.getAll());
    }


    @GetMapping("/{id}/active-resources")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PM', 'PSL', 'PSE', 'PSM')")
    public List<ResourceDTO> getActiveResources(@PathVariable Long id) {
        return resourceMapper.convertModelsToDtos(unitService.getActiveResourcesById(id));
    }


    @GetMapping("/{id}/active-resources-in-last-six-months")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public List<ResourceDTO> getActiveResourcesInLastSixMonths(@PathVariable Long id) {
        return resourceMapper.convertModelsToDtos(unitService.getActiveResourcesInLastSixMonthsById(id));
    }

    @GetMapping("/{id}/timesheets")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public List<ResourceTimesheetInfoDTO> getResourcesTimesheetsInfoByIdAndDate(@PathVariable Long id, @RequestParam("date") String date) {
        LocalDate localDate = LocalDate.parse(date);
        return timesheetService.getResourcesTimesheetsInfoByUnitIdAndDate(id, localDate);
    }

    @GetMapping("/timesheets")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public List<ResourceTimesheetInfoDTO> getAllResourcesTimesheetsInfoByDate(@RequestParam("date") String date) {
        LocalDate localDate = LocalDate.parse(date);
        return timesheetService.getAllResourcesTimesheetsInfoByDate(localDate);
    }


    @GetMapping("/resources-load")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public PageDTO<ResourceLoadDTO> getResourcesLoads(@RequestParam(value = "unit-id", required = false) Long id,
                                                  @RequestParam("year") Integer year,
                                                  @RequestParam(value = "pre-allocation", required = false) Boolean isPreAllocation,
                                                  @PageableDefault Pageable pageable) {
        return resourceLoadService.getResourcesLoadsByUnitIdYearAndPreAllocation(id, year, isPreAllocation, pageable);
    }

    @GetMapping("/projects-gantt")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public PageDTO<ProjectGanttDTO> getProjectsGantt(@RequestParam(value = "unit-id", required = false) Long id,
                                                 @RequestParam("year") Integer year,
                                                 @RequestParam(value = "status", required = false) Set<String> projectStatuses,
                                                 @PageableDefault Pageable pageable) {
        return projectCostService.getProjectsGanttByUnitIdYearAndProjectStatus(id, year, projectStatuses, pageable);
    }
}
