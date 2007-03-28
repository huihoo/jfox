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
