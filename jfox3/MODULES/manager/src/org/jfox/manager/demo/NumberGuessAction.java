package org.jfox.manager.demo;

import java.util.Random;

import org.jfox.mvc.ActionSupport;
import org.jfox.mvc.InvocationContext;
import org.jfox.mvc.Invocation;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.SessionContext;
import org.jfox.mvc.annotation.ActionMethod;
import org.jfox.framework.annotation.Service;

/**
 * number guess action
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id = "numberguess")
public class NumberGuessAction extends ActionSupport {

    @ActionMethod(successView = "demo/numberguess.vhtml", invocationClass = NumberGuessInvocation.class)
    public void doGetView(InvocationContext invocationContext) throws Exception {
        NumberGuessInvocation invocation = (NumberGuessInvocation)invocationContext.getInvocation();

        int count = 0;
        boolean success = false;
        String hint = "";

        SessionContext sessionContext = invocationContext.getSessionContext();
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
        
        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("success", success);
        pageContext.setAttribute("count", count);
        pageContext.setAttribute("hint", hint);

    }

    @ActionMethod(successView = "demo/numberguess.vhtml", invocationClass = NumberGuessInvocation.class)
    public void doPostView(InvocationContext invocationContext) throws Exception {
        doGetView(invocationContext);
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
