package ru.datafeed.bonds;

import java.util.List;
import java.util.function.Consumer;
import ru.datafeed.model.BondDto;

public interface BondsConsumer extends Consumer<List<BondDto>> {}
