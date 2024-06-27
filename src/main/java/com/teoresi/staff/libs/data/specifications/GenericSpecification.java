package com.teoresi.staff.libs.data.specifications;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public interface GenericSpecification<E, A> extends Specification<E> {
    Path<A> getFieldPath(Root<E> paramRoot);
}
