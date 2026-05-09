package com.opusreno.api.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opusreno.api.dto.LeadRequestDto;
import com.opusreno.api.service.LeadService;
import com.opusreno.common.errors.SpamDetectedException;
import com.opusreno.common.errors.ValidationException;
import com.opusreno.common.json.ObjectMapperFactory;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadsHandlerTest {

    @Mock private LeadService leadService;

    private LeadsHandler handler;
    private ObjectMapper mapper;
    private long now;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapperFactory().create();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        now = Instant.parse("2026-05-08T12:00:00Z").toEpochMilli();
        Clock clock = Clock.fixed(Instant.ofEpochMilli(now), ZoneOffset.UTC);
        handler = new LeadsHandler(leadService, mapper, validator, clock);
    }

    @Test
    void handle_validRequest_returns200() throws Exception {
        when(leadService.acceptLead(any(LeadRequestDto.class))).thenReturn("lead-123");

        APIGatewayV2HTTPResponse response = handler.handle(eventWithBody(validBody()));

        assertThat(response.getStatusCode()).isEqualTo(200);
        JsonNode body = mapper.readTree(response.getBody());
        assertThat(body.get("leadId").asText()).isEqualTo("lead-123");
        assertThat(body.get("status").asText()).isEqualTo("received");
        verify(leadService).acceptLead(any());
    }

    @Test
    void handle_honeypotFilled_returnsFakeSuccess() throws Exception {
        String json = validBody().replace("\"website\":null", "\"website\":\"http://spam.com\"");

        APIGatewayV2HTTPResponse response = handler.handle(eventWithBody(json));

        assertThat(response.getStatusCode()).isEqualTo(200);
        JsonNode body = mapper.readTree(response.getBody());
        assertThat(body.get("leadId").asText()).startsWith("fake-");
        verifyNoInteractions(leadService);
    }

    @Test
    void handle_formSubmittedTooQuickly_throwsSpam() {
        String json = validBody().replace(
                "\"formOpenedAt\":" + (now - 5000),
                "\"formOpenedAt\":" + (now - 1000));

        assertThatThrownBy(() -> handler.handle(eventWithBody(json)))
                .isInstanceOf(SpamDetectedException.class)
                .hasMessageContaining("too quickly");
    }

    @Test
    void handle_missingRequiredField_throwsValidation() {
        String json = """
                {"phone":"+919876543210","source":"heroCta","type":"homeowner","scope":"fullRenovation","sessionId":"s1","formOpenedAt":%d}
                """.formatted(now - 5000);

        assertThatThrownBy(() -> handler.handle(eventWithBody(json)))
                .isInstanceOf(ValidationException.class)
                .satisfies(ex -> {
                    var details = ((ValidationException) ex).getDetails();
                    assertThat(details).anyMatch(d -> d.contains("name"));
                });
    }

    @Test
    void handle_invalidPhone_throwsValidation() {
        String json = validBody().replace("+919876543210", "12345");

        assertThatThrownBy(() -> handler.handle(eventWithBody(json)))
                .isInstanceOf(ValidationException.class)
                .satisfies(ex -> {
                    var details = ((ValidationException) ex).getDetails();
                    assertThat(details).anyMatch(d -> d.contains("phone"));
                });
    }

    @Test
    void handle_malformedJson_throwsValidation() {
        assertThatThrownBy(() -> handler.handle(eventWithBody("not json")))
                .isInstanceOf(ValidationException.class)
                .satisfies(ex -> {
                    var details = ((ValidationException) ex).getDetails();
                    assertThat(details).contains("body: invalid JSON");
                });
    }

    private String validBody() {
        return """
                {
                  "name":"Rahul Sharma",
                  "phone":"+919876543210",
                  "email":"rahul@example.com",
                  "source":"heroCta",
                  "type":"homeowner",
                  "scope":"fullRenovation",
                  "budget":"5L-10L",
                  "timeline":"1-3_months",
                  "area":"Koregaon Park",
                  "message":"Need full renovation",
                  "sessionId":"sess-123",
                  "website":null,
                  "formOpenedAt":%d
                }
                """.formatted(now - 5000);
    }

    private APIGatewayV2HTTPEvent eventWithBody(String body) {
        return APIGatewayV2HTTPEvent.builder()
                .withBody(body)
                .build();
    }
}
