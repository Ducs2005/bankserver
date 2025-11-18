package org.example.bank;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class BankServer {
    public static void main(String[] args) {
        try {
            // IP thật của máy server (dùng ipconfig)
            System.setProperty("java.rmi.server.hostname", "10.50.138.100");

            // Start RMI Registry on port 1099
            LocateRegistry.createRegistry(1099);

            // Create remote object
            BankImpl bank = new BankImpl();

            // Bind service correctly
            Naming.rebind("rmi://10.50.138.100/BankService", bank);

            System.out.println("✅ Bank Server is running on rmi://10.50.138.100:1099/BankService");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
