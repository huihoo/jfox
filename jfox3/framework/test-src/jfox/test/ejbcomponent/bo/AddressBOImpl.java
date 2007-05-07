/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejbcomponent.bo;

import javax.ejb.EJB;

import jfox.test.ejbcomponent.dao.AddressDAO;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class AddressBOImpl implements AddressBO{

    @EJB
    AddressDAO addressDAO;

    public static void main(String[] args) {

    }
}
