package org.example.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BankInterface extends Remote {
    boolean register(String username, String password) throws RemoteException;
    boolean login(String username, String password) throws RemoteException;

    double getBalance(String username) throws RemoteException;
    boolean deposit(String username, double amount) throws RemoteException;
    boolean withdraw(String username, double amount) throws RemoteException;
    boolean transfer(String fromUser, String toUser, double amount) throws RemoteException;

    void registerCallback(String username, ClientCallback callback) throws RemoteException;
}
