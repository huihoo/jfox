/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejbcomponent.bo;

import jfox.test.ejbcomponent.dao.AccountDAO;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
//@Stateless
public class AccountBOImpl implements AccountBO {

    @EJB(name="AccountDAO")
    private AccountDAO accountDAO;

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String getAccountName() {
        return "Yang Yong";
    }

}
