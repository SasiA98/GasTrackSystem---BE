package com.teoresi.staff.mappers;

import com.teoresi.staff.dtos.TimesheetProjectDTO;
import com.teoresi.staff.entities.Allocation;
import com.teoresi.staff.entities.Project;
import com.teoresi.staff.entities.Timesheet;
import com.teoresi.staff.entities.TimesheetProject;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface TimesheetProjectMapper {


    TimesheetProjectDTO convertModelToDTO(TimesheetProject timesheetProject);

    default TimesheetProject convertDtoToModel(TimesheetProjectDTO timesheetProjectDTO){
        if ( timesheetProjectDTO == null ) {
            return null;
        }

        TimesheetProject timesheetProject = new TimesheetProject();

        timesheetProject.setId( timesheetProjectDTO.getId() );
        if(timesheetProjectDTO.getAllocationId() != null)
            timesheetProject.setAllocation( Allocation.builder().id(timesheetProjectDTO.getAllocationId()).build());
        timesheetProject.setProject( Project.builder().id(timesheetProjectDTO.getProjectId()).build());
        timesheetProject.setTimesheet( Timesheet.builder().id(timesheetProjectDTO.getTimesheetId()).build());

        timesheetProject.setHours( timesheetProjectDTO.getHours() );
        timesheetProject.setPreImportHours(timesheetProjectDTO.getPreImportHours());
        if ( timesheetProjectDTO.getCost() != null ) {
            timesheetProject.setCost( timesheetProjectDTO.getCost() );
        }
        timesheetProject.setNote( timesheetProjectDTO.getNote() );

        return timesheetProject;
    }

    List<TimesheetProjectDTO> convertModelsToDtos(List<TimesheetProject> timesheetProjects);

}
