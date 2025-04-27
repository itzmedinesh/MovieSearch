package io.demo.MovieSearch.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.demo.MovieSearch.model.MovieScheduleDetails;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class MovieScheduleDetailsMapper implements AttributeConverter<MovieScheduleDetails> {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public AttributeValue transformFrom(MovieScheduleDetails details) {
        try {
            String json = objectMapper.writeValueAsString(details);
            return AttributeValue.builder().s(json).build();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to serialize MovieScheduleDetails", e);
        }
    }

    @Override
    public MovieScheduleDetails transformTo(AttributeValue attributeValue) {
        try {
            return objectMapper.readValue(attributeValue.s(), MovieScheduleDetails.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to deserialize MovieScheduleDetails", e);
        }
    }

    @Override
    public EnhancedType<MovieScheduleDetails> type() {
        return EnhancedType.of(MovieScheduleDetails.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}