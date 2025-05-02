package io.demo.MovieSearch.repository.impl;

import io.demo.MovieSearch.model.MovieScheduleGeoRecord;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

public abstract class AbstractMovieSearchRepository {

    protected DynamoDbTable<MovieScheduleGeoRecord> table;

    /**
     * Constructor that initializes the DynamoDB table, creating it if it doesn't exist.
     *
     * @param dynamoDbClient The DynamoDB client
     */
    protected AbstractMovieSearchRepository(DynamoDbClient dynamoDbClient, String TABLE_NAME) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        try {
            // Check if table exists
            dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName(TABLE_NAME).build());
        } catch (ResourceNotFoundException e) {
            // Table doesn't exist, create it
            createTable(enhancedClient, TABLE_NAME);
            // Wait for table to be active
            waitForTableActive(dynamoDbClient, TABLE_NAME);
        }
        // Initialize the table reference
        this.table = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(MovieScheduleGeoRecord.class));
    }

    /**
     * Calculates the haversine distance between two points on Earth.
     *
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return Distance in kilometers
     */
    protected double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        // Formula to calculate distance between 2 lat/lng points
        final int R = 6371; // Radius of Earth in Km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // in kilometers
    }

    /**
     * Creates the DynamoDB table with required schema and indexes.
     *
     * @param enhancedClient The DynamoDB enhanced client
     */
    protected void createTable(DynamoDbEnhancedClient enhancedClient, String TABLE_NAME) {
        this.table = enhancedClient.table(TABLE_NAME,
                TableSchema.fromBean(MovieScheduleGeoRecord.class));

        // Set projections for the global secondary indexes
        EnhancedGlobalSecondaryIndex cityIndex = EnhancedGlobalSecondaryIndex.builder()
                .indexName("CityIndex")
                .projection(p -> p.projectionType(ProjectionType.ALL))
                .build();

        EnhancedGlobalSecondaryIndex theatreIndex = EnhancedGlobalSecondaryIndex.builder()
                .indexName("TheatreIndex")
                .projection(p -> p.projectionType(ProjectionType.ALL))
                .build();

        EnhancedGlobalSecondaryIndex movieNameIndex = EnhancedGlobalSecondaryIndex.builder()
                .indexName("MovieNameIndex")
                .projection(p -> p.projectionType(ProjectionType.ALL))
                .build();

        EnhancedGlobalSecondaryIndex movieLanguageIndex = EnhancedGlobalSecondaryIndex.builder()
                .indexName("MovieLanguageIndex")
                .projection(p -> p.projectionType(ProjectionType.ALL))
                .build();

        EnhancedGlobalSecondaryIndex movieFormateIndex = EnhancedGlobalSecondaryIndex.builder()
                .indexName("MovieFormatIndex")
                .projection(p -> p.projectionType(ProjectionType.ALL))
                .build();

        // Create the table with the GSI
        CreateTableEnhancedRequest request = CreateTableEnhancedRequest.builder()
                .globalSecondaryIndices(cityIndex, theatreIndex, movieNameIndex, movieLanguageIndex, movieFormateIndex)
                .build();

        table.createTable(request);
    }

    /**
     * Waits for the table to become active before proceeding.
     *
     * @param dynamoDbClient The DynamoDB client
     */
    protected static void waitForTableActive(DynamoDbClient dynamoDbClient, String TABLE_NAME) {
        try {
            boolean isActive = false;
            while (!isActive) {
                DescribeTableResponse response = dynamoDbClient.describeTable(
                        DescribeTableRequest.builder().tableName(TABLE_NAME).build());
                isActive = response.table().tableStatus() == TableStatus.ACTIVE;
                if (!isActive) {
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e1) {
            Thread.currentThread().interrupt();
        }
    }
}
