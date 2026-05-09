package com.opusreno.worker;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opusreno.worker.di.DaggerWorkerComponent;
import com.opusreno.worker.di.WorkerComponent;
import com.opusreno.worker.service.WorkerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.List;

public class WorkerLambdaHandler
        implements RequestHandler<SQSEvent, SQSBatchResponse> {

    private static final Logger log = LoggerFactory.getLogger(WorkerLambdaHandler.class);

    private final WorkerService service;
    private final ObjectMapper mapper;

    public WorkerLambdaHandler() {
        WorkerComponent component = DaggerWorkerComponent.create();
        this.service = component.workerService();
        this.mapper = component.objectMapper();
    }

    public WorkerLambdaHandler(WorkerService service, ObjectMapper mapper) {
        this.mapper = mapper;
        this.service = service;
    }

    @Override
    public SQSBatchResponse handleRequest(SQSEvent event, Context ctx) {

        List<SQSBatchResponse.BatchItemFailure> failures = new ArrayList<>();
        log.info("received SQS batch size={}", event.getRecords().size());

        for(SQSEvent.SQSMessage record : event.getRecords()) {
            MDC.put("messageId", record.getMessageId());

            try {
                String leadId = mapper.readTree(record.getBody()).get("leadId").asText();
                MDC.put("leadId", leadId);
                service.process(leadId);
            } catch(Exception e) {
                log.error("failed to process message", e);
                failures.add(SQSBatchResponse.BatchItemFailure.builder()
                        .withItemIdentifier(record.getMessageId())
                        .build());
            } finally {
                MDC.clear();
            }
        }

        log.info("batch complete failures={}", failures.size());
        return SQSBatchResponse.builder()
                .withBatchItemFailures(failures)
                .build();
    }
}
