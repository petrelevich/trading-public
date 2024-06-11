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
import com.linecorp.armeria.server.annotation.Path;
import com.linecorp.armeria.server.annotation.ProducesJsonSequences;
import com.linecorp.armeria.server.annotation.Put;
import com.linecorp.armeria.server.annotation.decorator.LoggingDecorator;
import io.netty.channel.EventLoop;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.datafeed.bonds.BondsInfoSourceService;
import ru.datafeed.model.SavedBondsReport;
import ru.datafeed.repository.BondsRepository;

@ExceptionHandler(ExceptionHandlerService.class)
@LoggingDecorator(requestLogLevel = LogLevel.INFO, successfulResponseLogLevel = LogLevel.INFO)
public class BondsRest {
    private static final Logger log = LoggerFactory.getLogger(BondsRest.class);

    private final ObjectMapper objectMapper;
    private final BondsInfoSourceService bondsInfoSourceService;
    private final BondsRepository bondsRepository;

    public BondsRest(
            ObjectMapper objectMapper, BondsInfoSourceService bondsInfoSourceService, BondsRepository bondsRepository) {
        this.objectMapper = objectMapper;
        this.bondsInfoSourceService = bondsInfoSourceService;
        this.bondsRepository = bondsRepository;
    }

    @Put
    @Path("/load")
    @ProducesJsonSequences
    public HttpResponse bonds(@Nonnull ServiceRequestContext ctx, @Nonnull HttpRequest req) {
        var startTime = System.currentTimeMillis();
        log.info("request for bonds list");
        HttpResponseWriter response = HttpResponse.streaming();

        bondsInfoSourceService.getBonds(bonds -> {
            log.info("response from grpc, size:{}, took:{} ms", bonds.size(), System.currentTimeMillis() - startTime);
            var savedCounter = bondsRepository.save(bonds);
            response.write(ResponseHeaders.builder(HttpStatus.OK)
                    .contentType(MediaType.JSON)
                    .build());
            response.whenConsumed()
                    .thenRun(() -> streamData(
                            startTime, ctx.eventLoop(), response, new SavedBondsReport(bonds.size(), savedCounter), 0));
        });
        return response;
    }

    private void streamData(
            long startTime,
            EventLoop executor,
            HttpResponseWriter response,
            SavedBondsReport savedBondsReport,
            int idx) {
        if (idx < 1) {
            var json = toJson(savedBondsReport);
            if (json != null) {
                response.write(HttpData.ofUtf8(json));
            }
            response.whenConsumed()
                    .thenRun(() -> executor.schedule(
                            () -> streamData(startTime, executor, response, savedBondsReport, idx + 1),
                            1,
                            TimeUnit.MILLISECONDS));
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
