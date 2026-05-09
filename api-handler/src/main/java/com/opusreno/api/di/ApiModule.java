package com.opusreno.api.di;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opusreno.common.dao.LeadDao;
import com.opusreno.common.json.ObjectMapperFactory;
import com.opusreno.common.queue.SqsPublisher;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.time.Clock;
import java.util.UUID;
import java.util.function.Supplier;

@Module
public class ApiModule {

    @Provides
    @Singleton
    static ObjectMapper objectMapper() {
        return new ObjectMapperFactory().create();
    }

    @Provides
    @Singleton
    static Clock clock() {
        return Clock.systemUTC();
    }

    @Provides
    @Singleton
    @Named("idSupplier")
    static Supplier<String> idSupplier() {
        return () -> UUID.randomUUID().toString();
    }

    @Provides
    @Named("leadsTableName")
    static String leadsTableName() {
        return System.getenv("LEADS_TABLE_NAME");
    }

    @Provides
    @Named("queueUrl")
    static String queueUrl() {
        return System.getenv("QUEUE_URL");
    }

    @Provides
    @Singleton
    static DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder().build();
    }

    @Provides
    @Singleton
    static DynamoDbEnhancedClient enhancedClient(DynamoDbClient client) {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
    }

    @Provides
    @Singleton
    static SqsClient sqsClient() {
        return SqsClient.builder().build();
    }

    @Provides
    @Singleton
    static LeadDao leadDao(DynamoDbEnhancedClient client, @Named("leadsTableName") String tableName) {
        return new LeadDao(client, tableName);
    }

    @Provides
    @Singleton
    static SqsPublisher sqsPublisher(SqsClient client, @Named("queueUrl") String queueUrl) {
        return new SqsPublisher(client, queueUrl);
    }

    @Provides
    @Singleton
    static Validator validator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }
}
