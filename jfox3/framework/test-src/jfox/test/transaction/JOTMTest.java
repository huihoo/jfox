package jfox.test.transaction;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import javax.transaction.TransactionManager;

import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.jotm.Jotm;
import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import org.enhydra.jdbc.standard.StandardXADataSource;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class JOTMTest {

    private static TransactionManager transactionManager;
    private static StandardXAPoolDataSource spds;

    @BeforeClass
    public static void setup() throws Exception {
        String username = "root";
        String password = "root";
// create an XA pool datasource with a minimum of 4 objects
        spds = new StandardXAPoolDataSource(2);
        spds.setUser(username);
        spds.setPassword(password);
        spds.setDebug(true);
        spds.setVerbose(true);
        Jotm jotm = new Jotm(true, false);
        transactionManager = jotm.getTransactionManager();
        spds.setTransactionManager(transactionManager);

// create an XA datasource which will be given to the XA pool
        StandardXADataSource xads = new StandardXADataSource();
        try {
            xads.setDriverName("com.mysql.jdbc.Driver");
            xads.setUrl("jdbc:mysql://localhost/test");
            xads.setUser(username);
            xads.setPassword(password);
            xads.setVerbose(true);
            xads.setDebug(true);
        }
        catch (Exception e) {
            System.err.println("JOTM problem.");
        }

// give the XA datasource to the pool (to create future objects)
        spds.setDataSource(xads);
    }

    @Test
    public void testSelect() throws Exception {
        transactionManager.begin();
        Connection connection = spds.getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rset = stmt.executeQuery("select * from ACCOUNT");
        while (rset.next()) {
            System.out.println("ACC_ID: " + rset.getString(1) + ", ACC_FIRST_NAME: " + rset.getString(2));
        }
        transactionManager.commit();
        connection.close();
    }

    @Test
    public void testUpdate() throws Exception {
        transactionManager.begin();
        try {
            Connection connection = spds.getConnection();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("update Account set ACC_FIRST_NAME='Yang Yong' where ACC_ID=10");

            Connection connection2 = spds.getConnection();
            Statement stmt2 = connection2.createStatement();
            stmt2.executeUpdate("update Account set ACC_FIRST_NAME='Lisa Li' where ACC_ID=11");

            // will throw exception
//            Connection connection3 = spds.getConnection();
//            Statement stmt3 = connection3.createStatement();
//            stmt3.executeUpdate("INSERT INTO ACCOUNT VALUES(12,'Clinton', 'Begin', 'clinton.begin@ibatis.com')");
//            transactionManager.commit();
        }
        catch (Exception e) {
            transactionManager.rollback();
            throw e;
        }

    }

    public static void main(String[] args) {

    }
}
