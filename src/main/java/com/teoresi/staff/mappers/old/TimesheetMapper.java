package com.teoresi.staff.mappers.old;

import com.teoresi.staff.dtos.old.TimesheetDTO;
import com.teoresi.staff.entities.old.Timesheet;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface TimesheetMapper {

    TimesheetDTO convertModelToDTO(Timesheet timesheet);

    List<TimesheetDTO> convertModelsToDtos(List<Timesheet> timesheets);

}
