package ru.datafeed;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.linecorp.armeria.client.grpc.GrpcClients;

import java.util.Objects;

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

        log.info("url:{}, port:{}", url, port);

        var jsonMapper = JsonMapper.builder().build();
        jsonMapper.registerModule(new JavaTimeModule());

        var instrumentsService = instrumentsServiceStub(url, token);
        var bondsService = bondsService(instrumentsService);
        var bondsRest = new BondsRest(jsonMapper, bondsService);
        var webServer = webServer(port, bondsRest);

        var shutdownHook = new Thread(() -> {
            log.info("closing Application");
            webServer.close();
            bondsService.close();
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    private InstrumentsServiceGrpc.InstrumentsServiceStub instrumentsServiceStub(String url, String token) {
        return GrpcClients.builder(String.format("gproto+https://%s", url))
                .decorator((delegate, ctx, req) -> {
                    ctx.addAdditionalRequestHeader("Authorization", String.format("Bearer %s", token));
                    return delegate.execute(ctx, req);
                })
                .build(InstrumentsServiceGrpc.InstrumentsServiceStub.class);
    }

    private WebServer webServer(int port, BondsRest bondsRest) {
        return new WebServer(port, bondsRest);
    }

    private BondsService bondsService(InstrumentsServiceGrpc.InstrumentsServiceStub instrumentsService) {
        return new BondsServiceGrpc(instrumentsService);
    }
}
