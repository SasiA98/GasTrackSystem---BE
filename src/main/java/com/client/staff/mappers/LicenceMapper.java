package com.client.staff.mappers;

import com.client.staff.dtos.LicenceDTO;
import com.client.staff.entities.Licence;
import com.client.staff.libs.web.dtos.PageDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface LicenceMapper {

    Licence convertDtoToModel(LicenceDTO licenceDTO);

    LicenceDTO convertModelToDTO(Licence licence);

    List<LicenceDTO> convertModelsToDtos(List<Licence> companies);

    default PageDTO<LicenceDTO> convertModelsPageToDtosPage(Page<Licence> modelsPage) {
        return PageDTO.<LicenceDTO>builder()
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
