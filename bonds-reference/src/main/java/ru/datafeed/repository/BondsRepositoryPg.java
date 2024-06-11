package ru.datafeed.repository;

import java.sql.BatchUpdateException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.datafeed.model.BondDto;

public class BondsRepositoryPg implements BondsRepository {
    private static final Logger log = LoggerFactory.getLogger(BondsRepositoryPg.class);
    private static final int BATCH_SIZE = 1000;
    private static final String INSERT_SQL =
            """
                      INSERT INTO bonds (isin,figi,ticker,class_code,lot,currency,klong_units,klong_nano,kshort_units,kshort_nano,dlong_units,dlong_nano,
                             dshort_units,dshort_nano,dlongMin_units,dlongMin_nano,dshort_min_units,dshort_min_nano,short_enabled_flag,name,exchange,
                             coupon_quantity_per_year,maturity_date,nominal_currency,nominal_units,nominal_nano,initial_nominalCurrency,
                             initial_nominal_units,initial_nominal_nano,state_reg_date,placement_date,placement_price_currency,placement_price_units,
                             placement_price_nano,aciValue_currency ,aciValue_units,aciValue_nano,sector,issue_kind,issue_size,issue_size_plan,
                             trading_status,buy_available_flag,sell_available_flag,floating_coupon_flag,perpetual_flag,amortization_flag,
                             min_price_increment_units,min_price_increment_nano,api_trade_available_flag,uid,position_uid,asset_uid,for_iis_flag,
                             for_qual_investor_flag,weekend_flag,blocked_tca_flag,subordinated_flag,liquidity_flag,first1min_candle_date,
                             first1day_candle_date,risk_level, created_at, updated_at)
                      VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                      ON CONFLICT (isin) DO UPDATE SET
                             figi = EXCLUDED.figi,
                             ticker = EXCLUDED.ticker,
                             class_code = EXCLUDED.class_code,
                             lot = EXCLUDED.lot,
                             currency = EXCLUDED.currency,
                             klong_units = EXCLUDED.klong_units,
                             klong_nano = EXCLUDED.klong_nano,
                             kshort_units = EXCLUDED.kshort_units,
                             kshort_nano = EXCLUDED.kshort_nano,
                             dlong_units = EXCLUDED.kshort_nano,
                             dlong_nano = EXCLUDED.dlong_nano,
                             dshort_units = EXCLUDED.dshort_units,
                             dshort_nano = EXCLUDED.dshort_nano,
                             dlongMin_units = EXCLUDED.dlongMin_units,
                             dlongMin_nano = EXCLUDED.dlongMin_nano,
                             dshort_min_units = EXCLUDED.dshort_min_units,
                             dshort_min_nano = EXCLUDED.dshort_min_nano,
                             short_enabled_flag = EXCLUDED.short_enabled_flag,
                             name = EXCLUDED.name,
                             exchange = EXCLUDED.exchange,
                             coupon_quantity_per_year = EXCLUDED.coupon_quantity_per_year,
                             maturity_date = EXCLUDED.maturity_date,
                             nominal_currency = EXCLUDED.nominal_currency,
                             nominal_units = EXCLUDED.nominal_units,
                             nominal_nano = EXCLUDED.nominal_nano,
                             initial_nominalCurrency = EXCLUDED.initial_nominalCurrency,
                             initial_nominal_units = EXCLUDED.initial_nominal_units,
                             initial_nominal_nano = EXCLUDED.initial_nominal_nano,
                             state_reg_date = EXCLUDED.state_reg_date,
                             placement_date = EXCLUDED.placement_date,
                             placement_price_currency = EXCLUDED.placement_price_currency,
                             placement_price_units = EXCLUDED.placement_price_units,
                             placement_price_nano = EXCLUDED.placement_price_nano,
                             aciValue_currency  = EXCLUDED.aciValue_currency,
                             aciValue_units = EXCLUDED.aciValue_units,
                             aciValue_nano = EXCLUDED.aciValue_nano,
                             sector = EXCLUDED.sector,
                             issue_kind = EXCLUDED.issue_kind,
                             issue_size = EXCLUDED.issue_size,
                             issue_size_plan = EXCLUDED.issue_size_plan,
                             trading_status = EXCLUDED.trading_status,
                             buy_available_flag = EXCLUDED.buy_available_flag,
                             sell_available_flag = EXCLUDED.sell_available_flag,
                             floating_coupon_flag = EXCLUDED.floating_coupon_flag,
                             perpetual_flag = EXCLUDED.perpetual_flag,
                             amortization_flag = EXCLUDED.amortization_flag,
                             min_price_increment_units = EXCLUDED.min_price_increment_units,
                             min_price_increment_nano = EXCLUDED.min_price_increment_nano,
                             api_trade_available_flag = EXCLUDED.api_trade_available_flag,
                             uid = EXCLUDED.uid,
                             position_uid = EXCLUDED.position_uid,
                             asset_uid = EXCLUDED.asset_uid,
                             for_iis_flag = EXCLUDED.for_iis_flag,
                             for_qual_investor_flag = EXCLUDED.for_qual_investor_flag,
                             weekend_flag = EXCLUDED.weekend_flag,
                             blocked_tca_flag = EXCLUDED.blocked_tca_flag,
                             subordinated_flag = EXCLUDED.subordinated_flag,
                             liquidity_flag = EXCLUDED.liquidity_flag,
                             first1min_candle_date = EXCLUDED.first1min_candle_date,
                             first1day_candle_date = EXCLUDED.first1day_candle_date,
                             risk_level = EXCLUDED.risk_level,
                             updated_at = EXCLUDED.updated_at
                    """;

