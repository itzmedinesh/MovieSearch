package io.demo.MovieSearch;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@SpringBootApplication
public class MovieSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieSearchApplication.class, args);
    }

    @Bean
    public DynamoDbClient initDynamoDB(@Value("${dynamodb.endpoint}") String dynamoDbEndpoint, @Value("${dynamodb.region}") String awsRegion) {
        // Create a DynamoDB client with local endpoint
        return DynamoDbClient.builder()
                .endpointOverride(URI.create(dynamoDbEndpoint))
                .region(Region.of(awsRegion))
                .build();
    }

}