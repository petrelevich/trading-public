package ru.datafeed.rest;

import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.annotation.ExceptionHandlerFunction;
import javax.annotation.Nonnull;

public class ExceptionHandlerService implements ExceptionHandlerFunction {
    @Override
    @Nonnull
    public HttpResponse handleException(
            @Nonnull ServiceRequestContext ctx, @Nonnull HttpRequest req, @Nonnull Throwable cause) {
        if (cause instanceof GatewayException) {
            return HttpResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .content("Application failed, will be fixed")
                    .build();
        }
        return ExceptionHandlerFunction.fallthrough();
    }
}
