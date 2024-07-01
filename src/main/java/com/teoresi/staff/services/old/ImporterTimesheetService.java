package com.teoresi.staff.services.old;

import com.teoresi.staff.dtos.old.ImportTimesheetDTO;
import com.teoresi.staff.entities.old.Resource;
import com.teoresi.staff.entities.old.Timesheet;
import com.teoresi.staff.entities.old.TimesheetProject;
import com.teoresi.staff.libs.data.models.TimesheetExcel;
import com.teoresi.staff.libs.utils.TimesheetExcelHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Sinks;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImporterTimesheetService {

    private final ResourceService resourceService;
    private final ProjectService projectService;
    private final TimesheetService timesheetService;
    private final TimesheetProjectService timesheetProjectService;
    private final TimesheetExcelHelper timesheetExcelHelper;
    private Sinks.Many<ImportTimesheetDTO> importTimesheetSink;
    private static final String PROJECT_ID_NOT_FOUND = "Project %s (ID : %s) not found.";
    private static final String EMPLOYEE_ID_NOT_FOUND = "%s (Employee ID = %s) not found.";
    private static final String ALLOCATION_NOT_FOUND = "%s (Employee ID = %s) is not allocated to %s (ID %s) as of %s.";

    private final Logger logger = LoggerFactory.getLogger(ImporterTimesheetService.class);

    @Async
    public void importTimesheetProject(MultipartFile file, Sinks.Many<ImportTimesheetDTO> importTimesheetSink, Path tempFile) {

        this.importTimesheetSink = importTimesheetSink;

        if (timesheetExcelHelper.hasExcelFormat(file)) {

            try {

                ImportTimesheetDTO importTimesheetDTO = new ImportTimesheetDTO();
                List<TimesheetExcel> timesheetExcels = timesheetExcelHelper.excelToTimesheets(file.getInputStream(), importTimesheetDTO);

                List<TimesheetExcel> filtered = timesheetExcels.stream()
                        .filter(timesheetExcel -> timesheetExcel.getResourceEmployeeId() != null
                                && timesheetExcel.getProjectId() != null
                                && timesheetExcel.getMonth() != null)
                        .collect(Collectors.toList());

                // Raggruppa per risorsa e progetto
                Map<Integer, Map<String, List<TimesheetExcel>>> group = filtered.stream()
                        .collect(Collectors.groupingBy(TimesheetExcel::getResourceEmployeeId,
                                Collectors.groupingBy(TimesheetExcel::getProjectId, HashMap::new,
                                        Collectors.toCollection(ArrayList::new))));

                importTimesheetDTO.setTotalResources(group.size());
                this.importTimesheetSink.tryEmitNext(importTimesheetDTO);

                importTimesheetDTO.setProcessedRows(0);
                importTimesheetDTO.setProcessedResources(0);

                group.forEach((employeeId, projects) -> {
                    Optional<Resource> resourceOpt = resourceService.getByEmployeeId(employeeId);

                    if (resourceOpt.isPresent()) {

                        Resource resource = resourceOpt.get();
                        projects.forEach((projectId, projectList) -> {
                            boolean projectExist = projectService.existsByProjectId(projectId);

                            if (projectExist) {

                                Map<YearMonth, Integer> hoursByMonth = new HashMap<>();

                                for (TimesheetExcel timesheetExcel : projectList) {
                                    if (timesheetExcel.getHours() != null) {
                                        YearMonth yearMonth = YearMonth.of(timesheetExcel.getMonth().getYear(), timesheetExcel.getMonth().getMonth());
                                        hoursByMonth.merge(yearMonth, timesheetExcel.getHours(), Integer::sum);
                                    }
                                }

                                for (Map.Entry<YearMonth, Integer> entry : hoursByMonth.entrySet()) {
                                    YearMonth month = entry.getKey();
                                    int totalHours = entry.getValue();

                                    Timesheet timesheet = timesheetService.getTimesheetByDateAndResourceId(resource.getId(),
                                            month.getMonthValue(), month.getYear());

                                    if(timesheet==null) {
                                        ImportTimesheetDTO errorMessage = new ImportTimesheetDTO();
                                        Optional<String> resourceFullName = timesheetExcels.stream()
                                                .filter(timesheetExcel -> timesheetExcel.getResourceEmployeeId().equals(employeeId))
                                                .map(TimesheetExcel::getResourceName)
                                                .findFirst();

                                        Optional<String> projectName = timesheetExcels.stream()
                                                .filter(timesheetExcel -> timesheetExcel.getProjectId().equals(projectId))
                                                .map(TimesheetExcel::getProjectName)
                                                .findFirst();

                                        String date = month.getMonthValue() + "/" + month.getYear();

                                        String error = String.format(ALLOCATION_NOT_FOUND, resourceFullName.get(), employeeId, projectName.get(), projectId, date);
                                        errorMessage.setMessage(error);
                                        this.importTimesheetSink.tryEmitNext(errorMessage);

                                        continue;
                                    }

                                    for (TimesheetProject relatedProject : timesheet.getRelatedProjects()) {

                                        if (projectId.equalsIgnoreCase(relatedProject.getProject().getProjectId())) {

                                            TimesheetProject timesheetProject = timesheetProjectService.getById(relatedProject.getId());

                                            if(timesheetProject.getHours() != totalHours) {
                                                timesheetProject.setHours(totalHours);
                                                timesheetProject.setVerifiedHours(true);
                                            } else {
                                                timesheetProject.setVerifiedHours(false);
                                            }
                                            timesheetProjectService.update(timesheetProject);
                                        }
                                    }
                                }

                            } else {
                                ImportTimesheetDTO errorMessage = new ImportTimesheetDTO();
                                Optional<String> projectName = timesheetExcels.stream()
                                        .filter(timesheetExcel -> timesheetExcel.getProjectId().equals(projectId))
                                        .map(TimesheetExcel::getProjectName)
                                        .findFirst();

                                String error = String.format(PROJECT_ID_NOT_FOUND, projectName.get(), projectId);
                                errorMessage.setMessage(error);
                                this.importTimesheetSink.tryEmitNext(errorMessage);
                            }
                        });
                    } else {
                        ImportTimesheetDTO errorMessage = new ImportTimesheetDTO();
                        Optional<String> resourceFullName = timesheetExcels.stream()
                                .filter(timesheetExcel -> timesheetExcel.getResourceEmployeeId().equals(employeeId))
                                .map(TimesheetExcel::getResourceName)
                                .findFirst();

                        String error = String.format(EMPLOYEE_ID_NOT_FOUND, resourceFullName.get(), employeeId);
                        errorMessage.setMessage(error);
                        this.importTimesheetSink.tryEmitNext(errorMessage);
                    }

                    importTimesheetDTO.setProcessedResources(importTimesheetDTO.getProcessedResources() + 1);
                    importTimesheetDTO.setProgress((importTimesheetDTO.getProcessedResources()*100)/importTimesheetDTO.getTotalResources());
                    this.importTimesheetSink.tryEmitNext(importTimesheetDTO);
                });

                importTimesheetDTO.setProgress(100);
                this.importTimesheetSink.tryEmitNext(importTimesheetDTO);

                this.importTimesheetSink.tryEmitComplete();
                Files.delete(tempFile);

            } catch (Exception e) {
                this.importTimesheetSink.tryEmitError(e);
                logger.error(e.getMessage());
            }
        }
    }

}