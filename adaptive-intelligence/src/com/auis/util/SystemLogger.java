package com.auis.util;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


 //   SystemLogger - Enterprise-grade logging system


public class SystemLogger {
    private static final String LOG_FILE = "logs/system.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy - MM - dd HH : mm : ss . SSS");
    private static PrintWriter writer = null;

    static {
        try {
            new File("logs").mkdirs();
            writer = new PrintWriter(new FileWriter(LOG_FILE, true));
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to initialize logger : " + e.getMessage());
        }
    }


     //   Log INFO level message

    public static synchronized void info(String message) {
        log("INFO", message);
    }


     //   Log ERROR level message

    public static synchronized void error(String message) {
        log("ERROR", message);
    }


     //  Log WARN level message

    public static synchronized void warn(String message) {
        log("WARN", message);
    }


     //  Log SECURITY level message

    public static synchronized void security(String message) {
        log("SECURITY", message);
    }


     //  Log PERFORMANCE level message with response time

    public static synchronized void performance(String operation, long responseTimeMs) {
        String message = String.format("%s | Response Time : %dms", operation, responseTimeMs);
        log("PERFORMANCE", message);

        if (responseTimeMs > 1000) {
            warn(String.format("SLOW_QUERY : %s took %dms", operation, responseTimeMs));
        }
    }


     // Internal log method

    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry = String.format("[%s] [%s] %s", timestamp, level, message);

        System.out.println(logEntry);

        if (writer != null) {
            writer.println(logEntry);
            writer.flush();
        }
    }


     //  Close logger

    public static void close() {
        if (writer != null) {
            writer.close();
        }
    }
}
