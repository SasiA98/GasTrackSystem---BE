package com.teoresi.staff.libs.web.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.teoresi.staff.libs.data.models.CompositeFilter;
import com.teoresi.staff.libs.data.models.CompositeFilterOperator;
import com.teoresi.staff.libs.data.models.EmptyFilter;
import com.teoresi.staff.libs.data.models.Filter;
import com.teoresi.staff.libs.data.models.SimpleFilter;
import com.teoresi.staff.libs.data.models.SimpleFilterOperator;
import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FilterDeserializer<T> extends StdDeserializer<Filter<T>> {
    protected FilterDeserializer(Class<?> vc) {
        super(vc);
    }

    public FilterDeserializer() {
        this(null);
    }

    public Filter<T> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode jsonNode = (JsonNode)jsonParser.getCodec().readTree(jsonParser);
        return createFilterFromJsonNode(jsonNode);
    }

    protected SimpleFilter<T> createSimpleFilterFromJsonNode(JsonNode jsonNode) {
        if (jsonNode.hasNonNull("field") && jsonNode
                .hasNonNull("operator")) {
            JsonNode fieldNode = jsonNode.get("field");
            JsonNode operatorNode = jsonNode.get("operator");
            SimpleFilter.SimpleFilterBuilder<T> simpleFilterBuilder = (SimpleFilter.SimpleFilterBuilder<T>) SimpleFilter.builder().field(fieldNode.asText()).operator(getSimpleFilterOperatorFromJsonNode(operatorNode));
            if (jsonNode.hasNonNull("value")) {
                JsonNode valueNode = jsonNode.get("value");
                simpleFilterBuilder.value(valueNode.asText());
            }
            return simpleFilterBuilder.build();
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The given JsonNode is not a valid SimpleFilter.");
    }

    protected CompositeFilter<T> createCompositeFilterFromJsonNode(JsonNode jsonNode) {
        if (jsonNode.hasNonNull("operandOne") && jsonNode
                .hasNonNull("operandTwo") && jsonNode
                .hasNonNull("operator")) {
            JsonNode operandANode = jsonNode.get("operandOne");
            JsonNode operandBNode = jsonNode.get("operandTwo");
            JsonNode operatorNode = jsonNode.get("operator");
            return (CompositeFilter<T>) CompositeFilter.builder()
                    .operandOne((Filter<Object>) createFilterFromJsonNode(operandANode))
                    .operandTwo((Filter<Object>) createFilterFromJsonNode(operandBNode))
                    .operator(getCompositeFilterOperatorFromJsonNode(operatorNode))
                    .build();
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The given JsonNode is not a valid CompositeFilter.");
    }

    protected Filter<T> createFilterFromJsonNode(JsonNode jsonNode) {
        if (jsonNode.isEmpty())
            return (Filter<T>)new EmptyFilter();
        if (jsonNode.hasNonNull("field"))
            return (Filter<T>)createSimpleFilterFromJsonNode(jsonNode);
        if (jsonNode.hasNonNull("operandOne"))
            return (Filter<T>)createCompositeFilterFromJsonNode(jsonNode);
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The given JsonNode is not a valid Filter.");
    }

    protected SimpleFilterOperator getSimpleFilterOperatorFromJsonNode(JsonNode operatorNode) {
        try {
            return SimpleFilterOperator.valueOf(operatorNode.asText());
        } catch (IllegalArgumentException ex) {
            String message = String.format("Invalid SimpleFilterOperator: %s.", operatorNode.asText());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    protected CompositeFilterOperator getCompositeFilterOperatorFromJsonNode(JsonNode operatorNode) {
        try {
            return CompositeFilterOperator.valueOf(operatorNode.asText());
        } catch (IllegalArgumentException ex) {
            String message = String.format("Invalid CompositeFilterOperator: %s.", operatorNode.asText());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
}

