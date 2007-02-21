package net.sourceforge.jfox.entity;

import java.io.Serializable;

/**
 * DataObject 接口，所有的对应DB的数据对象都应继承该接口
 * 产生 Map来映射，不区分大小写
 *
 * get方法需要用 @Column 描述
 * 没有用 @Column 描述的字段，仍然可以使用，但是却不会存到数据库中
 *
 * @author <a href="mailto:yy.young@gmail.com">Yang Yong</a>
 */
public interface EntityObject extends Serializable, Cloneable {
    
    Object getColumnValue(String colName);

    void setColumnValue(String colName, Object colValue);

    /**
     * 得到 该 DO 的帮助类
     * 没有实现方法，通过动态代理，获得 DO 的 @DOHelper指定的类
     */
    Object helper();

    Object clone() throws CloneNotSupportedException;
}
