package com.teoresi.staff.controllers.old;

import com.teoresi.staff.dtos.old.UserDTO;
import com.teoresi.staff.entities.old.User;
import com.teoresi.staff.mappers.old.UserMapper;
import com.teoresi.staff.services.old.UserService;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import com.teoresi.staff.libs.data.models.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public UserDTO create(@Valid @RequestBody UserDTO userDTO) {
        User user = userMapper.convertDtoToModel(userDTO);
        return userMapper.convertModelToDTO(userService.create(user));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    @PutMapping("{id}/reset-password")
    public UserDTO resetPassword(@PathVariable Long id) {
        return userMapper.convertModelToDTO(userService.resetPassword(id));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    @PostMapping("/advanced-search")
    public PageDTO<UserDTO> searchAdvanced(
            @RequestBody(required = false) Optional<Filter<User>> filter,
            @PageableDefault Pageable pageable
    ) {
        Page<User> resultPage = userService.searchAdvanced(filter, pageable);
        return userMapper.convertModelsPageToDtosPage(resultPage);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public UserDTO update(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        userDTO.setId(id);
        User user = userMapper.convertDtoToModel(userDTO);
        return userMapper.convertModelToDTO(userService.update(user));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public UserDTO partialUpdate(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return userMapper.convertModelToDTO(userService.partialUpdate(user));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public UserDTO getById(@PathVariable Long id, Authentication authentication) {
        return userMapper.convertModelToDTO(userService.getById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public void deleteById(@PathVariable Long id) {
        userService.deleteById(id);
    }


    @GetMapping("/checkUserInfo/{id}")
    public UserDTO checkUserInfo(@PathVariable Long id) {
        return userMapper.convertModelToDTO(userService.getById(id));
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM')")
    public List<UserDTO> getAll() {
        return userMapper.convertModelsToDtos(userService.getAll());
    }

}
