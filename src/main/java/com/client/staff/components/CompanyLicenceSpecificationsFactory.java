package com.client.staff.components;

import com.client.staff.entities.CompanyLicence;
import com.client.staff.libs.data.components.SpecificationFactory;
import com.client.staff.libs.data.models.SimpleFilterOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Component
public class CompanyLicenceSpecificationsFactory implements SpecificationFactory<CompanyLicence> {

    private final Set<String> searchableFields = Set.of("id",
            "expiryDate");
    private final Set<String> searchableSubfields = Set.of(
            "company.name",
            "licence.name");

    private static final String DATA_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String INVALID_DATA_PARSING = "Parsing must follow this format" + DATA_PATTERN;
    private final Logger logger = LoggerFactory.getLogger(CompanyLicenceSpecificationsFactory.class);

    @Override
    public Specification<CompanyLicence> createSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        if (searchableFields.contains(fieldName)) {
            return buildFieldSpecification(fieldName, operator, value);
        } else if (searchableSubfields.contains(fieldName)) {
            return buildSubfieldSpecification(fieldName, operator, value);
        }
        String message = String.format("Field %s is not allowed for search.", fieldName);
        logger.debug(message);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private Specification<CompanyLicence> buildSubfieldSpecification(String fieldName, SimpleFilterOperator operator, String value) {
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

    private Specification<CompanyLicence> buildFieldSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        switch (operator) {
            case EQUALS:
                return buildFieldIsEqualSpecification(fieldName, value);
            case IS_LIKE:
                return buildFieldIsLikeIgnoreCaseSpecification(fieldName, value);
            case IS_DATE_LTE:

                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATA_PATTERN);
                    Date date = dateFormat.parse(value);
                    return buildFieldDateLessThanSpecification(fieldName, date);
                } catch (ParseException e) {
                    String message = String.format(INVALID_DATA_PARSING, e.getMessage());
                    logger.debug(message);
                }


            case IS_DATE_GTE:
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATA_PATTERN);
                    Date date = dateFormat.parse(value);
                    return buildFieldDateGreaterThanSpecification(fieldName, date);
                } catch (ParseException e) {
                    String message = String.format(INVALID_DATA_PARSING, e.getMessage());
                    logger.debug(message);
                }

            default: {
                String message = String
                        .format("Search using operator %s and field %s is not implemented.", operator.name(), fieldName);
                logger.debug(message);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }
        }
    }
}
