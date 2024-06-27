package com.teoresi.staff.libs.web.controllers;

import com.teoresi.staff.libs.data.models.IdentifiableEntity;
import com.teoresi.staff.libs.web.services.DeleteService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
public interface DeleteController<Model extends IdentifiableEntity<Id>, Id> {
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    DeleteService<Model, Id> getDeleteService();
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canWrite(#root.this.moduleName, #root.this.sessionService.getRole())")
    @DeleteMapping({"/{id}"})
    default void deleteById(@PathVariable Id id) {
        if (!getDeleteService().deleteById(id)) {
            String message = String.format("Resource with id %s not found.", id.toString());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }
    }
}
