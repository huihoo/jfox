package org.jfox.ejb3.security;

import java.security.Principal;
import java.security.acl.Group;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.io.Serializable;
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
    private static final String ANONYMOUS_USER = "EasyBeans/Anonymous";

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

    /**
     * caller subject in run-as mode<br>
     * In run-as case, the run-as subject is set as the current subject, and the
     * previous one is kept.<br>
     * This previous subject is used to get the caller on the run-as bean.
     */
    private Subject callerInRunAsModeSubject = null;

    /**
     * Default private constructor.
     */
/*    public SecurityContext() {

    }*/

    /**
     * Build a security context with the given subject.
     * @param subject the given subject.
     */
    public SecurityContext(final Subject subject) {
        this.subject = subject;
    }

    /**
     * Enters in run-as mode with the given subject.<br>
     * The previous subject is stored and will be restored when run-as mode will
     * be ended.
     * @param runAsSubject the subject to used in run-as mode.
     * @return the previous subject.
     */
    public Subject enterRunAs(final Subject runAsSubject) {
        // keep previous
        this.callerInRunAsModeSubject = subject;

        // update the new one
        this.subject = runAsSubject;

        // return previous.
        return callerInRunAsModeSubject;
    }

    /**
     * Ends the run-as mode and then restore the context stored by container.
     * @param oldSubject subject kept by container and restored.
     */
    public void endsRunAs(final Subject oldSubject) {
        this.subject = oldSubject;

        // cancel caller of run-as subject (run-as mode has ended)
        this.callerInRunAsModeSubject = null;
    }

    /**
     * Gets the caller's principal.
     * @param runAsBean if true, the bean is a run-as bean.
     * @return principal of the caller.
     */
    public Principal getCallerPrincipal(final boolean runAsBean) {
        Subject subject = null;

        // in run-as mode, needs to return callerInRunAsModeSubject's principal.
        if (runAsBean && callerInRunAsModeSubject != null) {
            subject = this.callerInRunAsModeSubject;
        } else {
            subject = this.subject;
        }

        // Then, takes the first principal found. (which is not a role)
        for (Principal principal : subject.getPrincipals(Principal.class)) {
            if (!(principal instanceof Group)) {
                return principal;
            }
        }

        // Principal was not found, severe problem as it should be there. Maybe
        // the subject was not built correctly.
        logger.error("No principal found in the current subject. Authentication should have failed when populating subject");
        throw new IllegalStateException(
                "No principal found in the current subject. Authentication should have failed when populating subject");
    }

    /**
     * Gets the caller's roles.
     * @param runAsBean if true, the bean is a run-as bean.
     * @return list of roles of the caller.
     */
    public List<? extends Principal> getCallerRolesList(final boolean runAsBean) {
        Subject subject = null;

        // in run-as mode, needs to return callerInRunAsModeSubject's principal.
        if (runAsBean && callerInRunAsModeSubject != null) {
            subject = this.callerInRunAsModeSubject;
        } else {
            subject = this.subject;
        }

        // Then, takes all the roles found in this principal.
        for (Principal principal : subject.getPrincipals(Principal.class)) {
            if (principal instanceof Group) {
                return Collections.list(((Group) principal).members());
            }
        }

        // Principal was not found, severe problem as it should be there. Maybe
        // the subject was not built correctly.
        logger.error("No role found in the current subject. Authentication should have failed when populating subject");
        throw new IllegalStateException(
                "No role found in the current subject. Authentication should have failed when populating subject");
    }

    /**
     * Gets the caller's roles.
     * @param runAsBean if true, the bean is a run-as bean.
     * @return array of roles of the caller.
     */
    public Principal[] getCallerRoles(final boolean runAsBean) {
        List<? extends Principal> callerRoles = getCallerRolesList(runAsBean);
        return callerRoles.toArray(new Principal[callerRoles.size()]);
    }

    /**
     * Build an anonymous subject when no user is authenticated.<br>
     * This is required as getCallerPrincipal() should never return null.
     * @return anonymous subject.
     */
    private static Subject buildAnonymousSubject() {
        return buildSubject(ANONYMOUS_USER, new String[] {ANONYMOUS_ROLE});
    }


    /**
     * Build a subject with the given user name and the list of roles.<br>
     * @param userName given username
     * @param roleArray given array of roles.
     * @return built subject.
     */
    public static Subject buildSubject(final String userName, final String[] roleArray) {
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
