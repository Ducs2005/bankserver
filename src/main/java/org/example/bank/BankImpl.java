package org.example.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class BankImpl extends UnicastRemoteObject implements BankInterface {

    private final Map<String, String> users = new HashMap<>();
    private final Map<String, Double> balances = new HashMap<>();
    private final Map<String, ClientCallback> callbacks = new HashMap<>();

    public BankImpl() throws RemoteException {
        super();
        users.put("12345", "12345");
        balances.put("12345", 5000.0);
    }

    @Override
    public boolean register(String username, String password) throws RemoteException {
        if (users.containsKey(username)) return false;
        users.put(username, password);
        balances.put(username, 0.0);
        return true;
    }

    @Override
    public boolean login(String username, String password) throws RemoteException {
        return password.equals(users.get(username));
    }

    @Override
    public double getBalance(String username) throws RemoteException {
        return balances.getOrDefault(username, 0.0);
    }

    @Override
    public boolean deposit(String username, double amount) throws RemoteException {
        balances.put(username, getBalance(username) + amount);
        return true;
    }

    @Override
    public boolean withdraw(String username, double amount) throws RemoteException {
        double bal = getBalance(username);
        if (bal < amount) return false;
        balances.put(username, bal - amount);
        return true;
    }

    @Override
    public boolean transfer(String fromUser, String toUser, double amount) throws RemoteException {
        if (!users.containsKey(toUser)) return false;
        if (withdraw(fromUser, amount)) {
            deposit(toUser, amount);
            // Callback: gửi thông báo tới người nhận
            ClientCallback cb = callbacks.get(toUser);
            if (cb != null) cb.onReceiveTransfer(fromUser, amount);
            return true;
        }
        return false;
    }

    @Override
    public void registerCallback(String username, ClientCallback callback) throws RemoteException {
        callbacks.put(username, callback);
    }
}
