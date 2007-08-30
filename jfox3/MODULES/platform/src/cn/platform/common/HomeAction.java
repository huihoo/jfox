package cn.iservicedesk.common;

import cn.iservicedesk.infrastructure.SuperAction;
import org.jfox.framework.annotation.Service;
import org.jfox.mvc.InvocationContext;
import org.jfox.mvc.annotation.ActionMethod;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id="home")
public class HomeAction extends SuperAction {

    @ActionMethod(name="index",successView = "index.vhtml")
    public void indexPage(InvocationContext invocationContext) throws Exception{

    }

    @ActionMethod(name="today",successView = "new_contact.vhtml")
    public void todayPage(InvocationContext invocationContext) throws Exception{

    }

    
    public static void main(String[] args) {

    }
}
