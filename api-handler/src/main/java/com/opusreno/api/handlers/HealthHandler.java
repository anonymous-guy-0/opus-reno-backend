package com.opusreno.api.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.opusreno.common.json.Responses;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Map;

@Slf4j
public class HealthHandler {

    private static final String VERSION = "0.1.0";

    @Inject
    public HealthHandler() {}

    public APIGatewayV2HTTPResponse handle() {
        log.info("health check for version {}", VERSION);
        return Responses.success(Map.of(
                "status", "ok",
                "version", VERSION,
                "timestamp", Instant.now().toString()));
    }
}
