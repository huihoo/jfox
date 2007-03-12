package org.jfox.example.ejb3.webservice;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.jfox.example.ejb3.stateless.CalculatorLocal;
import org.jfox.example.ejb3.stateless.CalculatorRemote;

@Stateless(name = "webservice.CalculatorBean")
@Remote
@Local
@WebService(endpointInterface = "org.jfox.example.ejb3.webservice.Calculator")
public class CalculatorBean implements CalculatorRemote, CalculatorLocal {

    public int add(int x, int y) {
        return x + y;
    }

    public int subtract(int x, int y) {
        return x - y;
    }

}
