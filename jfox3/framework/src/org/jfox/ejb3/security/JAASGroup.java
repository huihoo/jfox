package org.jfox.ejb3.security;

import java.security.acl.Group;
import java.security.Principal;
import java.io.Serializable;
import java.util.Vector;
import java.util.Enumeration;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class JAASGroup implements Group, Serializable {

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 7035827226889396034L;

    /**
     * Name of this group.
     */
    private String groupName = null;

    /**
     * Members of this group.
     */
    private Vector<Principal> members = null;

    /**
     * Build a new group with the following name.
     *
     * @param groupName name of the group
     */
    public JAASGroup(final String groupName) {
        this.groupName = groupName;
        this.members = new Vector<Principal>();
    }

    /**
     * Compares this principal to the specified object. Returns true if the
     * object passed in matches the principal represented by the implementation
     * of this interface.
     *
     * @param another principal to compare with.
     * @return true if the principal passed in is the same as that encapsulated
     *         by this principal, and false otherwise.
     */
    @Override
    public boolean equals(final Object another) {
        if (!(another instanceof Group)) {
            return false;
        }
        // else
        return groupName.equals(((Group)another).getName());
    }

    /**
     * Returns a string representation of this principal.
     *
     * @return a string representation of this principal.
     */
    @Override
    public String toString() {
        return "Principal[" + groupName + "]";
    }

    /**
     * Returns a hashcode for this principal.
     *
     * @return a hashcode for this principal.
     */
    @Override
    public int hashCode() {
        return groupName.hashCode();
    }

    /**
     * Returns the name of this principal.
     *
     * @return the name of this principal.
     */
    public String getName() {
        return groupName;
    }

    /**
     * Adds the specified member to the group.
     *
     * @param user the principal to add to this group.
     * @return true if the member was successfully added, false if the principal
     *         was already a member.
     */
    public boolean addMember(final Principal user) {
        if (isMember(user)) {
            return false;
        }
        // else
        members.add(user);
        return true;
    }

    /**
     * Removes the specified member from the group.
     *
     * @param user the principal to remove from this group.
     * @return true if the principal was removed, or false if the principal was
     *         not a member.
     */
    public boolean removeMember(final Principal user) {
        if (!isMember(user)) {
            return false;
        }
        // else
        members.remove(user);
        return true;
    }

    /**
     * Returns true if the passed principal is a member of the group. This
     * method does a recursive search, so if a principal belongs to a group
     * which is a member of this group, true is returned.
     *
     * @param member the principal whose membership is to be checked.
     * @return true if the principal is a member of this group, false otherwise.
     */
    public boolean isMember(final Principal member) {
        return members.contains(member);
    }

    /**
     * Returns an enumeration of the members in the group. The returned objects
     * can be instances of either Principal or Group (which is a subclass of
     * Principal).
     *
     * @return an enumeration of the group members.
     */
    public Enumeration<? extends Principal> members() {
        return members.elements();
    }

}
