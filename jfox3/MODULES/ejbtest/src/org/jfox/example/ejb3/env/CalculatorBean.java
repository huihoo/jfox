package org.jfox.example.ejb3.env;

import javax.ejb.Stateless;
import javax.ejb.Remote;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.EJBException;
import javax.annotation.Resource;
import javax.naming.Context;

import net.sourceforge.jfox.ejb3.naming.JNDIContextHelper;

@Stateless(name = "env.CalculatorBean")
@Remote
@Local
public class CalculatorBean implements CalculatorRemote, CalculatorLocal {

    @Resource
    SessionContext sessionContext;

    public void remember(int number) {
        try {
            Context ctx = (Context)JNDIContextHelper.getInitalContext().lookup("java:comp/env");
            ctx.bind("memory", number);
        }
        catch (Exception e) {
            throw new EJBException(e);
        }
    }

    public int takeout() {
        try {
            int n1 = (Integer)JNDIContextHelper.getInitalContext().lookup("java:comp/env/memory");
            int n2 = (Integer)sessionContext.lookup("memory");
            if (n1 != n2) {
                throw new EJBException("number not equals");
            }
            return n1;

        }
        catch (Exception e) {
            throw new EJBException(e);
        }
    }

    public void clear() {
        try {
            Context ctx = (Context)JNDIContextHelper.getInitalContext().lookup("java:comp/env");
            ctx.unbind("memory");
        }
        catch (Exception e) {
            throw new EJBException(e);
        }
    }

}
