package com.client.staff.mappers;

import com.client.staff.entities.User;
import com.client.staff.profile.dtos.PatchProfileDTO;
import com.client.staff.dtos.UserDTO;
import com.client.staff.libs.web.dtos.PageDTO;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface UserMapper {

    UserDTO convertModelToDTO(User user);

    User convertDtoToModel(UserDTO userDTO);

    List<UserDTO> convertModelsToDtos(List<User> users);

    User convertProfileDTOtoUser(PatchProfileDTO profileDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModel(User source, @MappingTarget User target);

    default PageDTO<UserDTO> convertModelsPageToDtosPage(Page<User> modelsPage) {
        return PageDTO.<UserDTO>builder()
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
