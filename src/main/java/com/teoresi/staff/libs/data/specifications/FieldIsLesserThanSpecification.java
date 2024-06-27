package com.teoresi.staff.libs.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class FieldIsLesserThanSpecification<E, A extends Comparable<A>> implements GenericSpecification<E, A> {
    protected final A value;

    protected final boolean isInclusive;

    public FieldIsLesserThanSpecification(A value, boolean isInclusive) {
        this.value = value;
        this.isInclusive = isInclusive;
    }

    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return this.isInclusive ? criteriaBuilder.lessThanOrEqualTo(this.getFieldPath(root), this.value) : criteriaBuilder.lessThan(this.getFieldPath(root), this.value);
    }
}
