package com.opusreno.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;

import java.util.Map;

/**
 * Entry point for the Opus Reno API Lambda.
 *
 * Scaffold only — returns 501 for every request. Real wiring (Dagger
 * RequestRouter + handlers) lands in Task 4 (health endpoint) and Task 5
 * (leads endpoint).
 *
 * The CDK infrastructure expects this class at path:
 *   com.opusreno.api.ApiLambdaHandler::handleRequest
 *
 * Configured during backend CI/CD (Task 9) via:
 *   aws lambda update-function-configuration \
 *     --handler com.opusreno.api.ApiLambdaHandler::handleRequest
 */
public class ApiLambdaHandler
        implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context ctx) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(501)
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withBody("{\"error\":\"NOT_IMPLEMENTED\",\"message\":\"scaffold only\"}")
                .build();
    }
}
