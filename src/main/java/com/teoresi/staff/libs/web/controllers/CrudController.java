package com.teoresi.staff.libs.web.controllers;


import com.teoresi.staff.libs.data.models.IdentifiableEntity;
import com.teoresi.staff.libs.web.services.AdvancedSearchService;
import com.teoresi.staff.libs.web.services.CreateService;
import com.teoresi.staff.libs.web.services.CrudService;
import com.teoresi.staff.libs.web.services.DeleteService;
import com.teoresi.staff.libs.web.services.ReadService;
import com.teoresi.staff.libs.web.services.SearchService;
import com.teoresi.staff.libs.web.services.UpdateService;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CrudController<Model extends IdentifiableEntity<Id>, Id, CreateRequestDTO, CreateResponseDTO, ReadResponseDTO, SearchRequestDTO, SearchResponseDTO> extends CreateController<Model, Id, CreateRequestDTO, CreateResponseDTO>, ReadController<Model, Id, ReadResponseDTO>, UpdateController<Model, Id, CreateRequestDTO, CreateResponseDTO>, DeleteController<Model, Id>, SearchController<Model, SearchRequestDTO, SearchResponseDTO>, AdvancedSearchController<Model, SearchResponseDTO> {
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    CrudService<Model, Id, SearchRequestDTO> getCrudService();
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    default CreateService<Model, Id> getCreateService() {
        return (CreateService)getCrudService();
    }
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    default DeleteService<Model, Id> getDeleteService() {
        return (DeleteService)getCrudService();
    }
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    default ReadService<Model, Id> getReadService() {
        return (ReadService)getCrudService();
    }
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    default UpdateService<Model, Id> getUpdateService() {
        return (UpdateService)getCrudService();
    }
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    default SearchService<Model, SearchRequestDTO> getSearchService() {
        return (SearchService)getCrudService();
    }
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    default AdvancedSearchService<Model> getAdvancedSearchService() {
        return (AdvancedSearchService)getCrudService();
    }
}
