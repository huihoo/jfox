package org.jfox.ejb3;

import java.io.Serializable;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class EJBObjectId implements Serializable {

    /**
     * ejb name
     */
    private String ejbName;

    /**
     * ejb instance id number
     * empty if stateless ebj
     */
    private String ejbId = "";

    public EJBObjectId(String ejbName) {
        this.ejbName = ejbName;
    }

    public EJBObjectId(String ejbName, String ejbId) {
        this.ejbName = ejbName;
        this.ejbId = ejbId;
    }

    public String getEJBName() {
        return ejbName;
    }

    public String getEJBId() {
        return ejbId;
    }

    public String toString() {
        return getEJBName() + (getEJBId().trim().length() == 0 ? "" : "@" + getEJBId());
    }


    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof EJBObjectId)) {
            return false;
        }
        return ((EJBObjectId)obj).ejbId.equals(ejbId) && ((EJBObjectId)obj).ejbName.equals(ejbName); 
    }
}
