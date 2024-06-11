package ru.datafeed.repository;

import java.util.List;
import ru.datafeed.model.BondDto;

public interface BondsRepository {

    int save(List<BondDto> bonds);
}
