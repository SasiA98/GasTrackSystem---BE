package com.teoresi.staff.mappers;

import com.teoresi.staff.dtos.CompanyLicenceDTO;
import com.teoresi.staff.entities.CompanyLicence;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface CompanyLicenceMapper {

    CompanyLicence convertDtoToModel(CompanyLicenceDTO companyLicenceDTO);

    CompanyLicenceDTO convertModelToDTO(CompanyLicence companyLicence);

    List<CompanyLicenceDTO> convertModelsToDtos(List<CompanyLicence> companyLicences);

    default PageDTO<CompanyLicenceDTO> convertModelsPageToDtosPage(Page<CompanyLicence> modelsPage) {
        return PageDTO.<CompanyLicenceDTO>builder()
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
