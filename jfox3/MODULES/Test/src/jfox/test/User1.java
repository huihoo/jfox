package jfox.test;

import org.jfox.framework.annotation.Constant;
import org.jfox.framework.annotation.Service;
import org.jfox.framework.annotation.Inject;
import org.jfox.framework.component.ComponentContext;
import org.jfox.framework.component.ComponentInstantiation;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
@Service(
        id = "User1")
public class User1 implements IUser, ComponentInstantiation {

    @Inject(id = "UserManager")
    IUserManager userMgr;

    @Constant("World")
    private String name = "User1";

    public User1() {
    }


    /**
     * Component 实例化之后的回调方法
     * 可以做实例化之后，set Property 之前的准备工作
     *
     * @param componentContext Component context
     */
    public void postContruct(ComponentContext componentContext) {
    }

    /**
     * Component 属性设置完毕之后的回调方法
     * 负责做Properties Set 之后的检查工作，以及做 init 操作
     */
    public void postPropertiesSet() {
        userMgr.addUser(this);
    }

    public String getName() {
        return name;
    }

    public static void main(String[] args) {

    }
}
