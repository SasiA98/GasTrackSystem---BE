package com.teoresi.staff.libs.web.controllers;

import com.teoresi.staff.libs.data.models.IdentifiableEntity;
import com.teoresi.staff.libs.web.mappers.TransferableObjectMapper;
import com.teoresi.staff.libs.web.services.CreateService;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.text.ParseException;

public interface CreateController<Model extends IdentifiableEntity<Id>, Id, RequestDTO, ResponseDTO> {
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    TransferableObjectMapper<Model, RequestDTO, ResponseDTO> getCreateMapper();
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    CreateService<Model, Id> getCreateService();
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canWrite(#root.this.moduleName, #root.this.sessionService.getRole())")
    @PostMapping
    default ResponseDTO create(@RequestBody @Valid RequestDTO requestDTO) throws IOException, ParseException, InterruptedException {
        Model model = this.getCreateService().create(this.getCreateMapper().mapRequestToModel(requestDTO));
        return this.getCreateMapper().mapModelToResponse(model);
    }
}