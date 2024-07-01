package com.teoresi.staff.mappers.old;

import com.teoresi.staff.dtos.old.ProjectDTO;
import com.teoresi.staff.entities.old.Project;
import com.teoresi.staff.entities.old.Unit;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import com.teoresi.staff.entities.old.Resource;

import org.mapstruct.*;
import org.springframework.data.domain.Page;


import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface ProjectMapper {

    default Project convertDtoToModel(ProjectDTO projectDTO) {
        if (projectDTO == null ) {
            return null;
        }

        Project project = new Project();

        project.setId( projectDTO.getId() );
        project.setName( projectDTO.getName() );
        project.setIndustry( projectDTO.getIndustry() );
        project.setPRESALE(Resource.builder().id(projectDTO.getPresaleId()).build());
        project.setDUM(Resource.builder().id(projectDTO.getDumId()).build());
        project.setUnit(Unit.builder().id(projectDTO.getUnitId()).build());
        project.setBmTrigram( projectDTO.getBmTrigram() );
        project.setStatus( projectDTO.getStatus() );
        project.setCrmCode( projectDTO.getCrmCode() );
        project.setProjectId( projectDTO.getProjectId());
        project.setIc( projectDTO.isIc() );
        project.setSpecial( projectDTO.isSpecial() );
        project.setStartDate( projectDTO.getStartDate() );
        project.setEstimatedEndDate( projectDTO.getEstimatedEndDate() );
        project.setPreSaleScheduledEndDate( projectDTO.getPreSaleScheduledEndDate() );
        project.setKomDate( projectDTO.getKomDate() );
        project.setEndDate( projectDTO.getEndDate() );
        project.setNote( projectDTO.getNote() );
        project.setProjectType(projectDTO.getProjectType());
        project.setCurrentFixedCost(projectDTO.getCurrentFixedCost());
        project.setPreSaleFixedCost(projectDTO.getPreSaleFixedCost());

        return project;
    }

    ProjectDTO convertModelToDTO(Project project);

    List<ProjectDTO> convertModelsToDtos(List<Project> projects);

    default PageDTO<ProjectDTO> convertModelsPageToDtosPage(Page<Project> modelsPage) {
        return PageDTO.<ProjectDTO>builder()
                .content(convertModelsToDtos(modelsPage.getContent()))
                .first(modelsPage.isFirst())
                .last(modelsPage.isLast())
                .number(modelsPage.getNumber())
                .numberOfElements(modelsPage.getNumberOfElements())
                .size(modelsPage.getSize())
                .sort(modelsPage.getSort().toList())
                .totalElements(modelsPage.getTotalElements())
                .totalPages(modelsPage.getTotalPages())
                .build();
    }
}
