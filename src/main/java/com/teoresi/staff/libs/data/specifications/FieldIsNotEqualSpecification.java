package com.teoresi.staff.libs.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class FieldIsNotEqualSpecification<E, A> implements GenericSpecification<E, A> {
    protected final A value;

    public FieldIsNotEqualSpecification(A value) {
        this.value = value;
    }

    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.notEqual(this.getFieldPath(root), this.value);
    }
}
