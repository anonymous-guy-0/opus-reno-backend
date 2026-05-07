package com.opusreno.api.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Map;

@Slf4j
public class HealthHandler {

    private static final String VERSION = "0.1.0";
    private final ObjectMapper mapper;

    @Inject
    public HealthHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public APIGatewayV2HTTPResponse handle() {
        try {
            String body =
                    mapper.writeValueAsString(Map.of(
                            "status",    "ok",
                            "version",   VERSION,
                            "timestamp",
                            Instant.now().toString()
                    ));
            log.info("health check for version {}", VERSION);
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withHeaders(Map.of("Content-Type", "application/json"))
                    .withBody(body)
                    .build();
        } catch (JsonProcessingException e) {
            log.error("failed to serialize health response", e);
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(500)
                    .withBody("{\"error\":\"INTERNAL_ERROR\"}")
                    .build();
        }

    }

}
