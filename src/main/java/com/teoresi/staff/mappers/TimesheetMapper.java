package com.teoresi.staff.mappers;

import com.teoresi.staff.dtos.TimesheetDTO;
import com.teoresi.staff.entities.Timesheet;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface TimesheetMapper {

    TimesheetDTO convertModelToDTO(Timesheet timesheet);

    List<TimesheetDTO> convertModelsToDtos(List<Timesheet> timesheets);

}
