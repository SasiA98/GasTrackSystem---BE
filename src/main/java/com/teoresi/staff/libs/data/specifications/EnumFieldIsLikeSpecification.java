package com.teoresi.staff.libs.data.specifications;

import com.teoresi.staff.libs.utils.EnumUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class EnumFieldIsLikeSpecification<Entity, EnumType extends Enum<EnumType>, FieldType> implements GenericSpecification<Entity, FieldType> {
    private final Class<EnumType> enumClass;

    private final String value;

    public EnumFieldIsLikeSpecification(Class<EnumType> enumClass, String value) {
        this.enumClass = enumClass;
        this.value = value;
    }

    public Predicate toPredicate(Root<Entity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<FieldType> fieldPath = getFieldPath(root);
        return fieldPath.in(EnumUtils.getSimilarEnumValuesIgnoringCase(this.enumClass, this.value));
    }
}
