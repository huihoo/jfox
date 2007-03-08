package org.jfox.example.ejb3.callback;

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
