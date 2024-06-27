package com.teoresi.staff.libs.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class FieldStartWithSubstringSpecification<E> implements GenericSpecification<E, String> {
    protected final String value;

    public FieldStartWithSubstringSpecification(String value) {
        this.value = value;
    }

    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.like(criteriaBuilder.lower(this.getFieldPath(root)), (this.value + "%").toLowerCase());
    }
}
