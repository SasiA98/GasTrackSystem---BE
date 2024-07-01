package com.teoresi.staff.mappers.old;

import com.teoresi.staff.dtos.old.SkillDTO;
import com.teoresi.staff.entities.old.Skill;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface SkillMapper {

    Skill convertDtoToModel(SkillDTO skillDTO);

    SkillDTO convertModelToDTO(Skill skill);

    List<SkillDTO> convertModelsToDtos(List<Skill> skills);

    default PageDTO<SkillDTO> convertModelsPageToDtosPage(Page<Skill> modelsPage) {
        return PageDTO.<SkillDTO>builder()
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
