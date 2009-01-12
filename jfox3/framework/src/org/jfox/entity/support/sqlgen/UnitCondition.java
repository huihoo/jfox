package org.jfox.entity.support.sqlgen;

/**
 * 表示一个SQL条件，用来构造 SQL 语句
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create Jul 6, 2008 10:12:07 PM
 */
public class UnitCondition implements Condition {

    private String columnName;
    private OPERATOR operator;

    public UnitCondition(String columnName, OPERATOR operator) {
        this.columnName = columnName;
        this.operator = operator;
    }

    public String getColumnName() {
        return columnName;
    }

    public OPERATOR getOperator() {
        return operator;
    }

    public Condition and(final Condition condition) {
        return new JointCondition(this, JointOperator.AND, condition);
    }

    public Condition or(final Condition condition) {
        return new JointCondition(this, JointOperator.OR, condition);
    }

    public String toString() {
        return getSQLTemplateString();
    }

    public String getSQLTemplateString() {
        return "(" + columnName + " " + operator.toString() + " $" + columnName + ")";
    }

    public static void main(String[] args) {
        System.out.println(new UnitCondition("ID", OPERATOR.GREATER).or(new UnitCondition("NAME", OPERATOR.EQUAL)).and(new UnitCondition("age", OPERATOR.EQUAL)));
    }
}
