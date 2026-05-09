package com.opusreno.worker;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opusreno.common.json.ObjectMapperFactory;
import com.opusreno.worker.service.WorkerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkerLambdaHandlerTest {

    @Mock private WorkerService workerService;
    @Mock private Context context;

    private WorkerLambdaHandler handler;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapperFactory().create();
        handler = new WorkerLambdaHandler(workerService, mapper);
    }

    @Test
    void handleRequest_singleMessage_success() {
        SQSEvent event = eventWithMessages(message("msg-1", "{\"leadId\":\"lead-abc\"}"));

        SQSBatchResponse response = handler.handleRequest(event, context);

        assertThat(response.getBatchItemFailures()).isEmpty();
        verify(workerService).process("lead-abc");
    }

    @Test
    void handleRequest_multipleMessages_allSucceed() {
        SQSEvent event = eventWithMessages(
                message("msg-1", "{\"leadId\":\"lead-1\"}"),
                message("msg-2", "{\"leadId\":\"lead-2\"}"),
                message("msg-3", "{\"leadId\":\"lead-3\"}")
        );

        SQSBatchResponse response = handler.handleRequest(event, context);

        assertThat(response.getBatchItemFailures()).isEmpty();
        verify(workerService).process("lead-1");
        verify(workerService).process("lead-2");
        verify(workerService).process("lead-3");
    }

    @Test
    void handleRequest_oneMessageFails_onlyThatOneInFailures() {
        doNothing().when(workerService).process("lead-1");
        doThrow(new RuntimeException("DDB error")).when(workerService).process("lead-2");
        doNothing().when(workerService).process("lead-3");

        SQSEvent event = eventWithMessages(
                message("msg-1", "{\"leadId\":\"lead-1\"}"),
                message("msg-2", "{\"leadId\":\"lead-2\"}"),
                message("msg-3", "{\"leadId\":\"lead-3\"}")
        );

        SQSBatchResponse response = handler.handleRequest(event, context);

        assertThat(response.getBatchItemFailures()).hasSize(1);
        assertThat(response.getBatchItemFailures().get(0).getItemIdentifier()).isEqualTo("msg-2");
        verify(workerService).process("lead-1");
        verify(workerService).process("lead-3");
    }

    @Test
    void handleRequest_malformedJson_failsGracefully() {
        SQSEvent event = eventWithMessages(message("msg-bad", "not json"));

        SQSBatchResponse response = handler.handleRequest(event, context);

        assertThat(response.getBatchItemFailures()).hasSize(1);
        assertThat(response.getBatchItemFailures().get(0).getItemIdentifier()).isEqualTo("msg-bad");
        verifyNoInteractions(workerService);
    }

    @Test
    void handleRequest_emptyBatch_returnsEmptyFailures() {
        SQSEvent event = new SQSEvent();
        event.setRecords(List.of());

        SQSBatchResponse response = handler.handleRequest(event, context);

        assertThat(response.getBatchItemFailures()).isEmpty();
    }

    private SQSEvent eventWithMessages(SQSEvent.SQSMessage... messages) {
        SQSEvent event = new SQSEvent();
        event.setRecords(List.of(messages));
        return event;
    }

    private SQSEvent.SQSMessage message(String messageId, String body) {
        SQSEvent.SQSMessage msg = new SQSEvent.SQSMessage();
        msg.setMessageId(messageId);
        msg.setBody(body);
        return msg;
    }
}
