package ru.datafeed.model;

import lombok.Getter;

@Getter
// Уровень риска облигации.
public enum RiskLevel {
    RISK_LEVEL_UNSPECIFIED(0), // не указан
    RISK_LEVEL_LOW(1), // Низкий уровень риска
    RISK_LEVEL_MODERATE(2), // Средний уровень риска
    RISK_LEVEL_HIGH(3); // Высокий уровень риска

    private final int code;

    RiskLevel(int code) {
        this.code = code;
    }
}
