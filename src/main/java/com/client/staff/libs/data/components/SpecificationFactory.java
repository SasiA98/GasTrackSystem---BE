package com.client.staff.libs.data.components;

import java.util.Date;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.client.staff.libs.data.models.SimpleFilterOperator;
import com.client.staff.libs.data.specifications.*;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationFactory<T> {
    Specification<T> createSpecification(String paramString1, SimpleFilterOperator paramSimpleFilterOperator, String paramString2);

    default <S> Specification<T> buildFieldIsEqualSpecification(final String fieldName, S value) {
        return (Specification)new FieldIsEqualSpecification<T, S>(value) {
            public Path<S> getFieldPath(Root<T> root) {
                return root.get(fieldName);
            }
        };
    }

    default <S> Specification<T> buildFieldIsNotEqualSpecification(final String fieldName, S value) {
        return (Specification)new FieldIsNotEqualSpecification<T, S>(value) {
            public Path<S> getFieldPath(Root<T> root) {
                return root.get(fieldName);
            }
        };
    }

    default Specification<T> buildFieldIsLikeIgnoreCaseSpecification(final String fieldName, String value) {
        return (Specification<T>)new FieldIsLikeIgnoreCaseSpecification<T>(value) {
            public Path<String> getFieldPath(Root<T> root) {
                return root.get(fieldName);
            }
        };
    }


    default <S> Specification<T> buildSubfieldIsEqualSpecification(final String subFieldPath, S value) {
        return (Specification<T>) new FieldIsEqualSpecification<T, S>(value) {
            public Path<S> getFieldPath(Root<T> root) {
                return SpecificationFactory.this.getSubFieldPathFromRoot(root, subFieldPath);
            }
        };
    }


    default Specification<T> buildSubfieldIsLikeIgnoreCaseSpecification(final String subFieldPath, String value) {
        return (Specification<T>) new FieldIsLikeIgnoreCaseSpecification<T>(value) {
            public Path<String> getFieldPath(Root<T> root) {
                return SpecificationFactory.this.getSubFieldPathFromRoot(root, subFieldPath);
            }
        };
    }

    default Specification<T> buildSubFieldGreaterThanSpecification(final String subFieldPath, int value) {
        return new FieldIsGreaterThanSpecification<>(value, true) {
            public Path<Integer> getFieldPath(Root<T> root) {
                return SpecificationFactory.this.getSubFieldPathFromRoot(root, subFieldPath);
            }
        };
    }

    default Specification<T> buildSubfieldStartWithSpecification(final String subFieldPath, String value) {
        return (Specification<T>)new FieldStartWithSubstringSpecification<T>(value) {
            public Path<String> getFieldPath(Root<T> root) {
                return SpecificationFactory.this.getSubFieldPathFromRoot(root, subFieldPath);
            }
        };
    }

    default Specification<T> buildFieldStartWithSpecification(final String fieldName, String value) {
        return (Specification)new FieldIsGreaterThanSpecification<T, String>(value, true) {
            public Path<String> getFieldPath(Root<T> root) {
                return root.get(fieldName);
            }
        };
    }

    default Specification<T> buildFieldGreaterThanSpecification(final String fieldName, String value) {
        return (Specification)new FieldIsGreaterThanSpecification<T, String>(value, true) {
            public Path<String> getFieldPath(Root<T> root) {
                return root.get(fieldName);
            }
        };

    }

    default Specification<T> buildFieldLesserThanSpecification(final String fieldName, String value) {
        return (Specification)new FieldIsLesserThanSpecification<T, String>(value, true) {
            public Path<String> getFieldPath(Root<T> root) {
                return root.get(fieldName);
            }
        };

    }

    default Specification<T> buildFieldDateGreaterThanSpecification(final String fieldName, Date value) {
        return (Specification)new FieldIsGreaterThanSpecification<T, Date>(value, true) {
            public Path<Date> getFieldPath(Root<T> root) {
                return root.get(fieldName);
            }
        };
    }

    default Specification<T> buildFieldDateLessThanSpecification(final String fieldName, Date value) {
        return (Specification)new FieldIsLesserThanSpecification<T, Date>(value, true) {
            public Path<Date> getFieldPath(Root<T> root) {
                return root.get(fieldName);
            }
        };
    }

    default Specification<T> buildFieldIsNullSpecification(final String fieldName) {
        return (Specification)new FieldIsNullSpecification<T, Object>() {
            public Path<Object> getFieldPath(Root<T> root) {
                return root.get(fieldName);
            }
        };
    }

    private String[] getSubFieldsNames(String subfield) {
        if (subfield == null || subfield.length() == 0)
            throw new IllegalArgumentException(String.format("Invalid subfield name [%s].", new Object[] { subfield }));
        return subfield.split("\\.");
    }

    default <S> Path<S> getSubFieldPathFromRoot(Root<T> root, String subFieldPath) {
        String[] subFieldNames = getSubFieldsNames(subFieldPath);
        Join<?, ?> subFieldJoin = null;
        for (int i = 0; i < subFieldNames.length; i++) {
            if (subFieldJoin == null) {
                subFieldJoin = root.join(subFieldNames[i], JoinType.LEFT);
            } else if (i + 1 < subFieldNames.length) {
                subFieldJoin = subFieldJoin.join(subFieldNames[i]);
            } else {
                return subFieldJoin.get(subFieldNames[i]);
            }
        }
        throw new IllegalArgumentException("Unable to get subFieldPath!");
    }
}
