package org.example.bank;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class BankImpl extends UnicastRemoteObject implements BankInterface {

    private final String FILE_PATH = "data.txt";

    // Lưu dữ liệu trong RAM
    private Map<String, String> users = new HashMap<>();
    private Map<String, Double> balances = new HashMap<>();

    private Map<String, ClientCallback> callbacks = new HashMap<>();

    public BankImpl() throws RemoteException {
        super();
        loadData();

        // Nếu file trống → tạo user mặc định
        if (users.isEmpty()) {
            users.put("12345", "12345");
            balances.put("12345", 5000.0);
            saveData();
        }
    }

    // ---------------------------------------------------------
    // FILE I/O - STRING-BASED
    // ---------------------------------------------------------

    private synchronized void saveData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (String user : users.keySet()) {
                double bal = balances.getOrDefault(user, 0.0);
                String pass = users.get(user);

                pw.println(user + "|" + pass + "|" + bal);
            }
        } catch (Exception e) {
            System.out.println("Error saving: " + e);
        }
    }

    private synchronized void loadData() {
        users.clear();
        balances.clear();

        File f = new File(FILE_PATH);
        if (!f.exists()) return; // file chưa tồn tại

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    String user = parts[0];
                    String pass = parts[1];
                    double bal = Double.parseDouble(parts[2]);

                    users.put(user, pass);
                    balances.put(user, bal);
                }
            }

        } catch (Exception e) {
            System.out.println("Error loading: " + e);
        }
    }

    // ---------------------------------------------------------
    // LOGIC
    // ---------------------------------------------------------

    @Override
    public boolean register(String username, String password) throws RemoteException {
        if (users.containsKey(username)) return false;

        users.put(username, password);
        balances.put(username, 0.0);
        saveData();
        return true;
    }

    @Override
    public boolean login(String username, String password) throws RemoteException {
        String pass = users.get(username);
        return pass != null && pass.equals(password);
    }

    @Override
    public double getBalance(String username) throws RemoteException {
        return balances.getOrDefault(username, 0.0);
    }

    @Override
    public boolean deposit(String username, double amount) throws RemoteException {
        balances.put(username, getBalance(username) + amount);
        saveData();
        return true;
    }

    @Override
    public boolean withdraw(String username, double amount) throws RemoteException {
        double bal = getBalance(username);
        if (bal < amount) return false;

        balances.put(username, bal - amount);
        saveData();
        return true;
    }

    @Override
    public int transfer(String fromUser, String toUser, double amount) throws RemoteException {

        if (!users.containsKey(toUser)) {
            return -1; // tài khoản không tồn tại
        }

        double bal = balances.get(fromUser);
        if (bal < amount) {
            return -2; // số dư không đủ
        }

        // Thực hiện chuyển
        balances.put(fromUser, bal - amount);
        balances.put(toUser, balances.get(toUser) + amount);
        saveData();

        // thông báo realtime
        ClientCallback cb = callbacks.get(toUser);
        if (cb != null) cb.onReceiveTransfer(fromUser, amount);

        return 1; // thành công
    }


    @Override
    public void registerCallback(String username, ClientCallback callback) throws RemoteException {
        callbacks.put(username, callback);
    }
}
