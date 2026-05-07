package com.opusreno.worker;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import java.util.List;

/**
 * Entry point for the Opus Reno Worker Lambda.
 *
 * Scaffold only — acks all messages and does nothing. Real processing
 * (parse JSON → update lead record with processedAt) arrives in Task 6.
 *
 * The CDK infrastructure expects this class at path:
 *   com.opusreno.worker.WorkerLambdaHandler::handleRequest
 *
 * Returns SQSBatchResponse with empty failures list because the CDK
 * SqsEventSource has reportBatchItemFailures=true enabled. Never throw
 * from this handler — doing so re-delivers the entire batch.
 */
public class WorkerLambdaHandler
        implements RequestHandler<SQSEvent, SQSBatchResponse> {

    @Override
    public SQSBatchResponse handleRequest(SQSEvent event, Context ctx) {
        // Scaffold: acknowledge every message silently.
        return SQSBatchResponse.builder()
                .withBatchItemFailures(List.of())
                .build();
    }
}
