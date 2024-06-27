package com.teoresi.staff.libs.web.services;

import com.teoresi.staff.libs.data.components.SpecificationFactory;
import com.teoresi.staff.libs.data.models.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdvancedSearchService<Model> {
    JpaSpecificationExecutor<Model> getRepository();

    SpecificationFactory<Model> getSpecificationFactory();

    default Page<Model> searchAdvanced(Filter<Model> filter, Pageable pageable) {
        return getRepository().findAll(filter.toSpecification(getSpecificationFactory()), pageable);
    }
}

