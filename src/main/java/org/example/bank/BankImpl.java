package org.example.bank;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.HashMap;

public class BankImpl extends UnicastRemoteObject implements BankInterface {

    private BankData data; // chứa users + balances
    private final String FILE_PATH = "data.txt";

    private final Map<String, ClientCallback> callbacks = new HashMap<>();

    public BankImpl() throws RemoteException {
        super();
        data = loadData();

        // Trường hợp file trống → tạo user mặc định
        if (data.users.isEmpty()) {
            data.users.put("12345", "12345");
            data.balances.put("12345", 5000.0);
            saveData();
        }
    }

    // ------------------------- FILE I/O -------------------------

    private synchronized void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (Exception e) {
            System.out.println("Error saving file: " + e);
        }
    }

    private BankData loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (BankData) ois.readObject();
        } catch (Exception e) {
            return new BankData(); // file chưa tồn tại
        }
    }

    // ------------------------- LOGIC -------------------------

    @Override
    public boolean register(String username, String password) throws RemoteException {
        if (data.users.containsKey(username)) return false;

        data.users.put(username, password);
        data.balances.put(username, 0.0);
        saveData();
        return true;
    }

    @Override
    public boolean login(String username, String password) throws RemoteException {
        return password.equals(data.users.get(username));
    }

    @Override
    public double getBalance(String username) throws RemoteException {
        return data.balances.getOrDefault(username, 0.0);
    }

    @Override
    public boolean deposit(String username, double amount) throws RemoteException {
        data.balances.put(username, getBalance(username) + amount);
        saveData();
        return true;
    }

    @Override
    public boolean withdraw(String username, double amount) throws RemoteException {
        double bal = getBalance(username);
        if (bal < amount) return false;

        data.balances.put(username, bal - amount);
        saveData();
        return true;
    }

    @Override
    public boolean transfer(String fromUser, String toUser, double amount) throws RemoteException {
        if (!data.users.containsKey(toUser)) return false;

        if (withdraw(fromUser, amount)) {
            deposit(toUser, amount);

            // Callback tại client nhận tiền
            ClientCallback cb = callbacks.get(toUser);
            if (cb != null) cb.onReceiveTransfer(fromUser, amount);

            saveData();
            return true;
        }
        return false;
    }

    @Override
    public void registerCallback(String username, ClientCallback callback) throws RemoteException {
        callbacks.put(username, callback);
    }
}
