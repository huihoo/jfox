package org.jfox.example.ejb3.lob;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface LobberDAO {

    void insertLobber(Lobber lobber);

    Lobber getLobber(int id);

    void updateLobber(Lobber lobber);

    void deleteLobber(int id);
}
