package org.jfox.example.ejb3.lob;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public interface LobberDAO {

    void insertLobber(Lobber lobber);

    Lobber getLobber(int id);

    void updateLobber(Lobber lobber);

    void deleteLobber(int id);
}
