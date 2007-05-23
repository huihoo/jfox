/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejb3.webservice;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import jfox.test.ejb3.stateless.Calculator;

@Stateless(name = "webservice.CalculatorBean")
@Remote
@Local
@WebService(endpointInterface = "jfox.test.ejb3.webservice.Calculator", serviceName = "CalculatorBean")
public class CalculatorBean implements Calculator {

    public int add(int x, int y) {
        return x + y;
    }

    public int subtract(int x, int y) {
        return x - y;
    }

}
