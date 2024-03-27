package ru.datafeed.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrandData {
    String logoName; // Логотип инструмента. Имя файла для получения логотипа.
    String logoBaseColor; // 	Цвет бренда.
    String textColor; // Цвет текста для цвета логотипа бренда.
}
