/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.manager.demo;

import org.jfox.mvc.ActionContext;
import org.jfox.mvc.ActionSupport;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.ParameterObject;
import org.jfox.mvc.annotation.Action;
import org.jfox.mvc.annotation.ActionMethod;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Action(name="checkbox")
public class CheckboxAction extends ActionSupport {

    @ActionMethod(name="view", successView = "demo/checkbox.vhtml", httpMethod = ActionMethod.HttpMethod.GET)
    public void doGetView(ActionContext actionContext) throws Exception{
        // do nothing, just show template
    }

    @ActionMethod(name="submit", successView = "demo/checkbox.vhtml", parameterClass = CheckboxParameterObject.class, httpMethod = ActionMethod.HttpMethod.POST)
    public void doPostSubmit(ActionContext actionContext) throws Exception{
        CheckboxParameterObject invocation = (CheckboxParameterObject)actionContext.getParameterObject();
        String[] fruits = invocation.getFruit();
        PageContext pageContext = actionContext.getPageContext();
        pageContext.setAttribute("fruits", fruits);
    }

    public static class CheckboxParameterObject extends ParameterObject {
        String[] fruit = new String[0];

        public String[] getFruit() {
            return fruit;
        }

        public void setFruit(String[] fruit) {
            this.fruit = fruit;
        }
    }

    public static void main(String[] args) {

    }
}
