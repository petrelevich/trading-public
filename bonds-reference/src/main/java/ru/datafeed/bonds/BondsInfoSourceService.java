package ru.datafeed.bonds;

public interface BondsInfoSourceService {

    void getBonds(BondsConsumer bondsConsumers);

    void close();
}
