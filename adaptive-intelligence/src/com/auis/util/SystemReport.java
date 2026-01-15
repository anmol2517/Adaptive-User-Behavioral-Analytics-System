package com.auis.util;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/*

  * SystemReport Generator
  * Generates comprehensive reports instead of console-only output
  * Reports are saved as SystemReport.txt files in logs/ directory

*/


public class SystemReport {
    private static final String REPORT_DIR = "logs";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy - MM - dd HH : mm : ss");
    
    static {
        File dir = new File(REPORT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }


     //   Generate comprehensive system report


    public static void generateSystemReport(String title, Map<String, String> data) {
        String filename = REPORT_DIR + "/system_report_" + System.currentTimeMillis() + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("===============================================");
            writer.println(title);
            writer.println("===============================================");
            writer.println("Generated : " + LocalDateTime.now().format(formatter));
            writer.println();

            for (Map.Entry<String, String> entry : data.entrySet()) {
                writer.println("[" + entry.getKey() + "]");
                writer.println(entry.getValue());
                writer.println();
            }

            writer.println("===============================================");
            writer.println("End of Report");
            writer.println("===============================================");

            System.out.println("[REPORT] Generated : " + filename);
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to generate report : " + e.getMessage());
        }
    }


     //   Generate user activity report


    public static void generateUserActivityReport(int userId, String username, 
                                                   int totalActions, String lastAction, 
                                                   LocalDateTime sessionStart) {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("USER_INFO", String.format("User ID : %d\nUsername: %s\nSession Start : %s",
                                           userId, username, sessionStart.format(formatter)));
        data.put("ACTIVITY_SUMMARY", String.format("Total Actions : %d\nLast Action : %s\nStatus : ACTIVE",
                                                    totalActions, lastAction));
        
        generateSystemReport("USER ACTIVITY REPORT - " + username, data);
    }



     //    Generate anomaly detection report


    public static void generateAnomalyReport(int userId, String anomalyType, 
                                            int actionCount, String severity) {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("ANOMALY_ALERT", String.format("User ID : %d\nType : %s\nSeverity : %s",
                                               userId, anomalyType, severity));
        data.put("INCIDENT_DETAILS", String.format("Actions in Window : %d\nDetected At : %s\nStatus : FLAGGED",
                                                   actionCount, LocalDateTime.now().format(formatter)));
        
        generateSystemReport("ANOMALY DETECTION ALERT", data);
    }


     //    Generate security report


    public static void generateSecurityReport(String incidentType, String details) {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("SECURITY_INCIDENT", "Type : " + incidentType);
        data.put("DETAILS", details);
        data.put("TIMESTAMP", LocalDateTime.now().format(formatter));
        
        generateSystemReport("SECURITY REPORT", data);
    }



     //   Append data to existing report


    public static void appendToReport(String filename, String content) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            writer.println(content);
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to append to report : " + e.getMessage());
        }
    }
}


