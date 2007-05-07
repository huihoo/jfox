/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.transaction;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Random;
import javax.sql.DataSource;
import javax.transaction.RollbackException;
import javax.transaction.Transaction;

import org.jfox.ejb3.transaction.JTATransactionManager;
import org.jfox.entity.EntityManagerExt;
import org.jfox.entity.EntityManagerImpl;
import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import org.enhydra.jdbc.standard.StandardXADataSource;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class JTATransactionManagerTest {

    static EntityManagerExt pm;
    static JTATransactionManager tm;

    @BeforeClass
    public static void setup() throws Exception {
        tm = JTATransactionManager.getIntsance();
//        tm.instantiated(null);

        final StandardXAPoolDataSource sxapds = new StandardXAPoolDataSource();
        sxapds.setVerbose(true);
        sxapds.setDebug(true);
        sxapds.setUser("root");
        sxapds.setPassword("root");
        sxapds.setMinSize(0);
        sxapds.setMaxSize(200);

        StandardXADataSource xads = new StandardXADataSource();
        xads.setDriverName("com.mysql.jdbc.Driver");
        xads.setUrl("jdbc:mysql://localhost/test");
        xads.setUser("root");
        xads.setPassword("root");
        xads.setTransactionManager(tm);

        sxapds.setDataSource(xads);
        sxapds.setDataSourceName("XADataSource");


        pm = new EntityManagerImpl(null) {

            protected DataSource getDataSource() {
                return sxapds;
            }
        };
    }

    @Test
    public void testRollback() throws Exception {
        tm.begin();
        Connection connection3 = pm.getConnection();
        Statement stm = connection3.createStatement();
        int id2 = new Random().nextInt();
        stm.execute("INSERT INTO ACCOUNT VALUES(" + id2 + ",'Lisa', 'Begin', 'clinton.begin@ibatis.com')");
        tm.rollback();
    }

    @Test
    public void testInsert() throws Exception {
        tm.begin();
        Connection connection = pm.getConnection();
        Statement stm = connection.createStatement();
        int id1 = new Random().nextInt();
        int id2 = new Random().nextInt();
        stm.execute("INSERT INTO ACCOUNT VALUES(" + id1 + ",'Yang Yong1', 'Begin', 'clinton.begin@ibatis.com')");
        Connection connection2 = pm.getConnection();
        stm = connection2.createStatement();
        stm.execute("INSERT INTO ACCOUNT VALUES(" + id2 + ",'Yang Yong2', 'Begin', 'clinton.begin@ibatis.com')");
        tm.commit();

        tm.begin();
        Connection connection3 = pm.getConnection();
        stm = connection3.createStatement();
        id2 = new Random().nextInt();
        stm.execute("INSERT INTO ACCOUNT VALUES(" + id2 + ",'Lisa', 'Begin', 'clinton.begin@ibatis.com')");
        tm.rollback();

    }

    @Test
    public void testBigTransaction() throws Exception {
        tm.begin();
        int i = 100;
        Exception e = null;
        while (i++ < 200) {
            try {
                Connection connection = pm.getConnection();
                Statement stm = connection.createStatement();
                int id1 = i++;
                int id2 = i++;
                stm.execute("INSERT INTO ACCOUNT VALUES(" + id1 + ",'Yang Yong1', 'Begin', 'clinton.begin@ibatis.com')");
                Connection connection2 = pm.getConnection();
                stm = connection2.createStatement();
                stm.execute("INSERT INTO ACCOUNT VALUES(" + id2 + ",'Yang Yong2', 'Begin', 'clinton.begin@ibatis.com')");
            }
            catch (Exception ex) {
                e = ex;
            }

        }
        if(e == null) {
            tm.commit();
        }
        else {
            tm.rollback();
            throw e;
        }
    }

    @Test
    public void testSuspend() throws Exception {
        tm.begin();
        Connection connection = pm.getConnection();
        Statement stm = connection.createStatement();
        int id1 = new Random().nextInt();
        stm.execute("INSERT INTO ACCOUNT VALUES(" + id1 + ",'Yang Yong1', 'Begin', 'clinton.begin@ibatis.com')");

        Transaction tx = tm.suspend();
        tm.begin();
        int id2 = new Random().nextInt();
        Connection connection2 = pm.getConnection();
        stm = connection2.createStatement();
        stm.execute("INSERT INTO ACCOUNT VALUES(" + id2 + ",'Yang Yong2', 'Begin', 'clinton.begin@ibatis.com')");
        tm.rollback();

        tm.resume(tx);
        tm.commit();

        tm.begin();
        Connection connection3 = pm.getConnection();
        stm = connection3.createStatement();
        id2 = new Random().nextInt();
        stm.execute("INSERT INTO ACCOUNT VALUES(" + id2 + ",'Lisa', 'Begin', 'clinton.begin@ibatis.com')");
        tm.rollback();
    }

    @Test
    public void testSetRollbackOnly() throws Exception {
        tm.begin();
        Connection connection3 = pm.getConnection();
        Statement stm = connection3.createStatement();
        int id2 = new Random().nextInt();
        stm.execute("INSERT INTO ACCOUNT VALUES(" + id2 + ",'Lisa', 'Begin', 'clinton.begin@ibatis.com')");
        tm.setRollbackOnly();
        try {
            tm.commit();
        }
        catch(RollbackException e) {
            // rollback only
            tm.rollback();
        }
    }

    public static void main(String[] args) {

    }
}
