package net.sourceforge.jfox.mvc;

import net.sourceforge.jfox.mvc.validate.IntegerValidation;
import net.sourceforge.jfox.mvc.validate.StringValidation;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class UserManagementAction extends ActionSupport {

    protected PageContext doGetCreateUserAction(Invocation invocation) throws Exception{
        return null;
    }

    public void doPostCreateUserAction(Invocation invocation) throws Exception {

    }

    public void doGetUpdateUserAction(Invocation invocation) throws Exception {

    }

    public static void main(String[] args) {
        System.out.println(UserInvocation.class.getName());
    }

    public static class UserInvocation extends Invocation {
        //TODO: 对于 creatUser 和 deleteUser，校验标准不一样
        //TODO: set check 之后，还要check其它没有 set 的 field
        //TODO: 最后掉 validateAll

        @IntegerValidation(minValue = 1, maxValue = 65535, nullable = true)
        private int id;

        @StringValidation(minLength = 6, maxLength = 16)
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static void main(String[] args) {

        }
    }

}
