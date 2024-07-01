package com.teoresi.staff.components.old;

import com.teoresi.staff.entities.old.Allocation;
import com.teoresi.staff.entities.old.Project;
import com.teoresi.staff.entities.old.User;
import com.teoresi.staff.shared.models.Role;
import com.teoresi.staff.libs.data.components.SpecificationFactory;
import com.teoresi.staff.libs.data.models.SimpleFilterOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.*;
import java.util.Set;

@Component
public class UserSpecificationsFactory implements SpecificationFactory<User> {

    private final Set<String> searchableFields = Set.of(
            "id",
            "status");

    private final Set<String> searchableSubfields = Set.of(
            "resource.name",
            "resource.surname",
            "resource.roles",
            "resource.email",
            "resource.employee_id");
    private final Logger logger = LoggerFactory.getLogger(UserSpecificationsFactory.class);

    @Override
    public Specification<User> createSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        if (searchableFields.contains(fieldName)) {
            return buildFieldSpecification(fieldName, operator, value);
        } else if (searchableSubfields.contains(fieldName)) {
            return buildSubfieldSpecification(fieldName, operator, value);
        }
        String message = String.format("Field %s is not allowed for search.", fieldName);
        logger.debug(message);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }


    private Specification<User> buildFieldSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        switch (operator) {
            case EQUALS:
                return buildFieldIsEqualSpecification(fieldName, value);
            case IS_LIKE:
                return buildFieldIsLikeIgnoreCaseSpecification(fieldName, value);
            case NOT_EQUALS:
                return buildFieldIsNotEqualSpecification(fieldName, value);
            case IS_GTE:
                return null; //buildFieldIsGreaterThanOrEquals(fieldName, value);

            case CONTAINS :
                return buildRolesContains(value);

            default: {
                String message = String
                        .format("Search using operator %s and field %s is not implemented.", operator.name(), fieldName);
                logger.debug(message);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }
        }
    }

    private Specification<User> buildSubfieldSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        switch (operator) {
            case EQUALS:
                return buildSubfieldIsEqualSpecification(fieldName, value);
            case IS_LIKE:
                return buildSubfieldIsLikeIgnoreCaseSpecification(fieldName, value);

            default: {
                String message = String
                        .format("Search using operator %s and field %s is not implemented.", operator.name(), fieldName);
                logger.debug(message);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }
        }
    }

    @Override
    public <S> Specification<User> buildSubfieldIsEqualSpecification(final String subFieldPath, S value) {
        if ("resource.roles".equals(subFieldPath)) {
            return buildRolesContains((String) value);
        }
        return SpecificationFactory.super.buildSubfieldIsEqualSpecification(subFieldPath, value);
    }

    private Specification<User> buildRolesContains(String value) {
        Role role = Role.valueOf(value);

        return (root, query, builder) -> {
            Join<Project, Allocation> resourceJoin = root.join("resource", JoinType.INNER);
            return builder.isMember(role, resourceJoin.get("roles"));
        };
    }


    /*

    @Override
    public <S> Specification<User> buildFieldIsEqualSpecification(String fieldName, S value) {
        if ("roles".equals(fieldName)) {
            return buildRolesContains((String) value);
        }
        return SpecificationFactory.super.buildFieldIsEqualSpecification(fieldName, value);
    }

    @Override
    public Specification<User> buildFieldIsLikeIgnoreCaseSpecification(String fieldName, String value) {
        if ("roles".equals(fieldName)) {
            return buildRolesContains(value);
        }
        return SpecificationFactory.super.buildFieldIsLikeIgnoreCaseSpecification(fieldName, value);
    }

    @Override
    public <S> Specification<User> buildFieldIsNotEqualSpecification(String fieldName, S value) {
        if ("id".equals(fieldName)) {
            return buildIdNotEquals(value);
        }
        return SpecificationFactory.super.buildFieldIsNotEqualSpecification(fieldName, value);
    }


    private <S> Specification<User> buildIdNotEquals(S value) {
        Long id;
        if (value != null) {
            try {
                id = Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid value for field id.");
            }
        } else {
            id = null;
        }
        return new FieldIsNotEqualSpecification<>(id) {
            @Override
            public Path<Long> getFieldPath(Root<User> root) {
                return root.get("id");
            }
        };
    }

    private Specification<User> buildFieldIsGreaterThanOrEquals(String fieldName, String value) {
        if ("relatedNotifications".equals(fieldName)) {
            Integer numberOfElements = Integer.parseInt(value);
            return new FieldHasAtLeastNumberOfMembers<User, VulnerabilityNotification>(numberOfElements) {
                @Override
                public Path<Collection<VulnerabilityNotification>> getFieldPath(Root<User> root) {
                    return root.get(fieldName);
                }
            };
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                String.format("Field %s not allowed using operator IS_GTE", fieldName));
    }

    */
}
