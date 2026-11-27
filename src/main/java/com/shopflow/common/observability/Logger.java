package com.shopflow.common.observability;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility log thống nhất: log trạng thái bộ nhớ JVM theo MB.
 *
 * <p>Ví dụ dùng: trước/sau khi chạy batch lớn để xem memory thay đổi thế nào.</p>
 */
@Slf4j
public final class Logger {

    private Logger() {
    }

    public static void logMemory(String step) {
        Runtime runtime = Runtime.getRuntime();

        long used = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long total = runtime.totalMemory() / (1024 * 1024);
        long max = runtime.maxMemory() / (1024 * 1024);

        log.info("[{}]", step);
        log.info("Đang dùng: {} MB", used);
        log.info("Tổng cộng: {} MB", total);
        log.info("Tối đa: {} MB", max);
    }
}
