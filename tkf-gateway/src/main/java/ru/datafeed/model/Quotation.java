package ru.datafeed.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
// Котировка — денежная сумма без указания валюты
public class Quotation {
    long units; // целая часть суммы, может быть отрицательным числом
    long nano; // дробная часть суммы, может быть отрицательным числом
}
