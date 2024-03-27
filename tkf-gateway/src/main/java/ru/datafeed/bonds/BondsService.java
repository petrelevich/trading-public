package ru.datafeed.bonds;

public interface BondsService {

    void getBonds(BondsConsumer bondsConsumers);

    void close();
}
