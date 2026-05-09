package com.opusreno.common.json;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

public final class Responses {

    private static final ObjectMapper MAPPER = new ObjectMapperFactory().create();
    private static final Map<String, String> JSON_HEADERS = Map.of("Content-Type", "application/json");

    private Responses() {}

    public static APIGatewayV2HTTPResponse success(Object body) {
        return build(200, body);
    }

    public static APIGatewayV2HTTPResponse badRequest(String error, List<String> details) {
        return build(400, Map.of("error", error, "details", details));
    }

    public static APIGatewayV2HTTPResponse notFound(String detail) {
        return build(404, Map.of("error", "NOT_FOUND", "details", List.of(detail)));
    }

    public static APIGatewayV2HTTPResponse serverError() {
        return build(500, Map.of("error", "INTERNAL_ERROR"));
    }

    private static APIGatewayV2HTTPResponse build(int status, Object body) {
        try {
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(status)
                    .withHeaders(JSON_HEADERS)
                    .withBody(MAPPER.writeValueAsString(body))
                    .build();
        } catch (JsonProcessingException e) {
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(500)
                    .withHeaders(JSON_HEADERS)
                    .withBody("{\"error\":\"INTERNAL_ERROR\"}")
                    .build();
        }
    }
}
