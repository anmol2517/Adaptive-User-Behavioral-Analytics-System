package com.auis.main;

import com.auis.controller.UserController;
import com.auis.util.DBConnection;
import com.auis.util.SystemLogger;
import com.auis.util.HealthMonitor;
import com.auis.worker.BackgroundIntelligenceWorker;
import java.util.Scanner;



// MainApp - Entry Point for Adaptive User Intelligence System (AUIS)



public class MainApp {
    private UserController userController;
    private Scanner scanner;
    private Thread backgroundWorkerThread;
    private BackgroundIntelligenceWorker backgroundWorker;

    public MainApp() {
        this.userController = new UserController();
        this.scanner = new Scanner(System.in);
    }



    public void displayMenu() {
        System.out.println("\n====================================================");
        System.out.println("      ADAPTIVE USER BEHAVIORAL ANALYTICS SYSTEM       ");
        System.out.println("   (Secure Driven Behavioral Analytics Frameworks)   ");
        System.out.println("====================================================");

        if (userController.getCurrentUserId() == -1) {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. System Health Monitor");
            System.out.println("4. Exit");
        } else {
            System.out.println("1. Record Action");
            System.out.println("2. View Suggestions");
            System.out.println("3. View Activity Report");
            System.out.println("4. System Health Monitor");
            System.out.println("5. Logout");
            System.out.println("6. Exit");
            System.out.println("7. Display Suspicious Users Report");
        }



        System.out.println("========================================");
        System.out.print("Enter your choice : ");
    }



    private void handleLogin() {
        System.out.print("Enter username : ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password : ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("[ERROR] Inputs cannot be empty!");
            return;
        }

        if (userController.loginUser(username, password)) {
            System.out.println("[SUCCESS] User logged in successfully!");
        }
    }



    //  New Register Handle


    private void handleRegister() {
        System.out.print("Enter new username : ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter your password : ");
        String password = scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("[ERROR] Fields cannot be empty!");
            return;
        }


        //  UseeContoller Functioanlity Using Registration


        if (userController.loginUser(username, password)) {
            System.out.println("[SUCCESS] New account created and logged in!");
        }
    }


    private void handleRecordAction() {
        System.out.print("Enter action (e.g., 'Login', 'Browse', 'Search', 'Purchase', 'Gaming'): ");
        String action = scanner.nextLine().trim().toUpperCase();
        if (!action.isEmpty()) {
            userController.recordUserAction(action);
        }
    }


    private void startBackgroundWorker() {
        backgroundWorker = new BackgroundIntelligenceWorker();
        backgroundWorkerThread = new Thread(backgroundWorker, "BackgroundIntelligenceWorker");
        backgroundWorkerThread.start();
        SystemLogger.info("Background Intelligence Worker thread started");
    }


    private void stopBackgroundWorker() {
        if (backgroundWorker != null) {
            backgroundWorker.stop();
            try {
                backgroundWorkerThread.join(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


    private void displayAnomalyReport() {
        System.out.println("\n========== SECURITY & ANOMALY REPORT ==========");
        com.auis.dao.AnomalyDAO anomalyDAO = new com.auis.dao.AnomalyDAO();
        java.util.List<Integer> topSuspicious = anomalyDAO.getTopSuspiciousUsers(3);

        if (topSuspicious.isEmpty()) {
            System.out.println("No suspicious activity detected.");
        } else {
            topSuspicious.forEach(id -> System.out.println("Suspicious User ID : " + id));
        }
        System.out.println("============================================\n");
    }


    public void run() {
        try {
            DBConnection.initializePool();
        } catch (Exception e) {
            System.err.println("[CRITICAL] DB Connection failed!");
            return;
        }


        startBackgroundWorker();


        boolean running = true;
        while (running) {
            displayMenu();
            String choice = scanner.nextLine().trim();

            if (userController.getCurrentUserId() == -1) {
                switch (choice) {
                    case "1": handleLogin(); break;
                    case "2": handleRegister(); break;
                    case "3": HealthMonitor.displayHealthStatus(); break;
                    case "4": running = false; break;
                    default: System.out.println("[ERROR] Invalid choice!");
                }
            } else {

                switch (choice) {
                    case "1": handleRecordAction(); break;
                    case "2": userController.showSuggestion(); break;
                    case "3": userController.showActivityReport(); break;
                    case "4": HealthMonitor.displayHealthStatus(); break;
                    case "5": userController.logout(); break;
                    case "6": running = false; break;
                    case "7": displayAnomalyReport(); break;
                }
            }
        }


        stopBackgroundWorker();
        DBConnection.closeAllConnections();
        System.exit(0);
    }


    public static void main(String[] args) {
        new MainApp().run();
    }
}

