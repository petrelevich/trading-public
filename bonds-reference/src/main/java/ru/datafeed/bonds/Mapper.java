package ru.datafeed.bonds;

import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.datafeed.model.BondDto;
import ru.datafeed.model.Currency;
import ru.datafeed.model.RiskLevel;
import ru.datafeed.model.SecurityTradingStatus;
import ru.tinkoff.piapi.contract.v1.Bond;

public class Mapper {
    private static final Logger log = LoggerFactory.getLogger(Mapper.class);

    private Mapper() {}

    public static BondDto toDto(Bond bond) {

        try {
            return BondDto.builder()
                    .figi(bond.getFigi())
                    .ticker(bond.getTicker())
                    .classCode(bond.getClassCode())
                    .isin(bond.getIsin())
                    .lot(bond.getLot())
                    .currency(Currency.get(bond.getCurrency()))
                    .klongUnits(bond.getKlong().getUnits())
                    .klongNano(bond.getKlong().getNano())
                    .kshortUnits(bond.getKshort().getUnits())
                    .kshortNano(bond.getKshort().getNano())
                    .dlongUnits(bond.getDlong().getUnits())
                    .dlongNano(bond.getDlong().getNano())
                    .dshortUnits(bond.getDshort().getUnits())
                    .dshortNano(bond.getDshort().getNano())
                    .dlongMinUnits(bond.getDlongMin().getUnits())
                    .dlongMinNano(bond.getDlongMin().getNano())
                    .dshortMinUnits(bond.getDshortMin().getUnits())
                    .dshortMinNano(bond.getDshortMin().getNano())
                    .shortEnabledFlag(bond.getShortEnabledFlag())
                    .name(bond.getName())
                    .exchange(bond.getExchange())
                    .couponQuantityPerYear(bond.getCouponQuantityPerYear())
                    .maturityDate(toLdt(bond.getMaturityDate()))
                    .nominalCurrency(Currency.get(bond.getNominal().getCurrency()))
                    .nominalUnits(bond.getNominal().getUnits())
                    .nominalNano(bond.getNominal().getNano())
                    .initialNominalCurrency(
                            Currency.get(bond.getInitialNominal().getCurrency()))
                    .initialNominalUnits(bond.getInitialNominal().getUnits())
                    .initialNominalNano(bond.getInitialNominal().getNano())
                    .stateRegDate(toLdt(bond.getStateRegDate()))
                    .placementDate(toLdt(bond.getPlacementDate()))
                    .placementPriceCurrency(
                            Currency.get(bond.getPlacementPrice().getCurrency()))
                    .placementPriceUnits(bond.getPlacementPrice().getUnits())
                    .placementPriceNano(bond.getPlacementPrice().getNano())
                    .aciValueCurrency(Currency.get(bond.getAciValue().getCurrency()))
                    .aciValueUnits(bond.getAciValue().getUnits())
                    .aciValueNano(bond.getAciValue().getNano())
                    .sector(bond.getSector())
                    .issueKind(bond.getIssueKind())
                    .issueSize(bond.getIssueSize())
                    .issueSizePlan(bond.getIssueSizePlan())
                    .tradingStatus(toSts(bond.getTradingStatus()))
                    .buyAvailableFlag(bond.getBuyAvailableFlag())
                    .sellAvailableFlag(bond.getSellAvailableFlag())
                    .floatingCouponFlag(bond.getFloatingCouponFlag())
                    .perpetualFlag(bond.getPerpetualFlag())
                    .amortizationFlag(bond.getAmortizationFlag())
                    .minPriceIncrementUnits(bond.getMinPriceIncrement().getUnits())
                    .minPriceIncrementNano(bond.getMinPriceIncrement().getNano())
                    .apiTradeAvailableFlag(bond.getApiTradeAvailableFlag())
                    .uid(bond.getUid())
                    .positionUid(bond.getPositionUid())
                    .assetUid(bond.getAssetUid())
                    .forIisFlag(bond.getForIisFlag())
                    .forQualInvestorFlag(bond.getForQualInvestorFlag())
                    .weekendFlag(bond.getWeekendFlag())
                    .blockedTcaFlag(bond.getBlockedTcaFlag())
                    .subordinatedFlag(bond.getSubordinatedFlag())
                    .liquidityFlag(bond.getLiquidityFlag())
                    .first1minCandleDate(toLdt(bond.getFirst1MinCandleDate()))
                    .first1dayCandleDate(toLdt(bond.getFirst1DayCandleDate()))
                    .riskLevel(toRL(bond.getRiskLevel()))
                    .build();
        } catch (Exception ex) {
            log.error("error bond converting, bond:{}", bond, ex);
            return null;
        }
    }

    private static LocalDateTime toLdt(Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos())
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime();
    }

    private static SecurityTradingStatus toSts(ru.tinkoff.piapi.contract.v1.SecurityTradingStatus status) {
        return SecurityTradingStatus.valueOf(status.name());
    }

    private static RiskLevel toRL(ru.tinkoff.piapi.contract.v1.RiskLevel rl) {
        return RiskLevel.valueOf(rl.name());
    }
}
