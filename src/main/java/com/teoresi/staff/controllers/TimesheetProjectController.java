package com.teoresi.staff.controllers;

import com.teoresi.staff.dtos.TimesheetProjectDTO;
import com.teoresi.staff.entities.TimesheetProject;
import com.teoresi.staff.mappers.TimesheetProjectMapper;
import com.teoresi.staff.services.TimesheetProjectService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;

@Getter
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
@RequestMapping("timesheet-projects")
public class TimesheetProjectController {

    private final TimesheetProjectService timesheetProjectService;
    private final TimesheetProjectMapper timesheetProjectMapper;

    private final Logger logger = LoggerFactory.getLogger(TimesheetProjectService.class);

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public TimesheetProjectDTO create(@Valid @RequestBody TimesheetProjectDTO timesheetProjectDTO) throws IOException, ParseException, InterruptedException {
        TimesheetProject timesheetProject = timesheetProjectMapper.convertDtoToModel(timesheetProjectDTO);
        return timesheetProjectMapper.convertModelToDTO(timesheetProjectService.create(timesheetProject));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public TimesheetProjectDTO update(@PathVariable Long id, @Valid @RequestBody TimesheetProjectDTO timesheetProjectDTO) {
        timesheetProjectDTO.setId(id);
        TimesheetProject timesheetProject = timesheetProjectMapper.convertDtoToModel(timesheetProjectDTO);
        return timesheetProjectMapper.convertModelToDTO(timesheetProjectService.update(timesheetProject));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public void delete(@PathVariable Long id) {
        timesheetProjectService.delete(id);
    }

}

