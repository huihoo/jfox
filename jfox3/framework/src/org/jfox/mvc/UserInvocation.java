package org.jfox.mvc;

import org.jfox.mvc.validate.StringValidation;
import org.jfox.mvc.validate.IntegerValidation;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Yang Yong</a>
 */
public class UserInvocation extends Invocation {

    //TODO: 对于 creatUser 和 deleteUser，校验标准不一样

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
