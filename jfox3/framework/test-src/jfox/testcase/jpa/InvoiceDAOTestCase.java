package jfox.testcase.jpa;

import jfox.testcase.jpa.dao.InvoiceDAO;
import jfox.testcase.jpa.dao.InvoiceDAOBean;
import jfox.testcase.jpa.entity.Invoice;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create 2009-2-17 11:53:19
 */
public class InvoiceDAOTestCase extends HsqlDBSupportTestCase {

    static InvoiceDAO invoiceDAO = null;

    @BeforeClass
    public static void setup() throws Exception {

        invoiceDAO = new InvoiceDAOBean(){
            public EntityManager getEntityManager() {
                return HsqlDBSupportTestCase.getEntityManager();
            }
        };
    }


    @Test
    public void listInvoices() throws Exception {
        List<Invoice> invoices = invoiceDAO.getAllInvoices();
        Assert.assertTrue(!invoices.isEmpty());
        for(Invoice invoice : invoices){
            System.out.println(invoice);
        }
    }
}
