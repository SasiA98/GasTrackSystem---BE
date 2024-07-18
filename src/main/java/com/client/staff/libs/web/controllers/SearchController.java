package com.client.staff.libs.web.controllers;

import com.client.staff.libs.web.dtos.PageDTO;
import com.client.staff.libs.web.mappers.TransferableObjectMapper;
import com.client.staff.libs.web.services.SearchService;
import org.springframework.data.domain.Pageable;
import javax.validation.Valid;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SearchController<Model, SearchCriteria, ResponseDTO> {
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    TransferableObjectMapper<Model, SearchCriteria, ResponseDTO> getSearchMapper();

    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    SearchService<Model, SearchCriteria> getSearchService();
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    @PostMapping({"/search"})
    default PageDTO<ResponseDTO> search(@Valid @RequestBody SearchCriteria searchCriteria, @PageableDefault Pageable pageable) {
        return getSearchMapper().mapModelsPageToResponsePage(getSearchService().search(searchCriteria, pageable));
    }
}
