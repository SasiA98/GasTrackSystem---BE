package com.client.staff.libs.web.mappers;

import com.client.staff.libs.web.dtos.PageDTO;
import java.util.List;
import org.springframework.data.domain.Page;

public interface TransferableObjectMapper<Model, RequestDTO, ResponseDTO> {
    Model mapRequestToModel(RequestDTO paramRequestDTO);

    ResponseDTO mapModelToResponse(Model paramModel);

    List<ResponseDTO> mapModelsToResponseList(List<Model> paramList);

    default PageDTO<ResponseDTO> mapModelsPageToResponsePage(Page<Model> modelsPage) {
        return (PageDTO<ResponseDTO>) PageDTO.builder()
                .content((List<Object>) mapModelsToResponseList(modelsPage.getContent()))
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
