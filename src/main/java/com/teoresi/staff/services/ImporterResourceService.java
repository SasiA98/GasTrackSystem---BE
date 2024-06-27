package com.teoresi.staff.services;

import com.teoresi.staff.dtos.ImportResourceDTO;
import com.teoresi.staff.entities.Resource;
import com.teoresi.staff.libs.data.models.ResourceExcel;
import com.teoresi.staff.libs.utils.ResourceExcelHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImporterResourceService {


    private final ResourceExcelHelper resourceExcelHelper;
    private final ResourceService resourceService;
    private final UnitService unitService;

    private static final String RESOURCE_ALREADY_EXISTS = "already exists.";
    private static final String PREFIX_RESOURCE_ERROR = "%s (Employee ID = %s) ";
    private static final String EMAIL_DOMAIN = "@teoresigroup.com";

    private final Logger logger = LoggerFactory.getLogger(ImporterResourceService.class);
    private Sinks.Many<ImportResourceDTO> importResourceSink;

    @Async
    public void importResources(MultipartFile file, Sinks.Many<ImportResourceDTO>importResourceSink, Path tempFile) {

        this.importResourceSink = importResourceSink;

        if (!resourceExcelHelper.hasExcelFormat(file)) {
            return;
        }

        ImportResourceDTO importResourceDTO = new ImportResourceDTO();

        try {
            List<ResourceExcel> resourceExcels = resourceExcelHelper.excelToResources(file.getInputStream(), importResourceDTO);
            Set<ResourceExcel> validResourceExcels = filterValidResources(resourceExcels);

            initializeImportResourceDTO(importResourceDTO, resourceExcels.size(), validResourceExcels.size());
            this.importResourceSink.tryEmitNext(importResourceDTO);

            processResource(validResourceExcels, importResourceDTO);

            importResourceDTO.setProgress(100);
            this.importResourceSink.tryEmitComplete();
            try {
                Files.delete(tempFile);
            } catch (IOException e) {
                handleImportError(e);
            }

        } catch (Exception e) {
            handleImportError(e);
        }
    }


    private Set<ResourceExcel> filterValidResources(List<ResourceExcel> resourceExcels) {
            return resourceExcels.stream()
                    .filter(r -> r.getEmployeeId() != null)
                    .collect(Collectors.toSet());
        }

    private void initializeImportResourceDTO(ImportResourceDTO importResourceDTO, int totalRows, int totalResources) {
        importResourceDTO.setProcessedRows(0);
        importResourceDTO.setProcessedResources(0);
        importResourceDTO.setTotalRows(totalRows);
        importResourceDTO.setTotalResources(totalResources);
    }

    private void processResource(Set<ResourceExcel> validResources, ImportResourceDTO importResourceDTO) {

        for(ResourceExcel resourceExcel : validResources) {
            if (resourceService.existByEmployeeId(resourceExcel.getEmployeeId())) {
                String prefixError = String.format(PREFIX_RESOURCE_ERROR, resourceExcel.getFullName(), resourceExcel.getEmployeeId());
                String error = prefixError + RESOURCE_ALREADY_EXISTS;
                emitInvalidResource(error);

            } else {
                createAndEmitResource(resourceExcel);
            }
            updateImportResourceDTO(importResourceDTO);
        }
    }

    private void updateImportResourceDTO(ImportResourceDTO importResourceDTO){
        importResourceDTO.setProcessedResources(importResourceDTO.getProcessedResources() + 1);
        importResourceDTO.setProgress((importResourceDTO.getProcessedResources() * 100) /importResourceDTO.getTotalResources());
        this.importResourceSink.tryEmitNext(importResourceDTO);
    }


    private void emitInvalidResource(String msg) {
        ImportResourceDTO errorMessage = new ImportResourceDTO();
        errorMessage.setMessage(msg);
        this.importResourceSink.tryEmitNext(errorMessage);
    }

    private void createAndEmitResource(ResourceExcel resourceExcel) {

        try {
            Resource resource = mapExcelIntoEntity(resourceExcel);
            resourceService.create(resource, true);

        } catch (DataIntegrityViolationException exception){
            String prefixError = String.format(PREFIX_RESOURCE_ERROR, resourceExcel.getFullName(), resourceExcel.getEmployeeId());
            String error = prefixError + exception.getMessage();
            emitInvalidResource(error);
        } catch (IllegalArgumentException exception){
            emitInvalidResource(exception.getMessage());
        }
    }

    private Resource mapExcelIntoEntity(ResourceExcel resourceExcel) {
        return Resource.builder()
                .employeeId(resourceExcel.getEmployeeId())
                .unit(unitService.getByTrigram(resourceExcel.getUnitTrigram(), true))
                .name(resourceExcel.getName())
                .surname(resourceExcel.getSurname())
                .email(getResourceEmail(resourceExcel))
                .birthDate(resourceExcel.getBirthDate())
                .hiringDate(resourceExcel.getHiringDate())
                .lastWorkingTime(40)
                .lastWorkingTimeStartDate(resourceExcel.getHiringDate())
                .leaveDate(resourceExcel.getLeaveDate())
                .site(resourceExcel.getSite())
                .roles(resourceExcel.getRoles())
                .location(resourceExcel.getLocation())
                .lastHourlyCost(resourceExcel.getHourlyCost())
                .lastHourlyCostStartDate(resourceExcel.getHourlyCostStartDate())
                .build();
    }

    private String getResourceEmail(ResourceExcel resourceExcel){
        String emailName;
        String name = resourceExcel.getName();
        String surname = resourceExcel.getSurname();

        if(name != null && surname != null)
            emailName = name + "." + surname;
        else if(name != null)
            emailName = name;
        else if(surname!= null)
            emailName = surname;
        else
            return null;

        emailName = emailName.replaceAll("[^a-zA-Z0-9]", "");

        if(resourceService.existsHomonyms(resourceExcel.getName(), resourceExcel.getSurname()))
            emailName = emailName + resourceExcel.getEmployeeId();

        return (emailName + EMAIL_DOMAIN).toLowerCase();
    }

    private void handleImportError(Exception e) {
        this.importResourceSink.tryEmitError(e);
        logger.error(e.getMessage());
    }

}