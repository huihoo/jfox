package net.sourceforge.jfox.ejb3.example;

import javax.ejb.EJB;

import net.sourceforge.jfox.entity.dao.example.AddressDAO;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class AddressBOImpl implements AddressBO{

    @EJB
    AddressDAO addressDAO;

    public static void main(String[] args) {

    }
}
