package com.teoresi.staff.mappers;

import com.teoresi.staff.dtos.ResourceSkillDTO;
import com.teoresi.staff.entities.ResourceSkill;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface ResourceSkillMapper {

    ResourceSkillDTO convertModelToDTO(ResourceSkill resourceSkill);

    List<ResourceSkillDTO> convertModelsToDtos(List<ResourceSkill> resourceSkills);

    default PageDTO<ResourceSkillDTO> convertModelsPageToDtosPage(Page<ResourceSkill> modelsPage) {
        return PageDTO.<ResourceSkillDTO>builder()
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
