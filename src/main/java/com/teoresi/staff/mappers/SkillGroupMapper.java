package com.teoresi.staff.mappers;

import com.teoresi.staff.dtos.SkillGroupDTO;
import com.teoresi.staff.entities.SkillGroup;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface SkillGroupMapper {

    SkillGroup convertDtoToModel(SkillGroupDTO skillGroupDTO);

    SkillGroupDTO convertModelToDTO(SkillGroup skillGroup);

    List<SkillGroupDTO> convertModelsToDtos(List<SkillGroup> skills);

    default PageDTO<SkillGroupDTO> convertModelsPageToDtosPage(Page<SkillGroup> modelsPage) {
        return PageDTO.<SkillGroupDTO>builder()
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
