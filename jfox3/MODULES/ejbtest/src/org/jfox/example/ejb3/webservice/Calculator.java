package org.jfox.example.ejb3.webservice;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface Calculator extends Remote {
    int add(int x, int y) throws RemoteException;

    int subtract(int x, int y) throws RemoteException;
}
