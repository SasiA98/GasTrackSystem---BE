package com.teoresi.staff.controllers;

import com.teoresi.staff.dtos.OperationManagerDTO;
import com.teoresi.staff.entities.OperationManager;
import com.teoresi.staff.libs.data.models.Filter;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import com.teoresi.staff.mappers.OperationManagerMapper;
import com.teoresi.staff.services.OperationManagerService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("operation-manager")
public class OperationManagerController {

    private final OperationManagerMapper operationManagerMapper;
    private final OperationManagerService operationManagerService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'STAFF')")
    public OperationManagerDTO create(@Valid @RequestBody OperationManagerDTO operationManagerDTO) throws IOException, ParseException, InterruptedException {
        OperationManager operationManager = operationManagerMapper.convertDTOtoModel(operationManagerDTO);
        return operationManagerMapper.convertModelToDTO(operationManagerService.create(operationManager));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'STAFF')")
    public OperationManagerDTO update(@PathVariable Long id, @Valid @RequestBody OperationManagerDTO operationManagerDTO) {
        operationManagerDTO.setId(id);
        OperationManager operationManager = operationManagerMapper.convertDTOtoModel(operationManagerDTO);
        return operationManagerMapper.convertModelToDTO(operationManagerService.update(operationManager));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'STAFF')")
    public void deleteById(@PathVariable Long id) {
        operationManagerService.deleteById(id);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL', 'PM', 'DTL', 'STAFF')")
    public OperationManagerDTO getById(@PathVariable Long id) {
        return operationManagerMapper.convertModelToDTO(operationManagerService.getById(id));
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL', 'PM', 'DTL', 'STAFF')")
    public List<OperationManagerDTO> getAll() {
        return operationManagerMapper.convertModelsToDtos(operationManagerService.getAll());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'GDM', 'DUM', 'PSE', 'PSM', 'PSL', 'PM', 'DTL', 'STAFF')")
    @PostMapping("/advanced-search")
    public PageDTO<OperationManagerDTO> searchAdvanced(
            @RequestBody(required = false) Optional<Filter<OperationManager>> filter,
            @PageableDefault Pageable pageable) {

        Page<OperationManager> resultPage = operationManagerService.searchAdvanced(filter, pageable);
        return operationManagerMapper.convertModelsPageToDtosPage(resultPage);
    }

}
