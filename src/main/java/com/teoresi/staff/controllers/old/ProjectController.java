package com.teoresi.staff.controllers.old;

import com.teoresi.staff.dtos.old.AllocationDTO;
import com.teoresi.staff.dtos.old.ProjectDTO;
import com.teoresi.staff.entities.old.Project;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import com.teoresi.staff.libs.data.models.Filter;
import com.teoresi.staff.mappers.old.AllocationMapper;
import com.teoresi.staff.mappers.old.ProjectMapper;
import com.teoresi.staff.services.old.AllocationService;
import com.teoresi.staff.services.old.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
import java.util.List;
import java.util.Optional;

@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final AllocationMapper  allocationMapper;
    private final AllocationService allocationService;

    private final Logger logger = LoggerFactory.getLogger(ProjectService.class);


    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL')")
    public ProjectDTO create(@Valid @RequestBody ProjectDTO projectDTO) throws IOException, ParseException, InterruptedException {
        Project project = projectMapper.convertDtoToModel(projectDTO);
        return projectMapper.convertModelToDTO(projectService.create(project));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL')")
    public ProjectDTO update(@PathVariable Long id, @Valid @RequestBody ProjectDTO projectDTO) {
        projectDTO.setId(id);
        Project project = projectMapper.convertDtoToModel(projectDTO);
        return projectMapper.convertModelToDTO(projectService.update(project));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL', 'PM', 'DTL')")
    @PostMapping("/advanced-search")
    public PageDTO<ProjectDTO> searchAdvanced(
            @RequestBody(required = false) Optional<Filter<Project>> filter,
            @PageableDefault Pageable pageable) {

        Page<Project> resultPage = projectService.searchAdvanced(filter, pageable);
        return projectMapper.convertModelsPageToDtosPage(resultPage);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL')")
    public void deleteById(@PathVariable Long id) {
        projectService.deleteById(id);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL', 'PM', 'DTL')")
    public ProjectDTO getById(@PathVariable Long id) {
        return projectMapper.convertModelToDTO(projectService.getById(id));
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL')")
    public List<ProjectDTO> getAll() {
        return projectMapper.convertModelsToDtos(projectService.getAll());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL', 'PM', 'DTL')")
    @GetMapping("/{id}/allocations")
    public List<AllocationDTO> getAllocationsById(@PathVariable Long id) {
        return allocationMapper.convertModelsToDtos(allocationService.getAllByProjectId(id));
    }

}