    private final DataSource dataSource;
    private final DateTimeProvider dateTimeProvider;

    public BondsRepositoryPg(DataSource dataSource, DateTimeProvider dateTimeProvider) {
        this.dataSource = dataSource;
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public int save(List<BondDto> bonds) {
        var results = new ArrayList<Integer>();
        try {
            try (var connection = dataSource.getConnection()) {
                try (var ps = connection.prepareStatement(INSERT_SQL)) {
                    var currentBatchSize = 0;
                    for (int recIdx = 0; recIdx < bonds.size(); recIdx++) {
                        var idx = 1;
                        ps.setString(idx++, bonds.get(recIdx).isin());
                        ps.setString(idx++, bonds.get(recIdx).figi());
                        ps.setString(idx++, bonds.get(recIdx).ticker());
                        ps.setString(idx++, bonds.get(recIdx).classCode());
                        ps.setInt(idx++, bonds.get(recIdx).lot());
                        ps.setObject(idx++, bonds.get(recIdx).currency(), Types.OTHER);
                        ps.setLong(idx++, bonds.get(recIdx).klongUnits());
                        ps.setInt(idx++, bonds.get(recIdx).klongNano());
                        ps.setLong(idx++, bonds.get(recIdx).kshortUnits());
                        ps.setInt(idx++, bonds.get(recIdx).kshortNano());
                        ps.setLong(idx++, bonds.get(recIdx).dlongUnits());
                        ps.setInt(idx++, bonds.get(recIdx).dlongNano());
                        ps.setLong(idx++, bonds.get(recIdx).dshortUnits());
                        ps.setInt(idx++, bonds.get(recIdx).dshortNano());
                        ps.setLong(idx++, bonds.get(recIdx).dlongMinUnits());
                        ps.setInt(idx++, bonds.get(recIdx).dlongMinNano());
                        ps.setLong(idx++, bonds.get(recIdx).dshortMinUnits());
                        ps.setInt(idx++, bonds.get(recIdx).dshortMinNano());
                        ps.setBoolean(idx++, bonds.get(recIdx).shortEnabledFlag());
                        ps.setString(idx++, bonds.get(recIdx).name());
                        ps.setString(idx++, bonds.get(recIdx).exchange());
                        ps.setInt(idx++, bonds.get(recIdx).couponQuantityPerYear());
                        ps.setTimestamp(idx++, ts(bonds.get(recIdx).maturityDate()));
                        ps.setObject(idx++, bonds.get(recIdx).nominalCurrency(), Types.OTHER);
                        ps.setLong(idx++, bonds.get(recIdx).nominalUnits());
                        ps.setInt(idx++, bonds.get(recIdx).nominalNano());
                        ps.setObject(idx++, bonds.get(recIdx).initialNominalCurrency(), Types.OTHER);
                        ps.setLong(idx++, bonds.get(recIdx).initialNominalUnits());
                        ps.setInt(idx++, bonds.get(recIdx).initialNominalNano());
                        ps.setTimestamp(idx++, ts(bonds.get(recIdx).stateRegDate()));
                        ps.setTimestamp(idx++, ts(bonds.get(recIdx).placementDate()));
                        ps.setObject(idx++, bonds.get(recIdx).placementPriceCurrency(), Types.OTHER);
                        ps.setLong(idx++, bonds.get(recIdx).placementPriceUnits());
                        ps.setInt(idx++, bonds.get(recIdx).placementPriceNano());
                        ps.setObject(idx++, bonds.get(recIdx).aciValueCurrency(), Types.OTHER);
                        ps.setLong(idx++, bonds.get(recIdx).aciValueUnits());
                        ps.setInt(idx++, bonds.get(recIdx).aciValueNano());
                        ps.setString(idx++, bonds.get(recIdx).sector());
                        ps.setString(idx++, bonds.get(recIdx).issueKind());
                        ps.setLong(idx++, bonds.get(recIdx).issueSize());
                        ps.setLong(idx++, bonds.get(recIdx).issueSizePlan());
                        ps.setObject(idx++, bonds.get(recIdx).tradingStatus(), Types.OTHER);
                        ps.setBoolean(idx++, bonds.get(recIdx).buyAvailableFlag());
                        ps.setBoolean(idx++, bonds.get(recIdx).sellAvailableFlag());
                        ps.setBoolean(idx++, bonds.get(recIdx).floatingCouponFlag());
                        ps.setBoolean(idx++, bonds.get(recIdx).perpetualFlag());
                        ps.setBoolean(idx++, bonds.get(recIdx).amortizationFlag());
                        ps.setLong(idx++, bonds.get(recIdx).minPriceIncrementUnits());
                        ps.setInt(idx++, bonds.get(recIdx).minPriceIncrementNano());
                        ps.setBoolean(idx++, bonds.get(recIdx).apiTradeAvailableFlag());
                        ps.setString(idx++, bonds.get(recIdx).uid());
                        ps.setString(idx++, bonds.get(recIdx).positionUid());
                        ps.setString(idx++, bonds.get(recIdx).assetUid());
                        ps.setBoolean(idx++, bonds.get(recIdx).forIisFlag());
                        ps.setBoolean(idx++, bonds.get(recIdx).forQualInvestorFlag());
                        ps.setBoolean(idx++, bonds.get(recIdx).weekendFlag());
                        ps.setBoolean(idx++, bonds.get(recIdx).blockedTcaFlag());
                        ps.setBoolean(idx++, bonds.get(recIdx).subordinatedFlag());
                        ps.setBoolean(idx++, bonds.get(recIdx).liquidityFlag());
                        ps.setTimestamp(idx++, ts(bonds.get(recIdx).first1minCandleDate()));
                        ps.setTimestamp(idx++, ts(bonds.get(recIdx).first1dayCandleDate()));
                        ps.setObject(idx++, bonds.get(recIdx).riskLevel(), Types.OTHER);
                        ps.setTimestamp(idx++, ts(dateTimeProvider.now()));
                        ps.setTimestamp(idx, ts(dateTimeProvider.now()));

                        ps.addBatch();
                        currentBatchSize++;
                        if (recIdx % BATCH_SIZE == 0) {
                            var batchResult = ps.executeBatch();
                            results.addAll(Arrays.stream(batchResult).boxed().toList());
                            log.debug("save batch, recIdx:{}, currentBatchSize:{}", recIdx, currentBatchSize);
                            currentBatchSize = 0;
                        }
                    }
                    if (currentBatchSize != 0) {
                        var batchResult = ps.executeBatch();
                        results.addAll(Arrays.stream(batchResult).boxed().toList());
                        log.debug("save batch, last");
                    }
                }
                connection.commit();
            }
        } catch (BatchUpdateException ex) {
            log.error(
                    "BatchUpdateException, exec report, size:{} details:{}",
                    ex.getLargeUpdateCounts().length,
                    ex.getLargeUpdateCounts(),
                    ex);
            log.error("BatchUpdateException, other", ex.getNextException());
        } catch (Exception ex) {
            log.error("save exception", ex);
        }
        log.info("results:{}", results);
        return results.stream().filter(code -> code > 0).reduce(0, Integer::sum);
    }

    private java.sql.Timestamp ts(LocalDateTime ldt) {
        return Timestamp.valueOf(ldt);
    }
}
