package org.jfox.entity.support.sqlgen;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create Jul 6, 2008 10:34:26 PM
 */
public interface Condition {

    String getSQLTemplateString();

    public Condition and(Condition condition);

    public Condition or(Condition condition);

    public static enum OPERATOR {
        GREATER(">"),
        GREATER_EQUAL(">="),
        EQUAL("="),
        LESS("<"),
        LESS_EQUAL("<="),
        LIKE("LIKE");

        private String operator;
        OPERATOR(String operator) {
            this.operator = operator;
        }

        public String getOperator() {
            return operator;
        }

        public String toString() {
            return getOperator();
        }
    }

    public enum JointOperator {
        AND("AND"), OR("OR");

        private String operator;

        JointOperator(String operator) {
            this.operator = operator;
        }

        public String getOperator() {
            return operator;
        }

        public String toString() {
            return operator.toString();
        }
    }
}
