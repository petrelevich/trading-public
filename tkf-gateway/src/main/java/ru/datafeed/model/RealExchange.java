package ru.datafeed.model;

import lombok.Getter;

// Реальная площадка исполнения расчётов.
@Getter
public enum RealExchange {
    REAL_EXCHANGE_UNSPECIFIED(0), // Тип не определён.
    REAL_EXCHANGE_MOEX(1), // Московская биржа.
    REAL_EXCHANGE_RTS(2), // Санкт-Петербургская биржа.
    REAL_EXCHANGE_OTC(3); // Внебиржевой инструмент.

    private final int code;

    RealExchange(int code) {
        this.code = code;
    }
}
