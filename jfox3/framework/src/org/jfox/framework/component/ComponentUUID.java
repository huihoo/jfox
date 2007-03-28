package org.jfox.framework.component;

/**
 * ComponentUUID 用来定位一个Component实例
 * 在 ComponentMeta返回Component的动态代理引用时，包括了一个ComponentUUID对象，
 * 以便在 InvocationHandler 执行时可以更过ComponentUUID找到真实的Component对象
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
class ComponentUUID {

    private String uuid;

    public ComponentUUID(String uuid) {
        this.uuid = uuid;
    }

    public String toString() {
        return uuid;
    }

    public static void main(String[] args) {

    }
}
