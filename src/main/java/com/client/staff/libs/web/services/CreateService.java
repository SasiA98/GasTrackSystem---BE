package com.client.staff.libs.web.services;

import com.client.staff.libs.data.models.IdentifiableEntity;

import java.io.IOException;
import java.text.ParseException;

public interface CreateService<Model extends IdentifiableEntity<Id>, Id> extends RepositoryService<Model, Id> {
    default Model create(Model model) throws InterruptedException, IOException, ParseException {
        model.setId(null);
        return (Model)getRepository().save(model);
    }
}
