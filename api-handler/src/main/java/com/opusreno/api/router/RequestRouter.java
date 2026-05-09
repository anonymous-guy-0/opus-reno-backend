package com.opusreno.api.router;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.opusreno.api.handlers.HealthHandler;
import com.opusreno.api.handlers.LeadsHandler;
import com.opusreno.common.errors.SpamDetectedException;
import com.opusreno.common.errors.ValidationException;
import com.opusreno.common.json.Responses;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.List;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RequestRouter {

    private final HealthHandler healthHandler;
    private final LeadsHandler leadsHandler;

    public APIGatewayV2HTTPResponse route(APIGatewayV2HTTPEvent event, Context ctx) {
        String method = event.getRequestContext().getHttp().getMethod();
        String path = event.getRequestContext().getHttp().getPath();

        MDC.put("requestId", ctx.getAwsRequestId());

        try {
            log.info("dispatching {} {}", method, path);

            return switch (method + " " + path) {
                case "GET /api/health" -> healthHandler.handle();
                case "POST /api/leads" -> leadsHandler.handle(event);
                default -> Responses.notFound("no route for " + method + " " + path);
            };
        } catch (ValidationException e) {
            return Responses.badRequest("VALIDATION_ERROR", e.getDetails());
        } catch (SpamDetectedException e) {
            return Responses.badRequest("SPAM_DETECTED", List.of(e.getMessage()));
        } catch (Exception e) {
            log.error("unhandled exception", e);
            return Responses.serverError();
        } finally {
            MDC.clear();
        }
    }
}
