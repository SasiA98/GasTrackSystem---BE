package com.teoresi.staff.mappers;

import com.teoresi.staff.libs.web.dtos.PageDTO;

import com.teoresi.staff.dtos.UnitDTO;
import com.teoresi.staff.entities.Unit;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface UnitMapper {

    Unit convertDtoToModel(UnitDTO unitDTO);

    UnitDTO convertModelToDTO(Unit unit);

    List<UnitDTO> convertModelsToDtos(List<Unit> resources);

    default PageDTO<UnitDTO> convertModelsPageToDtosPage(Page<Unit> modelsPage) {
        return PageDTO.<UnitDTO>builder()
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
