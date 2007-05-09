/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.FlushModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public abstract class QueryExt implements Query {

    protected int startPosition = 0;
    protected int maxResult = Integer.MAX_VALUE;

    public abstract String getName();

    public abstract int executeUpdate();

    public abstract List<?> getResultList();

    public abstract Object getSingleResult();

    public abstract Query setParameter(String name, Object value);

    public Query setFirstResult(int startPosition) {
        if (startPosition > 0) {
            this.startPosition = startPosition;
        }
        return this;
    }

    public Query setMaxResults(int maxResult) {
        if(maxResult > 0) {
            this.maxResult = maxResult;
        }
        return this;
    }

    public int getFirstResult() {
        return startPosition;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public Query setFlushMode(FlushModeType flushMode) {
        return null;
    }

    public Query setHint(String hintName, Object value) {
        throw new UnsupportedOperationException("Query.setHint(String hintName, Object value)");
    }

    public Query setParameter(String name, Calendar value, TemporalType temporalType) {
        throw new UnsupportedOperationException("Query.setParameter(String name, Calendar value, TemporalType temporalType)");
    }

    public Query setParameter(String name, Date value, TemporalType temporalType) {
        throw new UnsupportedOperationException("Query.setParameter(String name, Date value, TemporalType temporalType)");
    }

    public Query setParameter(int position, Calendar value, TemporalType temporalType) {
        throw new UnsupportedOperationException("Query.setParameter(int position, Calendar value, TemporalType temporalType)");
    }

    public Query setParameter(int position, Date value, TemporalType temporalType) {
        throw new UnsupportedOperationException("Query.setParameter(int position, Date value, TemporalType temporalType)");
    }

    public Query setParameter(int position, Object value) {
        throw new UnsupportedOperationException("Query.setParameter(int position, Object value)");
    }

    public static void main(String[] args) {

    }
}
