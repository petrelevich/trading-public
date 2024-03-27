package ru.datafeed.rest;

import com.linecorp.armeria.common.Flags;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.docs.DocServiceFilter;
import com.linecorp.armeria.server.healthcheck.HealthCheckService;
import com.linecorp.armeria.server.logging.LoggingService;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmCompilationMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmInfoMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.netty.channel.EventLoopGroup;
import java.util.concurrent.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {
    private static final Logger log = LoggerFactory.getLogger(WebServer.class);
    private final Server server;
    private final BondsRest bondsRest;
    private final JvmGcMetrics jvmGcMetrics;
    private final JvmHeapPressureMetrics jvmHeapPressureMetrics;

    public WebServer(int port, int threadNumber, BondsRest bondsRest) {
        jvmGcMetrics = new JvmGcMetrics();
        jvmHeapPressureMetrics = new JvmHeapPressureMetrics();
        this.bondsRest = bondsRest;
        server = newServer(port, threadNumber);
        server.start().join();
        log.info("Server has been started. Serving DocService at http://127.0.0.1:{}/docs", server.activeLocalPort());
    }

    public void close() {
        jvmGcMetrics.close();
        jvmHeapPressureMetrics.close();
        server.close();
        log.info("webServer closed");
    }

    private Server newServer(int port, int threadNumber) {
        var prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        initMetrics(prometheusMeterRegistry);

        var sb = Server.builder();
        sb.http(port)
                .workerGroup(makeEventLoopGroup(threadNumber), true)
                .requestTimeoutMillis(20_000)
                .decorator(LoggingService.newDecorator())
                .service("/health", HealthCheckService.builder().build())
                .service("/metrics", (ctx, req) -> HttpResponse.of(prometheusMeterRegistry.scrape()));

        configureServices(sb);
        return sb.build();
    }

    private void configureServices(ServerBuilder sb) {
        sb.annotatedService("/bonds", bondsRest)
                .serviceUnder(
                        "/docs",
                        DocService.builder()
                                .include(DocServiceFilter.ofAnnotated())
                                .build());
    }

    private void initMetrics(PrometheusMeterRegistry prometheusMeterRegistry) {
        Metrics.addRegistry(prometheusMeterRegistry);
        new ClassLoaderMetrics().bindTo(prometheusMeterRegistry);
        new JvmMemoryMetrics().bindTo(prometheusMeterRegistry);
        jvmGcMetrics.bindTo(prometheusMeterRegistry);
        jvmHeapPressureMetrics.bindTo(prometheusMeterRegistry);
        new JvmCompilationMetrics().bindTo(prometheusMeterRegistry);
        new JvmInfoMetrics().bindTo(prometheusMeterRegistry);
        new ProcessorMetrics().bindTo(prometheusMeterRegistry);
        new JvmThreadMetrics().bindTo(prometheusMeterRegistry);
        new JvmThreadMetrics().bindTo(prometheusMeterRegistry);
        new ProcessorMetrics().bindTo(prometheusMeterRegistry);
        new UptimeMetrics().bindTo(prometheusMeterRegistry);
    }

    private EventLoopGroup makeEventLoopGroup(int nThreads) {
        ThreadFactory threadFactory =
                task -> Thread.ofVirtual().name("armeria-loop-", 0).unstarted(task);
        var type = Flags.transportType();
        return type.newEventLoopGroup(nThreads, unused -> threadFactory);
    }
}
