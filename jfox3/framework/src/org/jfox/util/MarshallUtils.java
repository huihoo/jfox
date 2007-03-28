package org.jfox.util;

import java.rmi.MarshalledObject;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */

public class MarshallUtils {
    public static MarshalledObject marshall(Object obj) {
        if (obj == null) {
            return null;
        }
        else if (obj instanceof MarshalledObject) {
            return (MarshalledObject)obj;
        }
        else {
            try {
                return new MarshalledObject(obj);
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static Object unmarshall(Object obj) {
        if (obj == null) {
            return null;
        }
        else if (obj instanceof MarshalledObject) {
            try {
                return ((MarshalledObject)obj).get();
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        else {
            return obj;
        }
    }

    public static void main(String[] args) {

    }
}
