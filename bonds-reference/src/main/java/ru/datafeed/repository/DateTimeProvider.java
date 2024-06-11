package ru.datafeed.repository;

import java.time.LocalDateTime;

public interface DateTimeProvider {
    LocalDateTime now();
}
