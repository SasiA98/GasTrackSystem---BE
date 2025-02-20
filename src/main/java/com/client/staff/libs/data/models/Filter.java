package com.client.staff.libs.data.models;

import com.client.staff.libs.data.components.SpecificationFactory;
import org.springframework.data.jpa.domain.Specification;

public interface Filter<T> {
    Specification<T> toSpecification(SpecificationFactory<T> paramSpecificationFactory);

    boolean containsField(String field);
}