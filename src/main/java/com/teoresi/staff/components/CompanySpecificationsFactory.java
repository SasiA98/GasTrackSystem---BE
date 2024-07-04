package com.teoresi.staff.components;

import com.teoresi.staff.controllers.CompanyController;
import com.teoresi.staff.entities.Company;
import com.teoresi.staff.entities.CompanyLicence;
import com.teoresi.staff.libs.data.components.SpecificationFactory;
import com.teoresi.staff.libs.data.models.SimpleFilterOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Component
public class CompanySpecificationsFactory implements SpecificationFactory<Company> {

    private final Set<String> searchableFields = Set.of("id",
            "name",
            "email");
    private final Set<String> searchableSubfields = Set.of();

    private final Logger logger = LoggerFactory.getLogger(CompanySpecificationsFactory.class);

    @Override
    public Specification<Company> createSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        if (searchableFields.contains(fieldName)) {
            return buildFieldSpecification(fieldName, operator, value);
        } else if (searchableSubfields.contains(fieldName)) {
            return buildSubfieldSpecification(fieldName, operator, value);
        }
        String message = String.format("Field %s is not allowed for search.", fieldName);
        logger.debug(message);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private Specification<Company> buildSubfieldSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        switch (operator) {
            case EQUALS:
                return buildSubfieldIsEqualSpecification(fieldName, value);
            case IS_LIKE:
                return buildSubfieldIsLikeIgnoreCaseSpecification(fieldName, value);
            case IS_LTE:
                return buildSubFieldGreaterThanSpecification(fieldName, Integer.parseInt(value));

            default: {
                String message = String
                        .format("Search using operator %s and field %s is not implemented.", operator.name(), fieldName);
                logger.debug(message);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }
        }
    }

    private Specification<Company> buildFieldSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        switch (operator) {
            case EQUALS:
                return buildFieldIsEqualSpecification(fieldName, value);
            case IS_LIKE:
                return buildFieldIsLikeIgnoreCaseSpecification(fieldName, value);

            default: {
                String message = String
                        .format("Search using operator %s and field %s is not implemented.", operator.name(), fieldName);
                logger.debug(message);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }
        }
    }
}
