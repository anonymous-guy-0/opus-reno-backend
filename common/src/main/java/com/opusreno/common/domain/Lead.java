package com.opusreno.common.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

import java.time.Instant;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Lead {

    private String leadId;
    private String gsi1Pk;
    private String phone;

    private String source;
    private String type;
    private String scope;
    private String budget;
    private Integer budgetRank;
    private String timeline;

    private String name;
    private String email;
    private String area;
    private String message;

    private String sessionId;
    private Instant createdAt;
    private Instant processedAt;

    @DynamoDbPartitionKey
    public String getLeadId() { return leadId; }

    @DynamoDbSecondaryPartitionKey(indexNames = "GSI1")
    public String getGsi1Pk() { return gsi1Pk; }

    @DynamoDbSecondaryPartitionKey(indexNames = "GSI2")
    public String getPhone() { return phone; }

    @DynamoDbSecondarySortKey(indexNames = {"GSI1", "GSI2"})
    public Instant getCreatedAt() { return createdAt; }
}
