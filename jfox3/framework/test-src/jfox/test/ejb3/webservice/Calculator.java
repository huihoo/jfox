/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejb3.webservice;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface Calculator extends Remote {
    int add(int x, int y) throws RemoteException;

    int subtract(int x, int y) throws RemoteException;
}
