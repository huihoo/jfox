/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejb3.callback;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public abstract class AbstractCalculatorBean implements CalculatorRemote, CalculatorLocal {

    @PostConstruct
    public void superPostConstruct(){
        System.out.println(AbstractCalculatorBean.class.getName() + ".superPostConstruct!");
    }

    @PreDestroy
    public void preDestroy(){
        System.out.println(AbstractCalculatorBean.class.getName() + ".PreDestroy!");
    }

    @PreDestroy
    public void superPreDestroy(){
        System.out.println(AbstractCalculatorBean.class.getName() + ".superPreDestroy!");
    }

}
