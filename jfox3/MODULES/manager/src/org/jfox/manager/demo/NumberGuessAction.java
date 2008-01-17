/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.manager.demo;

import java.util.Random;

import org.jfox.framework.annotation.Service;
import org.jfox.mvc.ActionContext;
import org.jfox.mvc.ActionSupport;
import org.jfox.mvc.Invocation;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.SessionContext;
import org.jfox.mvc.annotation.ActionMethod;

/**
 * number guess action
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id = "numberguess")
public class NumberGuessAction extends ActionSupport {

    @ActionMethod(name="view", successView = "demo/numberguess.vhtml", invocationClass = NumberGuessInvocation.class, httpMethod = ActionMethod.HttpMethod.GET)
    public void doGetView(ActionContext actionContext) throws Exception {
        NumberGuessInvocation invocation = (NumberGuessInvocation)actionContext.getInvocation();

        int count = 0;
        boolean success = false;
        String hint = "";

        SessionContext sessionContext = actionContext.getSessionContext();
        if (!sessionContext.containsAttribute("count")) { //start
            count = 0;
            int answer = Math.abs(new Random().nextInt() % 100) + 1;
            sessionContext.setAttribute("answer", answer);
            sessionContext.setAttribute("count", 0);
        }
        else {
            int answer = (Integer)sessionContext.getAttribute("answer");
            count = (Integer)sessionContext.getAttribute("count");
            int guessNO = invocation.getGuessNO();
            if (guessNO == answer) { // success
                success = true;
                sessionContext.removeAttribute("count");
                sessionContext.removeAttribute("answer");
            }
            else { // failed
                if (guessNO < answer) {
                    hint = "higher";
                }
                else {
                    hint = "lower";
                }
                count++;
                sessionContext.setAttribute("count", count);
            }
        }
        
        PageContext pageContext = actionContext.getPageContext();
        pageContext.setAttribute("success", success);
        pageContext.setAttribute("count", count);
        pageContext.setAttribute("hint", hint);

    }

    @ActionMethod(name="view", successView = "demo/numberguess.vhtml", invocationClass = NumberGuessInvocation.class, httpMethod = ActionMethod.HttpMethod.POST)
    public void doPostView(ActionContext actionContext) throws Exception {
        doGetView(actionContext);
    }

    public static class NumberGuessInvocation extends Invocation {
        private int guessNO = 0;

        public int getGuessNO() {
            return guessNO;
        }

        public void setGuessNO(int guessNO) {
            this.guessNO = guessNO;
        }
    }

    public static void main(String[] args) {

    }
}
