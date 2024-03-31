package ru.datafeed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.linecorp.armeria.client.ClientFactory;
import com.linecorp.armeria.client.grpc.GrpcClients;
import com.linecorp.armeria.common.Flags;
import com.linecorp.armeria.common.RequestContext;
import com.linecorp.armeria.common.annotation.Nullable;
import com.linecorp.armeria.common.grpc.GrpcExceptionHandlerFunction;
import io.grpc.Metadata;
import io.grpc.Status;
import io.netty.channel.EventLoopGroup;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.datafeed.bonds.BondsService;
import ru.datafeed.bonds.BondsServiceGrpc;
import ru.datafeed.rest.BondsRest;
import ru.datafeed.rest.WebServer;
import ru.tinkoff.piapi.contract.v1.InstrumentsServiceGrpc;

public class TksGateway {
    private static final Logger log = LoggerFactory.getLogger(TksGateway.class);

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        log.info("start TksGateway");
        new TksGateway().start();

        log.info("started TksGateway. {} ms", (System.currentTimeMillis() - start));
    }

    private void start() {
        var token = Objects.requireNonNull(System.getenv("TKF_SENDBOX"), "env TOKEN is null");
        var url = Objects.requireNonNull(System.getenv("URL"), "env URL is null");
        var port = Integer.parseInt(Objects.requireNonNull(System.getenv("PORT"), "env PORT is null"));
        var webThreads =
                Integer.parseInt(Objects.requireNonNull(System.getenv("WEB_THREADS"), "env WEB_THREADS is null"));
        var grpcThreads =
                Integer.parseInt(Objects.requireNonNull(System.getenv("GRPC_THREADS"), "env GRPC_THREADS is null"));
        log.info("url:{}, port:{}, webThreads:{}, grpcThreads:{}", url, port, webThreads, grpcThreads);

        var jsonMapper = objectMapper();

        var instrumentsService = instrumentsServiceStub(url, token, grpcThreads);
        var bondsService = bondsService(instrumentsService);
        var bondsRest = bondsRest(jsonMapper, bondsService);
        var webServer = webServer(port, webThreads, bondsRest);

        var shutdownHook = new Thread(() -> {
            log.info("closing Application");
            webServer.close();
            bondsService.close();
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    private ObjectMapper objectMapper() {
        var jsonMapper = JsonMapper.builder().build();
        jsonMapper.registerModule(new JavaTimeModule());
        return jsonMapper;
    }

    private BondsRest bondsRest(ObjectMapper jsonMapper, BondsService bondsService) {
        return new BondsRest(jsonMapper, bondsService);
    }

    private InstrumentsServiceGrpc.InstrumentsServiceStub instrumentsServiceStub(
            String url, String token, int grpcThreads) {
        var factory = ClientFactory.builder()
                .workerGroup(makeEventLoopGroup(grpcThreads), true)
                .build();

        return GrpcClients.builder(String.format("gproto+https://%s", url))
                .factory(factory)
                .exceptionHandler(new GrpcExceptionHandlerFunction() {
                    @Override
                    public @Nullable Status apply(
                            @Nonnull RequestContext ctx, @Nonnull Throwable cause, @Nonnull Metadata metadata) {
                        log.error("error. meta:{}", metadata, cause);
                        return Status.ABORTED;
                    }
                })
                .decorator((delegate, ctx, req) -> {
                    ctx.addAdditionalRequestHeader("Authorization", String.format("Bearer %s", token));
                    return delegate.execute(ctx, req);
                })
                .build(InstrumentsServiceGrpc.InstrumentsServiceStub.class);
    }

    private WebServer webServer(int port, int threadNumber, BondsRest bondsRest) {
        return new WebServer(port, threadNumber, bondsRest);
    }

    private BondsService bondsService(InstrumentsServiceGrpc.InstrumentsServiceStub instrumentsService) {
        return new BondsServiceGrpc(instrumentsService);
    }

    private EventLoopGroup makeEventLoopGroup(int nThreads) {
        ThreadFactory threadFactory =
                task -> Thread.ofVirtual().name("armeria-grpc-loop-", 0).unstarted(task);
        var type = Flags.transportType();
        return type.newEventLoopGroup(nThreads, unused -> threadFactory);
    }
}
