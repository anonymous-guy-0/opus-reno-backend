package com.opusreno.api.router;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.opusreno.api.handlers.HealthHandler;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;


import java.util.Map;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RequestRouter {
    private final HealthHandler healthHandler;

    public APIGatewayV2HTTPResponse route(APIGatewayV2HTTPEvent event, Context ctx) {
        String method = event.getRequestContext().getHttp().getMethod();

        String path = event.getRequestContext().getHttp().getPath();

        MDC.put("requestId", ctx.getAwsRequestId());

        try {
            log.info("dispatching {} {}", method, path);

            return switch (method + " " + path) {
                case "GET /api/health" -> healthHandler.handle();
                default -> notFound(method, path);
            };
        } catch (Exception e) {
            log.error("unhandled exception", e);
            return serverError();
        } finally {
            MDC.clear();
        }
    }

    private APIGatewayV2HTTPResponse
    notFound(String method, String path) {
        String body = String.format(
                "{\"error\":\"NOT_FOUND\",\"details\":[\"no route for %s %s\"]}",
                method, path
           );
        return APIGatewayV2HTTPResponse.builder()
                        .withStatusCode(404)
                        .withHeaders(Map.of("Content-Type",
                                "application/json"))
                        .withBody(body)
                        .build();
    }

    private APIGatewayV2HTTPResponse serverError() {
        return
                APIGatewayV2HTTPResponse.builder()
                        .withStatusCode(500)

                        .withHeaders(Map.of("Content-Type",
                                "application/json"))

                        .withBody("{\"error\":\"INTERNAL_ERROR\"}")
                        .build();
    }
}
