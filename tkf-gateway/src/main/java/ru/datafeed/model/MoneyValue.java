package ru.datafeed.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
// Денежная сумма в определенной валюте
public class MoneyValue {

    String currency; // строковый ISO-код валюты

    long units; // целая часть суммы, может быть отрицательным числом

    int nano; // дробная часть суммы, может быть отрицательным числом
}
