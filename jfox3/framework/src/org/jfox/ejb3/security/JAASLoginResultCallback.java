package org.jfox.ejb3.security;

import java.util.List;
import java.util.ArrayList;
import javax.security.auth.callback.Callback;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class JAASLoginResultCallback implements Callback {

    private String principalId;

    private List<String> roles = new ArrayList<String>();

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void addRole(String role){
        roles.add(role);
    }

    public void removeRole(String role){
        roles.remove(role);
    }

    public static void main(String[] args) {

    }
}
