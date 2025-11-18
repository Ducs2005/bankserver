package org.example.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallback extends Remote {
    void onReceiveTransfer(String fromUser, double amount) throws RemoteException;
}
