package com.teoresi.staff.mappers.old;

import com.teoresi.staff.dtos.old.OperationManagerDTO;
import com.teoresi.staff.entities.old.OperationManager;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface OperationManagerMapper {

    OperationManager convertDTOtoModel(OperationManagerDTO operationsTrigramDTO);

    OperationManagerDTO convertModelToDTO(OperationManager operationsTrigram);

    List<OperationManagerDTO> convertModelsToDtos(List<OperationManager> operationsTrigrams);

    default PageDTO<OperationManagerDTO> convertModelsPageToDtosPage(Page<OperationManager> modelsPage) {
        return PageDTO.<OperationManagerDTO>builder()
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
