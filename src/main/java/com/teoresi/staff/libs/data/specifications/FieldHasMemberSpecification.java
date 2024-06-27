package com.teoresi.staff.libs.data.specifications;


import java.util.Collection;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class FieldHasMemberSpecification<E, M> implements GenericSpecification<E, Collection<M>> {
    protected final M member;

    public FieldHasMemberSpecification(M member) {
        this.member = member;
    }

    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isMember(this.member, this.getFieldPath(root));
    }
}