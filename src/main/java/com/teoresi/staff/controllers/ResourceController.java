package com.teoresi.staff.controllers;

import com.teoresi.staff.dtos.*;
import com.teoresi.staff.dtos.CustomsDTO.ResourcesSalaryDetailDTO;
import com.teoresi.staff.dtos.ResourceHourlyCostDTO;
import com.teoresi.staff.dtos.TimesheetDTO;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import com.teoresi.staff.libs.data.models.Filter;
import com.teoresi.staff.mappers.*;
import com.teoresi.staff.services.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.teoresi.staff.entities.Resource;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("resources")
public class ResourceController {

    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;
    private final ResourceSkillMapper resourceSkillMapper;

    private final SkillMapper skillMapper;
    private final SkillService skillService;

    private final ResourceHourlyCostMapper resourceHourlyCostMapper;
    private final ResourceHourlyCostService resourceHourlyCostService;
    private final ResourceSalaryDetailsService resourceSalaryDetailsService;
    private final ResourceSalaryDetailsMapper resourceSalaryDetailsMapper;

    private final AllocationMapper allocationMapper;
    private final AllocationService allocationService;

    private final TimesheetService timesheetService;
    private final TimesheetMapper timesheetMapper;

    private final Logger logger = LoggerFactory.getLogger(ResourceService.class);


    // ------------------------------  REQUIREMENTS SATISFIED ------------------------------ //

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public ResourceDTO create(@Valid @RequestBody ResourceDTO resourceDTO) throws IOException, ParseException, InterruptedException {
        Resource resource = resourceMapper.convertDtoToModel(resourceDTO);
        return resourceMapper.convertModelToDTO(resourceService.create(resource, false));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public ResourceDTO update(@PathVariable Long id, @Valid @RequestBody ResourceDTO resourceDTO) {
        resourceDTO.setId(id);
        Resource resource = resourceMapper.convertDtoToModel(resourceDTO);
        return resourceMapper.convertModelToDTO(resourceService.update(resource));
    }


    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL', 'PM', 'DTL', 'STAFF')")
    @PostMapping("/advanced-search")
    public PageDTO<ResourceDTO> searchAdvanced(
            @RequestBody(required = false) Optional<Filter<Resource>> filter,
            @PageableDefault Pageable pageable) {

        Page<Resource> resultPage = resourceService.searchAdvanced(filter, pageable);
        return resourceMapper.convertModelsPageToDtosPage(resultPage);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public void deleteById(@PathVariable Long id) {
        resourceService.deleteById(id);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL', 'PM', 'DTL', 'STAFF')")
    public ResourceDTO getById(@PathVariable Long id) {
        return resourceMapper.convertModelToDTO(resourceService.getById(id));
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL', 'PM', 'DTL', 'STAFF')")
    public List<ResourceDTO> getAll() {
        return resourceMapper.convertModelsToDtos(resourceService.getAll());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSL', 'PSM')")
    @GetMapping("/{id}/hourlycosts")
    public List<ResourceHourlyCostDTO> getHourlyCostsById(@PathVariable Long id) {
        return resourceHourlyCostMapper.convertModelsToDtos(resourceHourlyCostService.getAllByResourceId(id));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'STAFF')")
    @GetMapping("/{id}/salaryDetails")
    public List<ResourcesSalaryDetailDTO> getSalaryDetailsById(@PathVariable Long id) {
        return resourceSalaryDetailsMapper.convertModelsToDtos(resourceSalaryDetailsService.getAllByResourceId(id));

    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PM')")
    @GetMapping("/{id}/allocations")
    public List<AllocationDTO> getAllocationsById(@PathVariable Long id) {
        return allocationMapper.convertModelsToDtos(allocationService.getAllByResourceId(id));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    @GetMapping("/{id}/timesheet")
    public TimesheetDTO getTimesheetByIdAndDate(@PathVariable Long id, @RequestParam("date") String date) {
        LocalDate timesheetDate = LocalDate.parse(date);
        return timesheetMapper.convertModelToDTO(timesheetService.getTimesheetByDateAndResourceId(id, timesheetDate.getMonthValue(), timesheetDate.getYear()));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL', 'PM', 'DTL', 'STAFF')")
    @GetMapping("/{id}/skills")
    public List<ResourceSkillDTO> getSkillsByResourceId(@PathVariable Long id) {
        return resourceSkillMapper.convertModelsToDtos(skillService.getAllByResourceId(id));
    }

    @PostMapping("/{resourceId}/skills/{skillId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'STAFF')")
    public ResourceDTO addOrUpdateSkill(@PathVariable Long resourceId, @PathVariable Long skillId,
                                @Valid @RequestBody ResourceSkillDTO resourceSkillDTO) {
        return resourceMapper.convertModelToDTO(resourceService.addOrUpdateSkill(resourceId, skillId, resourceSkillDTO));
    }

    @DeleteMapping("/{resourceId}/skills/{skillId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'STAFF')")
    public void deleteById(@PathVariable Long resourceId, @PathVariable Long skillId) {
        resourceService.deleteSkillById(resourceId, skillId);
    }
}
