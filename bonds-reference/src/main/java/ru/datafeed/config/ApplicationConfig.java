package ru.datafeed.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.linecorp.armeria.client.ClientFactory;
import com.linecorp.armeria.client.grpc.GrpcClients;
import com.linecorp.armeria.common.Flags;
import com.linecorp.armeria.common.RequestContext;
import com.linecorp.armeria.common.annotation.Nullable;
import com.linecorp.armeria.common.grpc.GrpcExceptionHandlerFunction;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.grpc.Metadata;
import io.grpc.Status;
import io.netty.channel.EventLoopGroup;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import javax.annotation.Nonnull;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.datafeed.bonds.BondsInfoSourceService;
import ru.datafeed.bonds.BondsInfoSourceServiceGrpc;
import ru.datafeed.repository.BondsRepository;
import ru.datafeed.repository.BondsRepositoryPg;
import ru.datafeed.repository.DateTimeProvider;
import ru.datafeed.rest.BondsRest;
import ru.datafeed.rest.WebServer;
import ru.tinkoff.piapi.contract.v1.InstrumentsServiceGrpc;

@Configuration
public class ApplicationConfig {
    private static final Logger log = LoggerFactory.getLogger(ApplicationConfig.class);

    @Bean(destroyMethod = "close")
    public DataSource dataSource(
            @Value("${data-source.url}") String url,
            @Value("${data-source.user}") String user,
            @Value("${data-source.pwd}") String pwd) {
        var config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setConnectionTimeout(3000); // ms
        config.setIdleTimeout(60000); // ms
        config.setMaxLifetime(600000); // ms
        config.setAutoCommit(false);
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(10);
        config.setPoolName("DemoHiPool");
        config.setRegisterMbeans(true);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        config.setUsername(user);
        config.setPassword(pwd);

        return new HikariDataSource(config);
    }

    @Bean
    public Flyway flyway(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("db migration finished.");
        return flyway;
    }

    @Bean
    public ObjectMapper objectMapper() {
        var jsonMapper = JsonMapper.builder().build();
        jsonMapper.registerModule(new JavaTimeModule());
        return jsonMapper;
    }

    @Bean(name = "grpcEventLoop", destroyMethod = "close")
    public EventLoopGroup grpcEventLoop(@Value("${tkf.thread-no}") int threadNumber) {
        log.info("grpcThreads:{}", threadNumber);

        ThreadFactory threadFactory =
                task -> Thread.ofVirtual().name("armeria-grpc-loop-", 0).unstarted(task);
        var type = Flags.transportType();
        return type.newEventLoopGroup(threadNumber, unused -> threadFactory);
    }

    @Bean(name = "webEventLoop", destroyMethod = "close")
    public EventLoopGroup webEventLoop(@Value("${webserver.thread-no}") int threadNumber) {
        ThreadFactory threadFactory =
                task -> Thread.ofVirtual().name("armeria-loop-", 0).unstarted(task);
        var type = Flags.transportType();
        return type.newEventLoopGroup(threadNumber, unused -> threadFactory);
    }

    @Bean
    public InstrumentsServiceGrpc.InstrumentsServiceStub instrumentsServiceStub(
            @Qualifier("grpcEventLoop") EventLoopGroup grpcEventLoop, @Value("${tkf.url}") String tkfUrl) {
        var factory = ClientFactory.builder().workerGroup(grpcEventLoop, true).build();
        var token = Objects.requireNonNull(System.getenv("TKF_SENDBOX"), "env TOKEN is null");
        log.info("tkfUrl:{}", tkfUrl);

        return GrpcClients.builder(String.format("gproto+https://%s", tkfUrl))
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

    @Bean(destroyMethod = "close")
    public BondsInfoSourceService bondsService(InstrumentsServiceGrpc.InstrumentsServiceStub instrumentsService) {
        return new BondsInfoSourceServiceGrpc(instrumentsService);
    }

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return LocalDateTime::now;
    }

    @Bean
    public BondsRepository bondsRepository(DataSource dataSource, DateTimeProvider dateTimeProvider) {
        return new BondsRepositoryPg(dataSource, dateTimeProvider);
    }

    @Bean
    public BondsRest bondsRest(
            ObjectMapper jsonMapper, BondsInfoSourceService bondsInfoSourceService, BondsRepository bondsRepository) {
        return new BondsRest(jsonMapper, bondsInfoSourceService, bondsRepository);
    }

    @Bean(destroyMethod = "close")
    public WebServer webServer(
            @Value("${webserver.port}") int port,
            @Qualifier("webEventLoop") EventLoopGroup webEventLoop,
            BondsRest bondsRest) {
        return new WebServer(port, webEventLoop, bondsRest);
    }
}
