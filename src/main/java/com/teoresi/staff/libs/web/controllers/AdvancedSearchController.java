package com.teoresi.staff.libs.web.controllers;


import com.teoresi.staff.libs.data.models.Filter;
import com.teoresi.staff.libs.web.dtos.PageDTO;
import com.teoresi.staff.libs.web.mappers.TransferableObjectMapper;
import com.teoresi.staff.libs.web.services.AdvancedSearchService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AdvancedSearchController<Model, ResponseDTO> {
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    TransferableObjectMapper<Model, ?, ResponseDTO> getAdvancedSearchMapper();
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    AdvancedSearchService<Model> getAdvancedSearchService();
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    @PostMapping({"/advanced-search"})
    default PageDTO<ResponseDTO> searchAdvanced(@RequestBody Filter<Model> filter, @PageableDefault Pageable pageable) {
        return getAdvancedSearchMapper()
                .mapModelsPageToResponsePage(getAdvancedSearchService().searchAdvanced(filter, pageable));
    }
}

