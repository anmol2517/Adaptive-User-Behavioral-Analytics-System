package com.auis.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;


/*

  *  Database Connection Utility - Connection Pool Pattern
  *  Removed static connection, now creates fresh connections
  *  Worker thread safety with proper connection management

*/


public class DBConnection {
    private static final LinkedBlockingQueue<Connection> connectionPool = new LinkedBlockingQueue<>(10);
    private static final int POOL_SIZE = 10;
    private static boolean poolInitialized = false;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/auis_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Scar2511@#";


     //  Initialize connection pool on startup


    public static synchronized void initializePool() throws SQLException {
        if (poolInitialized) return;

        for (int i = 0; i < POOL_SIZE; i++) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                connectionPool.put(conn);
            } catch (ClassNotFoundException e) {
                System.err.println("[ERROR] MySQL JDBC Driver not found!");
                throw new SQLException("JDBC Driver not found", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SQLException("Pool initialization interrupted", e);
            }
        }

        poolInitialized = true;
        System.out.println("[DB] Connection pool initialized with " + POOL_SIZE + " connections");
    }


     //   Get database connection from pool
     //   NEW : Returns fresh connection from pool instead of reusing single connection


    public static Connection getConnection() throws SQLException {
        if (!poolInitialized) {
            initializePool();
        }

        try {
            Connection conn = connectionPool.take();
            if (conn.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
            return conn;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for connection", e);
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found", e);
        }
    }



    /*

      *  Return connection back to pool (IMPORTANT: Must be called after use)
      *  NEW : This ensures connections are reused efficiently

    */


    public static void returnConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.getAutoCommit()) {
                    conn.setAutoCommit(true);
                }
                connectionPool.put(conn);
            } catch (SQLException | InterruptedException e) {
                System.err.println("[ERROR] Failed to return connection to pool : " + e.getMessage());
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("[ERROR] Failed to close connection : " + closeEx.getMessage());
                }
            }
        }
    }



     //  Close all connections in pool (call on shutdown)



    public static void closeAllConnections() {
        while (!connectionPool.isEmpty()) {
            try {
                Connection conn = connectionPool.take();
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException | InterruptedException e) {
                System.err.println("[ERROR] Error closing connection : " + e.getMessage());
            }
        }
        System.out.println("[DB] All connections closed");
    }



     //  Get pool size for monitoring


    public static int getPoolSize() {
        return connectionPool.size();
    }
}


