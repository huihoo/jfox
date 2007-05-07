/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejb3.lob;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface LobberDAO {

    void insertLobber(Lobber lobber);

    Lobber getLobber(int id);

    void updateLobber(Lobber lobber);

    void deleteLobber(int id);
}
