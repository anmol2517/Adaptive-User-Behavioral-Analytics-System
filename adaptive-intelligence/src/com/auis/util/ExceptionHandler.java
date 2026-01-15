package com.auis.util;

import java.io.PrintWriter;
import java.io.StringWriter;


/*
  *  Exception Handling & Error Recovery
  *  Centralized exception handling with friendly user messages
*/


public class ExceptionHandler {
    

     //  Handle database exceptions with friendly messages


    public static String handleDatabaseException(Exception e) {
        String errorMessage = e.getMessage();
        
        if (errorMessage.contains("Connection refused")) {
            return "[ERROR] Database server is not running. Please start MySQL and try again.";
        } else if (errorMessage.contains("Access denied")) {
            return "[ERROR] Invalid database credentials. Please check username and password.";
        } else if (errorMessage.contains("Unknown database")) {
            return "[ERROR] Database 'auis_db' not found. Please run setup_database.sql first.";
        } else if (errorMessage.contains("Lock wait timeout")) {
            return "[ERROR] Database is busy. Please try again in a moment.";
        } else if (errorMessage.contains("Out of memory")) {
            return "[ERROR] System is low on memory. Please close other applications.";
        } else {
            return "[ERROR] Database operation failed. Please try again.";
        }
    }


     //  Handle login exceptions with friendly messages


    public static String handleLoginException(Exception e) {
        String errorMessage = e.getMessage();
        
        if (errorMessage.contains("locked")) {
            return "[ERROR] Account is locked. Please try again after 30 minutes.";
        } else if (errorMessage.contains("password")) {
            return "[ERROR] Incorrect password. Please try again. (3 attempts remaining)";
        } else if (errorMessage.contains("not found")) {
            return "[ERROR] User not found. Please register first.";
        }
        
        return "[ERROR] Login failed. Please try again.";
    }



    //  Handle action recording exceptions



    public static String handleActionException(Exception e) {
        String errorMessage = e.getMessage();
        
        if (errorMessage.contains("timeout")) {
            return "[ERROR] Operation timed out. Your action may not have been recorded. Please try again.";
        } else if (errorMessage.contains("No user logged in")) {
            return "[ERROR] You must be logged in to perform this action.";
        }
        
        return "[ERROR] Failed to record action. Please try again.";
    }


     //  Get stack trace as string for logging


    public static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }


     //  Log exception and return user-friendly message


    public static void logAndNotify(String context, Exception e) {
        SystemLogger.error(context + " : " + e.getMessage());
        SystemLogger.error("Stack trace : " + getStackTrace(e));
        System.err.println("[ALERT] " + context + " - An error occurred. Please check logs/system.log");
    }
}


