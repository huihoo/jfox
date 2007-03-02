package net.sourceforge.jfox.ejb3;

import java.io.Serializable;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class EJBObjectId implements Serializable {

    private String ejbName;
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
}
