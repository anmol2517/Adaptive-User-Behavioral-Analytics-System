package com.auis.util;

import java.security.MessageDigest;
import java.util.Base64;


/*

  *  Security Manager
  *  Handles password hashing and account lock management

*/

public class SecurityManager {
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCK_DURATION_MINUTES = 30;


    //  Hash password using SHA-256

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (Exception e) {
            SystemLogger.error("Password hashing failed : " + e.getMessage());
            return null;
        }
    }


     //  Verify password against hash


    public static boolean verifyPassword(String password, String hash) {
        if (password == null || hash == null) {
            return false;
        }
        String hashedInput = hashPassword(password);
        return hashedInput != null && hashedInput.equals(hash);
    }


     //  Check if user account is locked

    public static boolean isAccountLocked(int userId) {


        // Check in database if user is locked & For now, using in-memory check


        return false;
    }


    //  Lock user account after max failed attempts


    public static void lockAccount(int userId) {
        SystemLogger.warn("Account locked for user : " + userId);
        SystemReport.generateSecurityReport("ACCOUNT_LOCKED", 
            "User " + userId + " account locked due to max failed login attempts");
    }



    //  Increment failed login attempts



    public static void incrementFailedAttempts(int userId) {
        SystemLogger.warn("Failed login attempt for user : " + userId);    // This will be updated in UserDAO
    }


     //  Reset failed login attempts on successful login


    public static void resetFailedAttempts(int userId) {
        SystemLogger.info("Failed attempts reset for user: " + userId);    // This will be updated in UserDAO
    }
}


