package com.opusreno.api.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opusreno.api.dto.LeadRequestDto;
import com.opusreno.api.service.LeadService;
import com.opusreno.common.errors.SpamDetectedException;
import com.opusreno.common.errors.ValidationException;
import com.opusreno.common.json.Responses;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class LeadsHandler {

    private final LeadService leadService;
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final Clock clock;

    public APIGatewayV2HTTPResponse handle(APIGatewayV2HTTPEvent event) {
        LeadRequestDto dto;
        try {
            dto = objectMapper.readValue(event.getBody(), LeadRequestDto.class);
        } catch (Exception e) {
            throw new ValidationException(java.util.List.of("body: invalid JSON"));
        }

        // Honeypot — return fake success to deceive bots
        if (dto.website != null && !dto.website.isBlank()) {
            log.warn("honeypot tripped, session={}", dto.sessionId);
            return Responses.success(Map.of(
                    "leadId", "fake-" + dto.sessionId,
                    "status", "received",
                    "message", "Thanks — we'll contact you within 24 hours."));
        }

        // Time check
        long elapsed = clock.millis() - dto.formOpenedAt;
        if (elapsed < 3000) {
            throw new SpamDetectedException(
                    "form submitted too quickly (" + elapsed + "ms < 3000ms)");
        }

        // Jakarta validation
        Set<ConstraintViolation<LeadRequestDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            var details = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .toList();
            throw new ValidationException(details);
        }

        // Process
        String leadId = leadService.acceptLead(dto);
        return Responses.success(Map.of(
                "leadId", leadId,
                "status", "received",
                "message", "Thanks — we'll contact you within 24 hours."));
    }
}
