package ru.datafeed.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Currency {
    RUB,
    USD,
    EUR,
    GBP,
    CNY,
    CHF,
    AED;

    private static final Logger log = LoggerFactory.getLogger(Currency.class);

    public static Currency get(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return Currency.valueOf(value.toUpperCase());
    }
}
