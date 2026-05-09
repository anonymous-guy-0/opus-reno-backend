package com.opusreno.common.queue;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Slf4j
public class SqsPublisher {

    private final SqsClient sqsClient;
    private final String queueUrl;

    public SqsPublisher(SqsClient sqsClient, String queueUrl) {
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
    }

    public String publish(String leadId) {
        String body = "{\"leadId\":\"" + leadId + "\"}";
        SendMessageResponse response = sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(body)
                .build());
        log.info("published leadId={} messageId={}", leadId, response.messageId());
        return response.messageId();
    }
}
