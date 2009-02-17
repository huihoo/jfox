package jfox.testcase.jpa;

import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create 2009-2-17 11:25:36
 */
public abstract class HsqlDBSupportTestCase {
    private static Random rRandom = new Random(100);

    private static EntityManager em = null;

    public synchronized static EntityManager getEntityManager(){
        if(em == null) {
            em = Persistence.createEntityManagerFactory("TestHsqlDB").createEntityManager();
        }
        return em;
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        Connection connection = getConnection();
        Statement stm = connection.createStatement();
        createTestTables(stm);
        createTestData(stm);
        System.out.println("HsqlDBSupportTest.beforeClass: memory database initialized.");
    }


    private static Connection getConnection() throws Exception {
        DriverManager.registerDriver(new org.hsqldb.jdbcDriver());
        return DriverManager.getConnection("jdbc:hsqldb:mem:.", "sa", "");
    }

    private static void createTestTables(Statement sStatement) {

        String[] demo = {
                "DROP TABLE Item IF EXISTS;", "DROP TABLE Invoice IF EXISTS;",
                "DROP TABLE Product IF EXISTS;", "DROP TABLE Customer IF EXISTS;",
                "CREATE TABLE Customer(ID INTEGER PRIMARY KEY,FirstName VARCHAR(20),"
                        + "LastName VARCHAR(20),Street VARCHAR(20),City VARCHAR(20));",
                "CREATE TABLE Product(ID INTEGER PRIMARY KEY,Name VARCHAR(20),"
                        + "Price DECIMAL(10,2));",
                "CREATE TABLE Invoice(ID INTEGER PRIMARY KEY,CustomerID INTEGER,"
                        + "Total DECIMAL(10,2), FOREIGN KEY (CustomerId) "
                        + "REFERENCES Customer(ID) ON DELETE CASCADE);",
                "CREATE TABLE Item(InvoiceID INTEGER,Item INTEGER,"
                        + "ProductID INTEGER,Quantity INTEGER,Cost DECIMAL(10,2),"
                        + "PRIMARY KEY(InvoiceID,Item), "
                        + "FOREIGN KEY (InvoiceId) REFERENCES "
                        + "Invoice (ID) ON DELETE CASCADE, FOREIGN KEY (ProductId) "
                        + "REFERENCES Product(ID) ON DELETE CASCADE);"
        };

        for (int i = 0; i < demo.length; i++) {

            // drop table may fail
            try {
                sStatement.execute(demo[i]);
            }
            catch (SQLException e) {
                ;
            }
        }
    }

    private static String createTestData(Statement sStatement) throws SQLException {

        String[] name = {
                "White", "Karsen", "Smith", "Ringer", "May", "King", "Fuller",
                "Miller", "Ott", "Sommer", "Schneider", "Steel", "Peterson",
                "Heiniger", "Clancy"
        };
        String[] firstname = {
                "Mary", "James", "Anne", "George", "Sylvia", "Robert", "Janet",
                "Michael", "Andrew", "Bill", "Susanne", "Laura", "Bob", "Julia",
                "John"
        };
        String[] street = {
                "Upland Pl.", "College Av.", "- 20th Ave.", "Seventh Av."
        };
        String[] city = {
                "New York", "Dallas", "Boston", "Chicago", "Seattle",
                "San Francisco", "Berne", "Oslo", "Paris", "Lyon", "Palo Alto",
                "Olten"
        };
        String[] product = {
                "Iron", "Ice Tea", "Clock", "Chair", "Telephone", "Shoe"
        };
        int max = 50;

        sStatement.execute("SET REFERENTIAL_INTEGRITY FALSE");

        for (int i = 0; i < max; i++) {
            sStatement.execute("INSERT INTO Customer VALUES(" + i + ",'"
                    + random(firstname) + "','" + random(name)
                    + "','" + random(554) + " " + random(street)
                    + "','" + random(city) + "')");
            sStatement.execute("INSERT INTO Product VALUES(" + i + ",'"
                    + random(product) + " " + random(product)
                    + "'," + (20 + 2 * random(120)) + ")");
            sStatement.execute("INSERT INTO Invoice VALUES(" + i + ","
                    + random(max) + ",0.0)");

            for (int j = random(20) + 2; j >= 0; j--) {
                sStatement.execute("INSERT INTO Item VALUES(" + i + "," + j
                        + "," + random(max) + ","
                        + (1 + random(24)) + ",1.5)");
            }
        }

        sStatement.execute("SET REFERENTIAL_INTEGRITY TRUE");
        sStatement.execute("UPDATE Product SET Price=ROUND(Price*.1,2)");
        sStatement.execute("UPDATE Item SET Cost=Cost*" + "(SELECT Price FROM Product prod WHERE ProductID=prod.ID)");
        sStatement.execute("UPDATE Invoice SET Total=(SELECT SUM(Cost*"
                + "Quantity) FROM Item WHERE InvoiceID=Invoice.ID)");

        return ("SELECT * FROM Customer");
    }

    private static String random(String[] s) {
        return s[random(s.length)];
    }

    /**
     * Method declaration
     *
     * @param i
     * @return
     */
    private static int random(int i) {

        i = rRandom.nextInt() % i;

        return i < 0 ? -i
                : i;
    }

    @AfterClass
    public static void afterClass() throws Exception {
        Statement stm = getConnection().createStatement();
        stm.execute("SHUTDOWN"); // close connection
//        connect.close();
        System.out.println("HsqlDBSupportTest.afterClass: memory database destroied.");
    }

    @Test
    public void testInitilaizingSuccessful() throws Exception{
        Statement stm = getConnection().createStatement();
        ResultSet rset = stm.executeQuery("select * from Customer");
        Assert.assertEquals(rset.next(), true);
    }
}
