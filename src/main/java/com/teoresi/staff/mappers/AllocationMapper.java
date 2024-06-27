package com.teoresi.staff.mappers;

import com.teoresi.staff.dtos.AllocationDTO;
import com.teoresi.staff.entities.Allocation;
import com.teoresi.staff.entities.Project;
import com.teoresi.staff.entities.Resource;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface AllocationMapper {

    default Allocation convertDtoToModel(AllocationDTO allocationDTO){
        if (allocationDTO == null ) {
            return null;
        }

        return Allocation.builder()
                .id(allocationDTO.getId())
                .resource(Resource.builder().id(allocationDTO.getResourceId()).build())
                .project(Project.builder().id(allocationDTO.getProjectId()).build())
                .hours(allocationDTO.getHours())
                .startDate(allocationDTO.getStartDate())
                .endDate(allocationDTO.getEndDate())
                .role(allocationDTO.getRole())
                .isRealCommitment(allocationDTO.isRealCommitment())
                .commitmentPercentage(allocationDTO.getCommitmentPercentage())
                .build();
    }

    AllocationDTO convertModelToDTO(Allocation allocation);

    List<AllocationDTO> convertModelsToDtos(List<Allocation> allocations);

    default PageDTO<AllocationDTO> convertModelsPageToDtosPage(Page<Allocation> modelsPage) {
        return PageDTO.<AllocationDTO>builder()
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
