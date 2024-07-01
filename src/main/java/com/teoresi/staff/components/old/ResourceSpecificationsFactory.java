package com.teoresi.staff.components.old;

import com.teoresi.staff.entities.old.Resource;
import com.teoresi.staff.entities.old.ResourceHourlyCost;
import com.teoresi.staff.entities.old.ResourceSkill;
import com.teoresi.staff.libs.data.components.SpecificationFactory;
import com.teoresi.staff.libs.data.models.SimpleFilterOperator;
import com.teoresi.staff.libs.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Component
public class ResourceSpecificationsFactory implements SpecificationFactory<Resource> {

    private final Set<String> searchableFields = Set.of("name",
            "surname",
            "hiringDate",
            "trigram",
            "id",
            "currentHourlyCost",
            "birthDate",
            "leaveDate");

    private final Set<String> searchableSubfields = Set.of("unit.type", "unit.id", "unit.status", "unit.trigram", "resourceSkills.id.skillId", "resourceSkills.rating");
    private final Logger logger = LoggerFactory.getLogger(ResourceSpecificationsFactory.class);
    private static final String DATA_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String INVALID_DATA_PARSING = "Parsing must follow this format" + DATA_PATTERN;

    @Override
    public Specification<Resource> createSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        if (searchableFields.contains(fieldName)) {
            return buildFieldSpecification(fieldName, operator, value);
        } else if (searchableSubfields.contains(fieldName)) {
            return buildSubfieldSpecification(fieldName, operator, value);
        }
        String message = String.format("Field %s is not allowed for search.", fieldName);
        logger.debug(message);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private Specification<Resource> buildFieldSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        switch (operator) {
            case EQUALS:
                return buildFieldIsEqualSpecification(fieldName, value);
            case IS_LIKE:
                return buildFieldIsLikeIgnoreCaseSpecification(fieldName, value);
            case NOT_EQUALS:
                return buildFieldIsNotEqualSpecification(fieldName, value);

            case IS_LTE:
                return buildFieldGreaterThanSpecification(fieldName, value);

            case IS_GTE:
                return buildFieldLesserThanSpecification(fieldName, value);

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

            case HOURLY_COST_IS_LTE:
                return buildCurrentHourlyCost(Float.parseFloat(value), SimpleFilterOperator.IS_LTE);

            case HOURLY_COST_IS_GTE:
                return buildCurrentHourlyCost(Float.parseFloat(value), SimpleFilterOperator.IS_GTE);

            case IS_NULL:
                return buildFieldIsNullSpecification(fieldName);

            default: {
                String message = String
                        .format("Search using operator %s and field %s is not implemented.", operator.name(), fieldName);
                logger.debug(message);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }
        }
    }

    private Specification<Resource> buildSubfieldSpecification(String fieldName, SimpleFilterOperator operator, String value) {
        switch (operator) {
            case EQUALS:
                return buildSubfieldIsEqualSpecification(fieldName, value);
            case IS_LIKE:
                return buildSubfieldIsLikeIgnoreCaseSpecification(fieldName, value);
            case IS_LTE:
                return buildSubFieldGreaterThanSpecification(fieldName, Integer.parseInt(value));
            case SKILL_RATING_IS_GTE:
                try {
                    Pair<Long, Integer> values = parseResIdAndRole(value);
                    Long skillId = values.getFirst();
                    Integer rating = values.getSecond();
                    return buildProjectsOfAResourceByIdAndRole(skillId, rating);

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

    private Pair<Long, Integer> parseResIdAndRole(String value) throws IllegalArgumentException {

        String[] values = value.split("-");
        if (values.length != 2) {
            throw new IllegalArgumentException("The string is not formatted correctly");
        }
        Long skillId = Long.parseLong(values[0]);
        Integer rating = Integer.parseInt(values[1]);
        return new Pair<>(skillId, rating);
    }

    private Specification<Resource> buildProjectsOfAResourceByIdAndRole(Long skillId, Integer rating) {
        return (root, query, builder) -> {
            Join<Resource, ResourceSkill> resourceResourceSkillJoin = root.join("resourceSkills");
            Predicate skillIdPredicate = builder.equal(resourceResourceSkillJoin.get("id").get("skillId"), skillId);
            Predicate ratingPredicate = builder.greaterThanOrEqualTo(resourceResourceSkillJoin.get("rating"), rating);

            query.distinct(true);

            return builder.and(skillIdPredicate, ratingPredicate);
        };
    }

    private Specification<Resource> buildCurrentHourlyCost(float hourlyCost, SimpleFilterOperator op) {


        return (root, query, builder) -> {

            LocalDate currentDate = LocalDate.now();
            Predicate costPredicate;

            Join<Resource, ResourceHourlyCost> resourceHourlyCostJoin = root.join("hourlyCosts");

            Subquery<Date> subquery = query.subquery(Date.class);
            Root<ResourceHourlyCost> subRoot = subquery.from(ResourceHourlyCost.class);
            subquery.select(builder.greatest(subRoot.<Date>get("startDate")))
                    .where(
                            builder.equal(subRoot.get("resource").get("id"), resourceHourlyCostJoin.get("resource").get("id")),
                            builder.lessThanOrEqualTo(subRoot.get("startDate"), currentDate)
                    );


            if(op.equals(SimpleFilterOperator.IS_LTE))
                costPredicate = builder.lessThanOrEqualTo(resourceHourlyCostJoin.get("cost"), hourlyCost);
            else
                costPredicate = builder.greaterThanOrEqualTo(resourceHourlyCostJoin.get("cost"), hourlyCost);


            Predicate datePredicate = builder.equal(resourceHourlyCostJoin.get("startDate"), subquery);

            return builder.and(costPredicate, datePredicate);
        };
    }

}
