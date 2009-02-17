package jfox.testcase.jpa.dao;

import jfox.testcase.jpa.DAOSupportTest;
import jfox.testcase.jpa.entity.Invoice;

import java.util.List;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create 2009-2-17 11:45:59
 */
public interface InvoiceDAO extends DAOSupportTest {

    public List<Invoice> getAllInvoices(); 

}
