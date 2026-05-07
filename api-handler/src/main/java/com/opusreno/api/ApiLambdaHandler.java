package com.opusreno.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.opusreno.api.router.RequestRouter;
import com.opusreno.api.di.DaggerApiComponent;

public class ApiLambdaHandler
        implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private final RequestRouter router;

    public ApiLambdaHandler() {
        this.router = DaggerApiComponent.create().router();
    }

    public ApiLambdaHandler(RequestRouter router) {
        this.router = router;
    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context ctx) {
        return router.route(event, ctx);
    }
}
