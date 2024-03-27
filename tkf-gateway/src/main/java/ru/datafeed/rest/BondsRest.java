package ru.datafeed.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpResponseWriter;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.common.ResponseHeaders;
import com.linecorp.armeria.common.logging.LogLevel;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.annotation.ExceptionHandler;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.server.annotation.Path;
import com.linecorp.armeria.server.annotation.ProducesJsonSequences;
import com.linecorp.armeria.server.annotation.decorator.LoggingDecorator;
import io.netty.channel.EventLoop;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.datafeed.bonds.BondsService;
import ru.datafeed.model.BondDto;

@ExceptionHandler(ExceptionHandlerService.class)
@LoggingDecorator(requestLogLevel = LogLevel.INFO, successfulResponseLogLevel = LogLevel.INFO)
public class BondsRest {
    private static final Logger log = LoggerFactory.getLogger(BondsRest.class);

    private final ObjectMapper objectMapper;
    private final BondsService bondsService;

    public BondsRest(ObjectMapper objectMapper, BondsService bondsService) {
        this.objectMapper = objectMapper;
        this.bondsService = bondsService;
    }

    @Get
    @Path("/")
    @Path("")
    @ProducesJsonSequences
    public HttpResponseWriter bonds(@Nonnull ServiceRequestContext ctx, @Nonnull HttpRequest req) {
        var startTime = System.currentTimeMillis();
        log.info("request for bonds list");
        HttpResponseWriter response = HttpResponse.streaming();

        bondsService.getBonds(bonds -> {
            log.info("response from grpc, size:{}, took:{} ms", bonds.size(), System.currentTimeMillis() - startTime);
            response.write(ResponseHeaders.builder(HttpStatus.OK)
                    .contentType(MediaType.JSON_SEQ)
                    .build());
            response.whenConsumed().thenRun(() -> streamData(startTime, ctx.eventLoop(), response, bonds, 0));
        });
        return response;
    }

    private void streamData(
            long startTime, EventLoop executor, HttpResponseWriter response, List<BondDto> bonds, int idx) {
        if (idx < bonds.size()) {
            var json = toJson(bonds.get(idx));
            if (json != null) {
                response.write(HttpData.ofUtf8(json));
            }
            response.whenConsumed()
                    .thenRun(() -> executor.schedule(
                            () -> streamData(startTime, executor, response, bonds, idx + 1), 1, TimeUnit.MILLISECONDS));

        } else {
            response.close();
            log.info("done, took total:{} ms", System.currentTimeMillis() - startTime);
        }
    }

    private <T> String toJson(T data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            log.error("write json error", ex);
            return null;
        }
    }
}
