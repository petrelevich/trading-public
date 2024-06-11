package ru.datafeed.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Builder
@Getter
@ToString
@Accessors(fluent = true)
public class BondDto {
    String figi; // Figi-идентификатор инструмента.
    String ticker; // Тикер инструмента.
    String classCode; // Класс-код (секция торгов).
    String isin; // Isin-идентификатор инструмента.
    int lot; // Лотность инструмента. Возможно совершение операций только на количества ценной бумаги, кратные
    // параметру *lot*. Подробнее: [лот](https://russianinvestments.github.io/investAPI/glossary#lot)
    Currency currency; // Валюта расчётов.

    // Коэффициент ставки риска длинной позиции по клиенту. 2 – клиент со стандартным уровнем риска
    // (КСУР). 1 – клиент с повышенным уровнем риска (КПУР)
    Long klongUnits; // целая часть суммы, может быть отрицательным числом
    Integer klongNano; // дробная часть суммы, может быть отрицательным числом

    // Коэффициент ставки риска короткой позиции по клиенту. 2 – клиент со стандартным уровнем риска
    // (КСУР). 1 – клиент с повышенным уровнем риска (КПУР)
    Long kshortUnits; // целая часть суммы, может быть отрицательным числом
    Integer kshortNano; // дробная часть суммы, может быть отрицательным числом

    // Ставка риска начальной маржи для КСУР лонг. Подробнее: [ставка риска в
    // лонг](https://help.tinkoff.ru/margin-trade/long/risk-rate/)
    Long dlongUnits; // целая часть суммы, может быть отрицательным числом
    Integer dlongNano; // дробная часть суммы, может быть отрицательным числом

    // Ставка риска начальной маржи для КСУР шорт. Подробнее: [ставка риска в
    // шорт](https://help.tinkoff.ru/margin-trade/short/risk-rate/)
    Long dshortUnits; // целая часть суммы, может быть отрицательным числом
    Integer dshortNano; // дробная часть суммы, может быть отрицательным числом

    // Ставка риска начальной маржи для КПУР лонг. Подробнее: [ставка риска в
    // лонг](https://help.tinkoff.ru/margin-trade/long/risk-rate/)
    Long dlongMinUnits; // целая часть суммы, может быть отрицательным числом
    Integer dlongMinNano; // дробная часть суммы, может быть отрицательным числом

    // Ставка риска начальной маржи для КПУР шорт. Подробнее: [ставка риска в
    // шорт](https://help.tinkoff.ru/margin-trade/short/risk-rate/)
    Long dshortMinUnits; // целая часть суммы, может быть отрицательным числом
    Integer dshortMinNano; // дробная часть суммы, может быть отрицательным числом

    boolean shortEnabledFlag; // Признак доступности для операций в шорт.
    String name; // Название инструмента.
    String exchange; // Tорговая площадка (секция биржи).

    int couponQuantityPerYear; // Количество выплат по купонам в год.
    LocalDateTime maturityDate; // Дата погашения облигации в часовом поясе UTC.

    // Номинал облигации.
    Currency nominalCurrency; // строковый ISO-код валюты
    Long nominalUnits; // целая часть суммы, может быть отрицательным числом
    Integer nominalNano; // дробная часть суммы, может быть отрицательным числом

    // Первоначальный номинал облигации.
    Currency initialNominalCurrency; // строковый ISO-код валюты
    Long initialNominalUnits; // целая часть суммы, может быть отрицательным числом
    Integer initialNominalNano; // дробная часть суммы, может быть отрицательным числом

    LocalDateTime stateRegDate; // Дата выпуска облигации в часовом поясе UTC.
    LocalDateTime placementDate; // Дата размещения в часовом поясе UTC.

    // Цена размещения.
    Currency placementPriceCurrency; // строковый ISO-код валюты
    Long placementPriceUnits; // целая часть суммы, может быть отрицательным числом
    Integer placementPriceNano; // дробная часть суммы, может быть отрицательным числом

    // Значение НКД (накопленного купонного дохода) на дату.
    Currency aciValueCurrency; // строковый ISO-код валюты
    Long aciValueUnits; // целая часть суммы, может быть отрицательным числом
    Integer aciValueNano; // дробная часть суммы, может быть отрицательным числом

    String sector; // Сектор экономики.
    String issueKind; // Форма выпуска: **documentary** — документарная **non_documentary** — бездокументарная.
    long issueSize; // Размер выпуска.
    long issueSizePlan; // Плановый размер выпуска.

    SecurityTradingStatus tradingStatus; // Текущий режим торгов инструмента.
    boolean buyAvailableFlag; // Признак доступности для покупки.
    boolean sellAvailableFlag; // Признак доступности для продажи.
    boolean floatingCouponFlag; // Признак облигации с плавающим купоном.
    boolean perpetualFlag; // Признак бессрочной облигации.
    boolean amortizationFlag; // Признак облигации с амортизацией долга.
    // Шаг цены.
    Long minPriceIncrementUnits; // целая часть суммы, может быть отрицательным числом
    Integer minPriceIncrementNano; // дробная часть суммы, может быть отрицательным числом

    boolean apiTradeAvailableFlag; // Параметр указывает на возможность торговать инструментом через API.

    String uid; // Уникальный идентификатор инструмента.
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
}
