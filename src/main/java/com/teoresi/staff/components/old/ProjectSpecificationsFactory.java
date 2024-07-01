package com.teoresi.staff.components.old;

import com.teoresi.staff.entities.old.Allocation;
import com.teoresi.staff.entities.old.Project;
import com.teoresi.staff.libs.data.components.SpecificationFactory;
import com.teoresi.staff.libs.data.models.SimpleFilterOperator;
import com.teoresi.staff.entities.old.Resource;
import com.teoresi.staff.libs.utils.Pair;
import com.teoresi.staff.shared.models.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.Set;

@Component
public class ProjectSpecificationsFactory implements SpecificationFactory<Project> {

    private final Set<String> searchableFields = Set.of(
            "id",
            "name",
            "industry",
            "bmTrigram",
            "status");
    private final Logger logger = LoggerFactory.getLogger(ProjectSpecificationsFactory.class);

    private final Set<String> searchableSubfields = Set.of(
            "allocations.resource.id - allocations.role",
            "PRESALE.id",
            "DUM.id");

    @Override
    public Specification<Project> createSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        if (searchableFields.contains(fieldName)) {
            return buildFieldSpecification(fieldName, operator, value);
        } else if (searchableSubfields.contains(fieldName)) {
            return buildSubfieldSpecification(fieldName, operator, value);
        }
        String message = String.format("Field %s is not allowed for search.", fieldName);
        logger.debug(message);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private Specification<Project> buildFieldSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        switch (operator) {
            case EQUALS:
                return buildFieldIsEqualSpecification(fieldName, value);
            case IS_LIKE:
                return buildFieldIsLikeIgnoreCaseSpecification(fieldName, value);
            case NOT_EQUALS:
                return buildFieldIsNotEqualSpecification(fieldName, value);
            case IS_GTE:
                return null; //buildFieldIsGreaterThanOrEquals(fieldName, value);
            default: {
                String message = String
                        .format("Search using operator %s and field %s is not implemented.", operator.name(), fieldName);
                logger.debug(message);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }
        }
    }


    private Specification<Project> buildSubfieldSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        switch (operator) {
            case EQUALS:
                return buildSubfieldIsEqualSpecification(fieldName, value);
            case IS_LIKE:
                return buildSubfieldIsLikeIgnoreCaseSpecification(fieldName, value);

            case RES_ID_AND_ROLE:

                try {
                    Pair<Long, String> values = parseResIdAndRole(value);
                    Long resourceId = values.getFirst();
                    String roleString = values.getSecond();
                    Role role = Role.valueOf(roleString);

                    return buildProjectsOfAResourceByIdAndRole(resourceId, role);

                } catch (IllegalArgumentException e) {
                    String message = String
                            .format("Error: " + e.getMessage());
                    logger.debug(message);
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
                }

            default: {
                String message = String
                    .format("Search using operator %s and field %s is not implemented.", operator.name(), fieldName);
                logger.debug(message);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }
        }
    }


    private Pair<Long, String> parseResIdAndRole(String value) throws IllegalArgumentException {

        // " id - role "
        String[] values = value.split("-");
        if (values.length != 2) {
            throw new IllegalArgumentException("The string is not formatted correctly");
        }
        Long resourceId = Long.parseLong(values[0]);
        String role = values[1];
        return new Pair<>(resourceId, role);
    }

    public Specification<Project> buildProjectsOfAResourceByIdAndRole(Long resourceId, Role role) {
        return (root, query, builder) -> {
            Join<Project, Allocation> allocationJoin = root.join("allocations", JoinType.INNER);
            Join<Allocation, Resource> resourceJoin = allocationJoin.join("resource", JoinType.INNER);
            Predicate idPredicate = builder.equal(resourceJoin.get("id"), resourceId);
            Predicate rolePredicate = builder.equal(allocationJoin.get("role"), role);
            Predicate commitmentPredicate = builder.equal(allocationJoin.get("isRealCommitment"), true);

            query.distinct(true);

            return builder.and(idPredicate, rolePredicate, commitmentPredicate);
        };
    }

}
