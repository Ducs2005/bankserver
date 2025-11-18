package org.example.bank;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class BankServer {
    public static void main(String[] args) {
        try {
            // 1️⃣ Tell RMI what IP to advertise to clients
            System.setProperty("java.rmi.server.hostname", "192.168.1.19");

            // 2️⃣ Start registry on this machine (localhost)
            LocateRegistry.createRegistry(1099);

            // 3️⃣ Create your remote object
            BankImpl bank = new BankImpl();

            // 4️⃣ Bind service (IMPORTANT: No IP here)
            Naming.rebind("BankService", bank);

            System.out.println("✅ Bank Server is running on 192.168.1.19:1099");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
