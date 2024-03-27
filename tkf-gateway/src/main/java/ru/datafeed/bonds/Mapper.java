package ru.datafeed.bonds;

import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import ru.datafeed.model.BondDto;
import ru.datafeed.model.BrandData;
import ru.datafeed.model.MoneyValue;
import ru.datafeed.model.Quotation;
import ru.datafeed.model.RealExchange;
import ru.datafeed.model.RiskLevel;
import ru.datafeed.model.SecurityTradingStatus;
import ru.tinkoff.piapi.contract.v1.Bond;

public class Mapper {
    private Mapper() {}

    public static BondDto toDto(Bond bond) {
        return BondDto.builder()
                .figi(bond.getFigi())
                .ticker(bond.getTicker())
                .classCode(bond.getClassCode())
                .isin(bond.getIsin())
                .lot(bond.getLot())
                .currency(bond.getCurrency())
                .klong(toQt(bond.getKlong()))
                .kshort(toQt(bond.getKshort()))
                .dlong(toQt(bond.getDlong()))
                .dshort(toQt(bond.getDshort()))
                .dlongMin(toQt(bond.getDlongMin()))
                .dshortMin(toQt(bond.getDshortMin()))
                .shortEnabledFlag(bond.getShortEnabledFlag())
                .name(bond.getName())
                .exchange(bond.getExchange())
                .couponQuantityPerYear(bond.getCouponQuantityPerYear())
                .maturityDate(toLdt(bond.getMaturityDate()))
                .nominal(toMv(bond.getNominal()))
                .initialNominal(toMv(bond.getInitialNominal()))
                .stateRegDate(toLdt(bond.getStateRegDate()))
                .placementDate(toLdt(bond.getPlacementDate()))
                .placementPrice(toMv(bond.getPlacementPrice()))
                .aciValue(toMv(bond.getAciValue()))
                .countryOfRisk(bond.getCountryOfRisk())
                .countryOfRiskName(bond.getCountryOfRiskName())
                .sector(bond.getSector())
                .issueKind(bond.getIssueKind())
                .issueSize(bond.getIssueSize())
                .issueSizePlan(bond.getIssueSizePlan())
                .tradingStatus(toSts(bond.getTradingStatus()))
                .otcFlag(bond.getOtcFlag())
                .buyAvailableFlag(bond.getBuyAvailableFlag())
                .sellAvailableFlag(bond.getSellAvailableFlag())
                .floatingCouponFlag(bond.getFloatingCouponFlag())
                .perpetualFlag(bond.getPerpetualFlag())
                .amortizationFlag(bond.getAmortizationFlag())
                .minPriceIncrement(toQt(bond.getMinPriceIncrement()))
                .apiTradeAvailableFlag(bond.getApiTradeAvailableFlag())
                .uid(bond.getUid())
                .realExchange(toRe(bond.getRealExchange()))
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
                .brand(toBd(bond.getBrand()))
                .build();
    }

    private static LocalDateTime toLdt(Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos())
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime();
    }

    private static Quotation toQt(ru.tinkoff.piapi.contract.v1.Quotation quotation) {
        return Quotation.builder()
                .nano(quotation.getNano())
                .units(quotation.getUnits())
                .build();
    }

    private static MoneyValue toMv(ru.tinkoff.piapi.contract.v1.MoneyValue moneyValue) {
        return MoneyValue.builder()
                .currency(moneyValue.getCurrency())
                .units(moneyValue.getUnits())
                .nano(moneyValue.getNano())
                .build();
    }

    private static SecurityTradingStatus toSts(ru.tinkoff.piapi.contract.v1.SecurityTradingStatus status) {
        return SecurityTradingStatus.valueOf(status.name());
    }

    private static RealExchange toRe(ru.tinkoff.piapi.contract.v1.RealExchange re) {
        return RealExchange.valueOf(re.name());
    }

    private static RiskLevel toRL(ru.tinkoff.piapi.contract.v1.RiskLevel rl) {
        return RiskLevel.valueOf(rl.name());
    }

    private static BrandData toBd(ru.tinkoff.piapi.contract.v1.BrandData db) {
        return BrandData.builder()
                .logoName(db.getLogoName())
                .logoBaseColor(db.getLogoBaseColor())
                .textColor(db.getTextColor())
                .build();
    }
}
