package jfox.testcase.jpa.dao;

import jfox.testcase.jpa.entity.Invoice;
import org.jfox.entity.EntityManagerExt;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create 2009-2-17 11:44:14
 */
@Stateless(name = "InvoiceDAOBeanEJB")
@Local
@NamedNativeQueries(
        {
                @NamedNativeQuery(name="getAllInvoices",
                        query = "select * from Invoice", 
                        resultClass = Invoice.class)
        }
)
public class InvoiceDAOBean implements InvoiceDAO{

    @PersistenceContext(unitName = "TestHsqlDB")
    EntityManagerExt em;

    public EntityManager getEntityManager() {
        return em;
    }

    public List<Invoice> getAllInvoices() {
        // 为了能容器外运行，必须用getEntityManager()方法，而不是直接使用 em 字段
        Query query = getEntityManager().createNamedQuery("getAllInvoices");
        List<Invoice> invoices = query.getResultList();
        return invoices;
    }

}
