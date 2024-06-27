package com.teoresi.staff.libs.data.specifications;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class FieldIsNullSpecification<E, A> implements GenericSpecification<E, A> {
    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isNull(this.getFieldPath(root));
    }
}
