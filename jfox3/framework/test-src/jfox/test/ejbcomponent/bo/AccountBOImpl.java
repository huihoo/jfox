package jfox.test.ejbcomponent.bo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import jfox.test.ejbcomponent.dao.AccountDAO;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Stateless
public class AccountBOImpl implements AccountBO {

    @EJB(name="AccountDAO")
    private AccountDAO accountDAO;

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String getAccountName() {
        return "Yang Yong";
    }

}
