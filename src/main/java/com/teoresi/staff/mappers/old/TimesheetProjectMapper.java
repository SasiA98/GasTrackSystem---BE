package com.teoresi.staff.mappers.old;

import com.teoresi.staff.dtos.old.TimesheetProjectDTO;
import com.teoresi.staff.entities.old.Allocation;
import com.teoresi.staff.entities.old.Project;
import com.teoresi.staff.entities.old.Timesheet;
import com.teoresi.staff.entities.old.TimesheetProject;
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
