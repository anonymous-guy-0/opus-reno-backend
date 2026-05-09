package com.opusreno.common.dao;

import com.opusreno.common.config.Constants;
import com.opusreno.common.domain.Lead;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.time.Instant;
import java.util.Optional;

@Slf4j
public class LeadDao {

    private final DynamoDbTable<Lead> table;

    public LeadDao(DynamoDbEnhancedClient client, String tableName) {
        this.table = client.table(tableName, TableSchema.fromBean(Lead.class));
    }

    public void save(Lead lead) {
        try {
            table.putItem(PutItemEnhancedRequest.builder(Lead.class)
                    .item(lead)
                    .conditionExpression(Expression.builder()
                            .expression("attribute_not_exists(leadId)")
                            .build())
                    .build());
        } catch (ConditionalCheckFailedException e) {
            log.warn("duplicate leadId={}, treating as idempotent replay", lead.getLeadId());
        }
    }

    public Optional<Lead> findById(String leadId) {
        Lead lead = table.getItem(Key.builder().partitionValue(leadId).build());
        return Optional.ofNullable(lead);
    }

    public void markProcessed(String leadId, Instant processedAt) {
        Lead lead = new Lead();
        lead.setLeadId(leadId);
        lead.setProcessedAt(processedAt);
        table.updateItem(UpdateItemEnhancedRequest.builder(Lead.class)
                .item(lead)
                .ignoreNulls(true)
                .build());
    }

    public int countRecentByPhone(String phone, Instant since) {
        DynamoDbIndex<Lead> gsi2 = table.index(Constants.GSI2_INDEX_NAME);

        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.sortGreaterThan(k -> k
                        .partitionValue(phone)
                        .sortValue(since.toString())))
                .limit(Constants.DEDUP_MAX_LEADS_PER_PHONE)
                .build();

        return gsi2.query(request).stream()
                .findFirst()
                .map(page -> page.items().size())
                .orElse(0);
    }
}
