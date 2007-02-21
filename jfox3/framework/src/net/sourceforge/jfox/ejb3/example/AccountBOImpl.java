package net.sourceforge.jfox.ejb3.example;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import net.sourceforge.jfox.entity.dao.example.AccountDAO;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
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
