/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test;

import org.jfox.framework.annotation.ComponentBean;
import org.jfox.framework.annotation.Constant;
import org.jfox.framework.annotation.Inject;
import org.jfox.framework.component.ComponentContext;
import org.jfox.framework.component.ComponentUnregistration;
import org.jfox.framework.event.ModuleEvent;
import org.jfox.framework.event.ModuleListener;

/**
 *
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
@ComponentBean(id="User2")
public class User2 implements IUser2, ComponentUnregistration, ModuleListener {

    @Inject(id = "UserManager")
    IUserManager userMgr;

    @Constant("Yang Yong")
    private String name = "User1";

    private String password = "*****";

    public User2() {
    }

    /**
     * Component 实例化之后的回调方法
     * 可以做实例化之后，set Property 之前的准备工作
     *
     * @param componentContext Component context
     */
    public void postContruct(ComponentContext componentContext) {
        System.out.println("instantiated");
    }

    /**
     * Component 属性设置完毕之后的回调方法
     * 负责做Properties Set 之后的检查工作，以及做 init 操作
     */
    public void postInject() {
        System.out.println("postPropertiesSet");
        userMgr.addUser(this);
    }


    public boolean preUnregister(ComponentContext context) {
        userMgr.removeUser(this);
        return true;
    }

    public void postUnregister() {
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void moduleChanged(ModuleEvent moduleEvent) {
        System.out.println("moduleChanged: " + moduleEvent);
    }

    public static void main(String[] args) {

    }
}
