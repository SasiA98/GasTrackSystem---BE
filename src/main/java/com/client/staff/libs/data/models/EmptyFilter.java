package com.client.staff.libs.data.models;

import com.client.staff.libs.data.components.SpecificationFactory;
import org.springframework.data.jpa.domain.Specification;

public class EmptyFilter<T> implements Filter<T> {
    public Specification<T> toSpecification(SpecificationFactory<T> specificationFactory) {
        return Specification.where((Specification)null);
    }

    @Override
    public boolean containsField(String field) {
        return false;
    }
}