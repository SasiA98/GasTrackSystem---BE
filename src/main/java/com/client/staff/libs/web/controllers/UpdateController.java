package com.client.staff.libs.web.controllers;

import com.client.staff.libs.data.models.IdentifiableEntity;
import com.client.staff.libs.web.mappers.TransferableObjectMapper;
import com.client.staff.libs.web.services.UpdateService;
import java.util.Optional;
import javax.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

public interface UpdateController<Model extends IdentifiableEntity<Id>, Id, RequestDTO, ResponseDTO> {
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    UpdateService<Model, Id> getUpdateService();
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    TransferableObjectMapper<Model, RequestDTO, ResponseDTO> getUpdateMapper();
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canWrite(#root.this.moduleName, #root.this.sessionService.getRole())")
    @PutMapping({"/{id}"})
    default ResponseDTO update(@PathVariable Id id, @Valid @RequestBody RequestDTO requestDTO) {
        Optional<Model> updatedModel;
        Model identifiableEntity = getUpdateMapper().mapRequestToModel(requestDTO);
        identifiableEntity.setId(id);
        try {
            updatedModel = getUpdateService().update(identifiableEntity);
        } catch (DataIntegrityViolationException ex) {
            String message = String.format("One or more constraint violations occurred: %s", new Object[] { ex.getMessage() });
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
        if (updatedModel.isEmpty()) {
            String message = String.format("Resource with id %s not found.", new Object[] { identifiableEntity.getId().toString() });
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }
        return (ResponseDTO)getUpdateMapper().mapModelToResponse(updatedModel.get());
    }
}

