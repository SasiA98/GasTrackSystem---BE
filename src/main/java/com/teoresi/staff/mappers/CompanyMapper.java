package com.teoresi.staff.mappers;

import com.teoresi.staff.dtos.CompanyDTO;
import com.teoresi.staff.dtos.CompanyLicenceDTO;
import com.teoresi.staff.entities.Company;
import com.teoresi.staff.entities.CompanyLicence;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface CompanyMapper {

    Company convertDtoToModel(CompanyDTO companyDTO);

    CompanyDTO convertModelToDTO(Company company);

    List<CompanyDTO> convertModelsToDtos(List<Company> companies);

    default PageDTO<CompanyDTO> convertModelsPageToDtosPage(Page<Company> modelsPage) {
        return PageDTO.<CompanyDTO>builder()
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
