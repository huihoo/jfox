package org.jfox.ejb3.security;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.Subject;

import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public class SecurityContext implements Serializable {
    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 6612085599241360430L;

    /**
     * Logger.
     */
    private static Logger logger = Logger.getLogger(SecurityContext.class);

    /**
     * Anonymous user name.
     */
    private static final String ANONYMOUS_USER = "Anonymous";

    /**
     * Anonymous role.
     */
    private static final String ANONYMOUS_ROLE = "anonymous";

    /**
     * Anonymous subject (not authenticated).
     */
    private static final Subject ANONYMOUS_SUBJECT = buildAnonymousSubject();

    /**
     * Current subject (subject that has been authenticated).<br>
     * By default, it is the anonymous subject.
     */
    private Subject subject = ANONYMOUS_SUBJECT;

    public SecurityContext() {
        this(null);
    }

    /**
     * Build a security context with the given subject.
     * @param subject the given subject.
     */
    public SecurityContext(final Subject subject) {
        if(subject == null) {
            this.subject = ANONYMOUS_SUBJECT;
        }
        else {
            this.subject = subject;
        }
    }

    public Subject getSubject() {
        return subject;
    }

    public String getPrincipalName() {
        String username = null;
        for(Principal p : getSubject().getPrincipals()){
            if(!(p instanceof Group)) {
                username = p.getName();
            }
        }
        return username;
    }

    /**
     * Build an anonymous subject when no user is authenticated.<br>
     * This is required as getCallerPrincipal() should never return null.
     * @return anonymous subject.
     */
    private static Subject buildAnonymousSubject() {
        return buildSubject(ANONYMOUS_USER, ANONYMOUS_ROLE);
    }


    /**
     * Build a subject with the given user name and the list of roles.<br>
     * @param userName given username
     * @param roleArray given array of roles.
     * @return built subject.
     */
    public static Subject buildSubject(final String userName, final String... roleArray) {
        List<String> roles = new ArrayList<String>();
        if (roleArray != null) {
            for (String role : roleArray) {
                roles.add(role);
            }
        }
        return buildSubject(userName, roles);
    }

    /**
     * Build a subject with the given user name and the list of roles.<br>
     * @param userName given username
     * @param roleList given list of roles.
     * @return built subject.
     */
    public static Subject buildSubject(final String userName, final List<String> roleList) {
        Subject subject = new Subject();
        return initSubject(subject, userName, roleList);
    }

    /**
     * Build a subject with the given user name and the list of roles.<br>
     * @param subject subject created
     * @param userName given username
     * @param roleList given list of roles.
     * @return built subject.
     */
    public static Subject initSubject(final Subject subject, final String userName, final List<String> roleList) {
        // Add principal name
        Principal namePrincipal = new JAASPrincipal(userName);
        subject.getPrincipals().add(namePrincipal);

        // Add roles for this principal
        Group roles = new JAASGroup("roles");
        if (roleList != null) {
            for (String role : roleList) {
                roles.addMember(new JAASPrincipal(role));
            }
        }
        subject.getPrincipals().add(roles);

        return subject;
    }

}
