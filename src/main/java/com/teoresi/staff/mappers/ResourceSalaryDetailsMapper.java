package com.teoresi.staff.mappers;

import com.teoresi.staff.dtos.CustomsDTO.ResourcesSalaryDetailDTO;
import com.teoresi.staff.dtos.ResourceHourlyCostDTO;
import com.teoresi.staff.entities.ResourceSalaryDetails;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

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
