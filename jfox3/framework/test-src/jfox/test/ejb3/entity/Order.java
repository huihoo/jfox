package jfox.test.ejb3.entity;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Column;

import org.jfox.entity.annotation.MappingColumn;
import org.jfox.entity.annotation.ParameterMap;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Entity
public class Order {

    @Column(name="id")
    Integer id;

    @Column(name = "createtime")
    String createTime;

    @MappingColumn(namedQuery = OrderDAOImpl.GET_LINEITEMS_BY_ORDER_ID, params = {@ParameterMap(name = "orderid", value="$this.getId()")})
    List<LineItem> lineItems;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }
}
