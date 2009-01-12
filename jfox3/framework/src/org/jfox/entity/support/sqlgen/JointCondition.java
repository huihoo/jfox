package org.jfox.entity.support.sqlgen;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create Jul 6, 2008 10:18:56 PM
 */
public class JointCondition implements Condition{

    private Condition condition1;
    private Condition condition2;
    private JointOperator jointOperator;

    public JointCondition(Condition condition1, JointOperator jointOperator, Condition cOndition2) {
        this.condition1 = condition1;
        this.jointOperator = jointOperator;
        this.condition2 = cOndition2;
    }

    public Condition getCondition1() {
        return condition1;
    }

    public Condition getCondition2() {
        return condition2;
    }

    public JointOperator getJointOperator() {
        return jointOperator;
    }

    public String toString() {
        return getSQLTemplateString();
    }

    public String getSQLTemplateString() {
        return "(" + condition1.toString() + " " + jointOperator.getOperator() + " " + condition2.toString() + ")";
    }

    public Condition and(final Condition condition) {
        return new JointCondition(this, JointOperator.AND, condition);
    }

    public Condition or(final Condition condition) {
        return new JointCondition(this, JointOperator.OR, condition);
    }

    public static void main(String[] args) {

    }
}
