package org.jfox.webservice.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.jfox.ejb3.remote.ContainerInvoker;
import org.jfox.framework.ComponentId;
import org.jfox.framework.Framework;
import org.jfox.mvc.WebContextLoader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create May 5, 2008 10:13:43 PM
 */
public class JFoxHessianServlet extends HttpServlet {

    public void init(ServletConfig config) throws ServletException {
        super.init(config);    //To change body of overridden methods use File | Settings | File Templates.

    }

    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        super.doPost(httpServletRequest, httpServletResponse);    //To change body of overridden methods use File | Settings | File Templates.
    }

    private ContainerInvoker getHessianContainerInvoker() throws ServletException {
        Framework framework = WebContextLoader.getManagedFramework();
/*
        Collection<ContainerInvoker> containerInvokers = framework.getSystemModule().findComponentByInterface(ContainerInvoker.class);
        for(ContainerInvoker containerInvoker : containerInvokers){
            if(containerInvoker instanceof HessianContainerInvoker) {
                return containerInvoker;
            }
        }
        throw new ServletException("HessianServlet is not installed.");
*/
        try {
            return (ContainerInvoker) framework.getSystemModule().getComponent(new ComponentId(HessianContainerInvoker.class.getSimpleName()));
        }
        catch (Exception e) {
            throw new ServletException("HessianServlet is not installed.", e);
        }
    }

    private void handleHessianRequest(InputStream in, OutputStream out) throws ServletException {
        try {
            Hessian2Input hessian2Input = new Hessian2Input(in);
            Object inObj = hessian2Input.readObject();

            ContainerInvoker containerInvoker = getHessianContainerInvoker();
            Object obj = null;//containerInvoker.invokeEJB();
            Hessian2Output hessian2Output = new Hessian2Output(out);
            hessian2Output.writeObject(obj);
        }
        catch (Exception e) {
            throw new ServletException("Hessian serialize failed!", e);
        }
    }

    public static void main(String[] args) {

    }
}
