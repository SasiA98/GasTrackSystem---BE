package com.teoresi.staff.libs.data.specifications;

import java.util.Collection;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class FieldHasAtLeastNumberOfMembers<T, M> implements GenericSpecification<T, Collection<M>> {
    protected final Integer value;

    public FieldHasAtLeastNumberOfMembers(Integer value) {
        this.value = value;
    }

    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.size(this.getFieldPath(root)), this.value);
    }
}
