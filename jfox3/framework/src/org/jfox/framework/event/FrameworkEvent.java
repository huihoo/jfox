/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.framework.event;

import java.util.EventObject;

import org.jfox.framework.Framework;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class FrameworkEvent extends EventObject {


    public FrameworkEvent(Object source) {
        super(source);
    }

    public Framework getFramework(){
        return (Framework)getSource();
    }

    public static void main(String[] args) {

    }
}
