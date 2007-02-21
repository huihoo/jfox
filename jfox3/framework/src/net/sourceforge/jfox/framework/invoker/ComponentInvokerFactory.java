package net.sourceforge.jfox.framework.invoker;

/**
 * ComponentInvoker 工厂
 *
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public class ComponentInvokerFactory {

    /**
     * ComponentInvoker 类型
     */
    public static enum TYPE{
        Reflect,
        Transactable
    }

    private static ComponentInvoker reflectComponentInvoker = new ReflectComponentInvoker();

    /**
     * 根据类型获得对应的 ComponentInvoker
     *
     * @param type ComponentInvoker类型，有ComponentFactory根据Component类型选择
     */
    public static ComponentInvoker getComponentInvoker(TYPE type){
        switch(type) {
            case Reflect: return reflectComponentInvoker;
            default: return reflectComponentInvoker; 
        }
    }

    public static void main(String[] args) {

    }
}
