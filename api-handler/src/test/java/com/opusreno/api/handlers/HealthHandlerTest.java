package com.opusreno.api.handlers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.opusreno.common.json.ObjectMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static
        org.assertj.core.api.Assertions.assertThat;

class HealthHandlerTest {

    private HealthHandler handler;
    private com.fasterxml.jackson.databind.ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapperFactory().create();
        handler = new HealthHandler();
    }

    @Test
    void handle_returnsOk() throws Exception
    {
        APIGatewayV2HTTPResponse response = handler.handle();

        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(response.getHeaders()).containsEntry("Content-Type", "application/json");

        JsonNode body = mapper.readTree(response.getBody());

        assertThat(body.get("status").asText()).isEqualTo("ok");

        assertThat(body.get("version").asText()).isNotBlank();

        assertThat(body.get("timestamp").asText()).isNotBlank();
    }
}