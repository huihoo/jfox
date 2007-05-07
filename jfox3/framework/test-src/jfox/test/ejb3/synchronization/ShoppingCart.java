/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejb3.synchronization;

import java.util.Map;
import javax.ejb.Remove;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public interface ShoppingCart {

    void buy(String product, int quantity);

    Map<String, Integer> getCartContents();

    @Remove
    void checkout();
}