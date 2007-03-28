package org.jfox.framework;

/**
 * ComponentId 由Module和Name共同组成
 * Component之间相互引用的时候，均是通过 ComponentId 引用
 * 这样的弱引用使得 Module 动态部署以及引用的切换成为可能
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class ComponentId {
    /**
     * 注册Component时使用的名称，用来区分同一个Component类型的不同注册
     */
    private String name = null;

    public ComponentId(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

/*
    public String getModule() {
        return module;
    }
*/

    public String toString() {
        return "ComponentId {" + getName() + "}";
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComponentId that = (ComponentId)o;

        return name.equals(that.name);
    }

    public int hashCode() {
        return name.hashCode();
    }

    public static void main(String[] args) {

    }
}
