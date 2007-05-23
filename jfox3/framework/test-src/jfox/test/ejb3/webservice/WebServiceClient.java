/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejb3.webservice;

import org.jfox.webservice.WebServiceHelper;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class WebServiceClient {

    public static void main(String[] args) throws Exception{

        Calculator example = WebServiceHelper.lookupWS("http://localhost:8080/jfox/webservice/CalculatorBean", Calculator.class);

        System.out.println("Web Service invoke Calculator.add(1,1): " + example.add(1,1));
        System.out.println("Web Service invoke Calculator.substract(2,1): " + example.subtract(2,1));
    }
}
