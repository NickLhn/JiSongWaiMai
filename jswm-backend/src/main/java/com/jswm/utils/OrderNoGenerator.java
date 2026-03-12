package com.jswm.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderNoGenerator {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    public static synchronized String generate() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        int seq = SEQUENCE.incrementAndGet();
        if (seq > 9999) {
            SEQUENCE.set(0);
            seq = 0;
        }
        return "ORD" + timestamp + String.format("%04d", seq);
    }
}
