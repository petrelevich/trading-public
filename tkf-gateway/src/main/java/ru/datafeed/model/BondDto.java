package ru.datafeed.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BondDto {
    String figi; // Figi-идентификатор инструмента.
    String ticker; // Тикер инструмента.
    String classCode; // Класс-код (секция торгов).
    String isin; // Isin-идентификатор инструмента.
    int lot; // Лотность инструмента. Возможно совершение операций только на количества ценной бумаги, кратные
    // параметру *lot*. Подробнее: [лот](https://russianinvestments.github.io/investAPI/glossary#lot)
    String currency; // Валюта расчётов.

    Quotation klong; // Коэффициент ставки риска длинной позиции по клиенту. 2 – клиент со стандартным уровнем риска
    // (КСУР). 1 – клиент с повышенным уровнем риска (КПУР)
    Quotation kshort; // Коэффициент ставки риска короткой позиции по клиенту. 2 – клиент со стандартным уровнем риска
    // (КСУР). 1 – клиент с повышенным уровнем риска (КПУР)
    Quotation dlong; // Ставка риска начальной маржи для КСУР лонг. Подробнее: [ставка риска в
    // лонг](https://help.tinkoff.ru/margin-trade/long/risk-rate/)
    Quotation dshort; // Ставка риска начальной маржи для КСУР шорт. Подробнее: [ставка риска в
    // шорт](https://help.tinkoff.ru/margin-trade/short/risk-rate/)
    Quotation dlongMin; // Ставка риска начальной маржи для КПУР лонг. Подробнее: [ставка риска в
    // лонг](https://help.tinkoff.ru/margin-trade/long/risk-rate/)
    Quotation dshortMin; // Ставка риска начальной маржи для КПУР шорт. Подробнее: [ставка риска в
    // шорт](https://help.tinkoff.ru/margin-trade/short/risk-rate/)
    boolean shortEnabledFlag; // Признак доступности для операций в шорт.
    String name; // Название инструмента.
    String exchange; // Tорговая площадка (секция биржи).

    int couponQuantityPerYear; // Количество выплат по купонам в год.
    LocalDateTime maturityDate; // Дата погашения облигации в часовом поясе UTC.
    MoneyValue nominal; // Номинал облигации.
    MoneyValue initialNominal; // Первоначальный номинал облигации.

    LocalDateTime stateRegDate; // Дата выпуска облигации в часовом поясе UTC.
    LocalDateTime placementDate; // Дата размещения в часовом поясе UTC.
    MoneyValue placementPrice; // Цена размещения.
    MoneyValue aciValue; // Значение НКД (накопленного купонного дохода) на дату.

    String countryOfRisk; // Код страны риска, т.е. страны, в которой компания ведёт основной бизнес.
    String countryOfRiskName; // Наименование страны риска, т.е. страны, в которой компания ведёт основной бизнес.
    String sector; // Сектор экономики.
    String issueKind; // Форма выпуска: **documentary** — документарная **non_documentary** — бездокументарная.
    long issueSize; // Размер выпуска.
    long issueSizePlan; // Плановый размер выпуска.

    SecurityTradingStatus tradingStatus; // Текущий режим торгов инструмента.
    boolean otcFlag; // Признак внебиржевой ценной бумаги.
    boolean buyAvailableFlag; // Признак доступности для покупки.
    boolean sellAvailableFlag; // Признак доступности для продажи.
    boolean floatingCouponFlag; // Признак облигации с плавающим купоном.
    boolean perpetualFlag; // Признак бессрочной облигации.
    boolean amortizationFlag; // Признак облигации с амортизацией долга.
    Quotation minPriceIncrement; // Шаг цены.
    boolean apiTradeAvailableFlag; // Параметр указывает на возможность торговать инструментом через API.

    String uid; // Уникальный идентификатор инструмента.
    RealExchange realExchange; // Реальная площадка исполнения расчётов. (биржа)
    String positionUid; // Уникальный идентификатор позиции инструмента.
    String assetUid; // Уникальный идентификатор актива.

    boolean forIisFlag; // Признак доступности для ИИС.
    boolean forQualInvestorFlag; // Флаг отображающий доступность торговли инструментом только для квалифицированных
    // инвесторов.
    boolean weekendFlag; // Флаг отображающий доступность торговли инструментом по выходным
    boolean blockedTcaFlag; // Флаг заблокированного ТКС
    boolean subordinatedFlag; // Признак субординированной облигации.
    boolean liquidityFlag; // Флаг достаточной ликвидности
    LocalDateTime first1minCandleDate; // Дата первой минутной свечи.
    LocalDateTime first1dayCandleDate; // Дата первой дневной свечи.
    RiskLevel riskLevel; // Уровень риска.
    BrandData brand; // Информация о бренде.
}
