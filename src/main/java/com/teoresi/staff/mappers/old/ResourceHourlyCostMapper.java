package com.teoresi.staff.mappers.old;

import com.teoresi.staff.dtos.old.ResourceHourlyCostDTO;
import com.teoresi.staff.entities.old.ResourceHourlyCost;
import com.teoresi.staff.libs.web.dtos.PageDTO;

import org.mapstruct.*;
import org.springframework.data.domain.Page;


import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface ResourceHourlyCostMapper {

    ResourceHourlyCostDTO convertModelToDTO(ResourceHourlyCost hourlyCost);

    List<ResourceHourlyCostDTO> convertModelsToDtos(List<ResourceHourlyCost> hourlyCosts);

    default PageDTO<ResourceHourlyCostDTO> convertModelsPageToDtosPage(Page<ResourceHourlyCost> modelsPage) {
        return PageDTO.<ResourceHourlyCostDTO>builder()
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
