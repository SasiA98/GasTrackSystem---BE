package com.teoresi.staff.mappers.old;

import com.teoresi.staff.dtos.old.CustomsDTO.ResourcesSalaryDetailDTO;
import com.teoresi.staff.entities.old.ResourceSalaryDetails;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface ResourceSalaryDetailsMapper {

    ResourcesSalaryDetailDTO convertModelToDTO(ResourceSalaryDetails entity);

    List<ResourcesSalaryDetailDTO> convertModelsToDtos(List<ResourceSalaryDetails> entity);

//    default PageDTO<ResourcesSalaryDetailDTO> convertModelsPageToDtosPage(Page<ResourceSalaryDetails> modelsPage) {
//        return PageDTO.<ResourceHourlyCostDTO>builder()
//                .content(convertModelsToDtos(modelsPage.getContent()))
//                .first(modelsPage.isFirst())
//                .last(modelsPage.isLast())
//                .number(modelsPage.getNumber())
//                .numberOfElements(modelsPage.getNumberOfElements())
//                .size(modelsPage.getSize())
//                .sort(modelsPage.getSort().toList())
//                .totalElements(modelsPage.getTotalElements())
//                .totalPages(modelsPage.getTotalPages())
//                .build();
//    }
}
