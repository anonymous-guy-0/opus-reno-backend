package com.opusreno.worker.di;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opusreno.common.dao.LeadDao;
import com.opusreno.common.json.ObjectMapperFactory;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.time.Clock;

@Module
public class WorkerModule {

    @Provides
    @Singleton
    static ObjectMapper objectMapper() { return new ObjectMapperFactory().create();}

    @Provides
    @Singleton
    static Clock clock() { return Clock.systemUTC();}

    @Provides
    @Named("leadsTableName")
    static String leadsTableName() { return System.getenv("LEADS_TABLE_NAME");}

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
    static LeadDao leadDao(DynamoDbEnhancedClient client, @Named("leadsTableName") String tableName) {
        return new LeadDao(client, tableName);
    }


}
