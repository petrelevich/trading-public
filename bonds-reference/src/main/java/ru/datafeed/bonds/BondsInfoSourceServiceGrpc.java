package ru.datafeed.bonds;

import static ru.tinkoff.piapi.contract.v1.RealExchange.REAL_EXCHANGE_MOEX;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tinkoff.piapi.contract.v1.BondsResponse;
import ru.tinkoff.piapi.contract.v1.InstrumentStatus;
import ru.tinkoff.piapi.contract.v1.InstrumentsRequest;
import ru.tinkoff.piapi.contract.v1.InstrumentsServiceGrpc;

public class BondsInfoSourceServiceGrpc implements BondsInfoSourceService {
    private static final Logger log = LoggerFactory.getLogger(BondsInfoSourceServiceGrpc.class);

    private final InstrumentsServiceGrpc.InstrumentsServiceStub instrumentsService;

    public BondsInfoSourceServiceGrpc(InstrumentsServiceGrpc.InstrumentsServiceStub instrumentsService) {
        this.instrumentsService = instrumentsService;
    }

    @Override
    public void getBonds(BondsConsumer bondsConsumers) {
        var instrumentsRequest = InstrumentsRequest.newBuilder()
                .setInstrumentStatus(InstrumentStatus.INSTRUMENT_STATUS_ALL)
                .build();

        instrumentsService.bonds(instrumentsRequest, new io.grpc.stub.StreamObserver<>() {
            @Override
            public void onNext(BondsResponse bondsResponse) {
                var bonds = bondsResponse.getInstrumentsList().stream()
                        .filter(bond -> bond.getCountryOfRisk().equals("RU"))
                        .filter(bond -> !bond.getOtcFlag())
                        .filter(bond -> bond.getRealExchange().equals(REAL_EXCHANGE_MOEX))
                        .map(bond -> {
                            log.info("bond.name:{}", bond.getName());
                            log.debug("bond:{}", bond);
                            return Mapper.toDto(bond);
                        })
                        .filter(Objects::nonNull)
                        .toList();
                bondsConsumers.accept(bonds);
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("error", throwable);
            }

            @Override
            public void onCompleted() {
                log.info("completed");
            }
        });
    }

    @Override
    public void close() {
        log.info("BondsServiceGrpc closed");
    }
}
