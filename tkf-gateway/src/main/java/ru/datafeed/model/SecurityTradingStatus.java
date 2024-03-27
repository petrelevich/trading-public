package ru.datafeed.model;

import lombok.Getter;

@Getter
public enum SecurityTradingStatus {
    SECURITY_TRADING_STATUS_UNSPECIFIED(0), // Торговый статус не определён
    SECURITY_TRADING_STATUS_NOT_AVAILABLE_FOR_TRADING(1), // Недоступен для торгов
    SECURITY_TRADING_STATUS_OPENING_PERIOD(2), // Период открытия торгов
    SECURITY_TRADING_STATUS_CLOSING_PERIOD(3), // Период закрытия торгов
    SECURITY_TRADING_STATUS_BREAK_IN_TRADING(4), // Перерыв в торговле
    SECURITY_TRADING_STATUS_NORMAL_TRADING(5), // Нормальная торговля
    SECURITY_TRADING_STATUS_CLOSING_AUCTION(6), // Аукцион закрытия
    SECURITY_TRADING_STATUS_DARK_POOL_AUCTION(7), // Аукцион крупных пакетов
    SECURITY_TRADING_STATUS_DISCRETE_AUCTION(8), // Дискретный аукцион
    SECURITY_TRADING_STATUS_OPENING_AUCTION_PERIOD(9), // Аукцион открытия
    SECURITY_TRADING_STATUS_TRADING_AT_CLOSING_AUCTION_PRICE(10), // Период торгов по цене аукциона закрытия
    SECURITY_TRADING_STATUS_SESSION_ASSIGNED(11), // Сессия назначена
    SECURITY_TRADING_STATUS_SESSION_CLOSE(12), // Сессия закрыта
    SECURITY_TRADING_STATUS_SESSION_OPEN(13), // Сессия открыта
    SECURITY_TRADING_STATUS_DEALER_NORMAL_TRADING(14), // Доступна торговля в режиме внутренней ликвидности брокера
    SECURITY_TRADING_STATUS_DEALER_BREAK_IN_TRADING(15), // Перерыв торговли в режиме внутренней ликвидности брокера
    SECURITY_TRADING_STATUS_DEALER_NOT_AVAILABLE_FOR_TRADING(
            16); // Недоступна торговля в режиме внутренней ликвидности брокера

    private final int code;

    SecurityTradingStatus(int code) {
        this.code = code;
    }
}
