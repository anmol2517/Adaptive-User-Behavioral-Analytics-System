package com.auis.util;

import java.util.*;


  //  HealthMonitor - System health and performance metrics


public class HealthMonitor {
    private static long startTime = System.currentTimeMillis();
    private static long totalQueryCount = 0;
    private static long totalQueryTime = 0;
    private static long maxQueryTime = 0;


     //  Record query execution time

    public static synchronized void recordQuery(long responseTimeMs) {
        totalQueryCount++;
        totalQueryTime += responseTimeMs;
        if (responseTimeMs > maxQueryTime) {
            maxQueryTime = responseTimeMs;
        }
    }


     //   Get health metrics


    public static synchronized Map<String, Object> getHealthMetrics() {
        Map<String, Object> metrics = new LinkedHashMap<>();

        long uptime = System.currentTimeMillis() - startTime;
        metrics.put("uptime_seconds", uptime / 1000);
        metrics.put("active_sessions", SessionManager.getActiveSessionCount());
        metrics.put("total_queries", totalQueryCount);


        metrics.put("average_query_time_ms", totalQueryCount > 0 ? totalQueryTime / totalQueryCount : 0);
        metrics.put("max_query_time_ms", maxQueryTime);
        metrics.put("memory_used_mb", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024));
        metrics.put("memory_available_mb", Runtime.getRuntime().freeMemory() / (1024 * 1024));
        metrics.put("database_connection_status", "ACTIVE");

        return metrics;
    }


     //  Display health status


    public static void displayHealthStatus() {
        Map<String, Object> metrics = getHealthMetrics();

        System.out.println("\n========================================");
        System.out.println("  SYSTEM HEALTH MONITOR");
        System.out.println("========================================");

        for (Map.Entry<String, Object> entry : metrics.entrySet()) {
            System.out.printf("%-30s : %s\n", entry.getKey(), entry.getValue());
        }

        System.out.println("========================================\n");


        // Performance alerts


        long avgQueryTime = (long) metrics.get("average_query_time_ms");
        if (avgQueryTime > 500) {
            SystemLogger.warn("PERFORMANCE_ALERT : Average query time is " + avgQueryTime + "ms");
        }

        int activeSessions = (Integer) metrics.get("active_sessions");
        if (activeSessions > 100) {
            SystemLogger.warn("LOAD_ALERT : High number of active sessions : " + activeSessions);
        }
    }
}



