/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.jfox.framework.annotation.Service;
import org.jfox.framework.component.ComponentInitialization;
import org.jfox.framework.event.ModuleListener;
import org.jfox.framework.component.ComponentContext;
import org.jfox.framework.event.ModuleEvent;
import org.jfox.framework.event.ComponentEvent;
import org.jfox.framework.event.ComponentListener;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service
public class UserManager implements IUserManager, ComponentInitialization, ModuleListener, ComponentListener {

    private List<IUser> users = new ArrayList<IUser>();


    public void postContruct(ComponentContext componentContext) {
        
    }

    public void postInject() {
    }

    public void moduleChanged(ModuleEvent moduleEvent) {
        System.out.println("moduleChanged: " + moduleEvent);
    }

    public void componentChanged(ComponentEvent componentEvent) {
        System.out.println("componentChanged: " + componentEvent);
    }

    public void addUser(IUser user) {
        System.out.println("!!!!!! add user: " + user);
        users.add(user);
    }

    public void removeUser(IUser user) {
        System.out.println("!!!!!! remove user: " + user);
        users.remove(user);
    }

    public String listUsers() {
        List<String> list = new ArrayList<String>();
        for(IUser user : users) {
            list.add(user.getName());
        }
        return Arrays.toString(list.toArray(new String[list.size()]));
    }

    public static void main(String[] args) {

    }
}
