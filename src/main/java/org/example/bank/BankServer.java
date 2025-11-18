package org.example.bank;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class BankServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            BankImpl bank = new BankImpl();
            Naming.rebind("rmi://localhost/BankService", bank);
            System.out.println("✅ Bank Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
