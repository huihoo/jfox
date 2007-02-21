package net.sourceforge.jfox.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.FlushModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public abstract class QueryExt implements Query {

    public abstract String getName();

    public abstract int executeUpdate();

    public abstract List<?> getResultList();

    public abstract Object getSingleResult() ;

    // javax.persistence.Query
    public Query setFirstResult(int startPosition) {
        return null;
    }

    public Query setFlushMode(FlushModeType flushMode) {
        return null;
    }

    public Query setHint(String hintName, Object value) {
        return null;
    }

    public Query setMaxResults(int maxResult) {
        return null;
    }

    public Query setParameter(String name, Calendar value, TemporalType temporalType) {
        return null;
    }

    public Query setParameter(String name, Date value, TemporalType temporalType) {
        return null;
    }

    public Query setParameter(int position, Calendar value, TemporalType temporalType) {
        return null;
    }

    public Query setParameter(int position, Date value, TemporalType temporalType) {
        return null;
    }

    public Query setParameter(int position, Object value) {
        return null;
    }

    public static void main(String[] args) {

    }
}
