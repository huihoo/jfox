package org.jfox.entity.mapping;

import org.jfox.entity.annotation.ParameterMap;

/**
 * \@MappingColumn field
 * 
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 * @create May 2, 2008 2:17:25 PM
 */
public class MappingColumnEntry extends ColumnEntry{
    private String namedQuery;
    private ParameterMap[] params;

    public String getNamedQuery() {
        return namedQuery;
    }

    public ParameterMap[] getParams() {
        return params;
    }

    public boolean isMappedColumn() {
        return true;
    }

    public void setNamedQuery(String namedQuery) {
        this.namedQuery = namedQuery;
    }

    public void setParams(ParameterMap[] params) {
        this.params = params;
    }
}
