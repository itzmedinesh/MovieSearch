package io.demo.MovieSearch;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.net.URI;

@Slf4j
@SpringBootApplication
public class MovieSearchApplication {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static void main(String[] args) {
        SpringApplication.run(MovieSearchApplication.class, args);
    }

    @Bean
    @Profile("!cloud")
    public DynamoDbClient initDynamoDBLocal(@Value("${dynamodb.endpoint}") String dynamoDbEndpoint, @Value("${dynamodb.region}") String awsRegion) {
        // Create a DynamoDB client with local endpoint
        return DynamoDbClient.builder()
                .endpointOverride(URI.create(dynamoDbEndpoint))
                .region(Region.of(awsRegion))
                .build();
    }

    @Bean
    @Profile("cloud")
    public DynamoDbClient initDynamoDBCloud(@Value("${dynamodb.region}") String awsRegion) {
        Region region = Region.of(awsRegion);

        try {
            // Create a Secrets Manager client
            Result result = getSecrets(region);

            // Build DynamoDB client with the retrieved credentials
            return DynamoDbClient.builder()
                    .region(region)
                    .credentialsProvider(() ->
                            AwsBasicCredentials.create(result.accessKeyId(), result.secretKey()))
                    .build();

        } catch (Exception e) {
            // Log the error
            System.err.println("Failed to retrieve credentials from Secrets Manager: " + e.getMessage());

            // Fall back to instance profile credentials
            return DynamoDbClient.builder()
                    .region(region)
                    .build();
        }
    }

    private static Result getSecrets(Region region) throws JsonProcessingException {
        SecretsManagerClient secretsClient = SecretsManagerClient.builder()
                .region(region)
                .build();

        // Get the secret value
        GetSecretValueResponse response = secretsClient.getSecretValue(
                GetSecretValueRequest.builder()
                        .secretId("movie-search/dynamodb-credentials")
                        .build());

        // Parse the secret JSON
        JsonNode secretJson = objectMapper.readTree(response.secretString());

        log.info("Secrets retrieved successfully from Secrets Manager.");

        return new Result(secretJson.get("accessKeyId").asText(), secretJson.get("secretKey").asText());
    }

    private record Result(String accessKeyId, String secretKey) {
    }

}
