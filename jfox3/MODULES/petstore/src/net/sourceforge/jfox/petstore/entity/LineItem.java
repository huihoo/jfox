package net.sourceforge.jfox.petstore.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Column;

import net.sourceforge.jfox.entity.annotation.MappedColumn;
import net.sourceforge.jfox.entity.annotation.ParameterMap;
import net.sourceforge.jfox.entity.annotation.EntityHelper;
import net.sourceforge.jfox.petstore.dao.ItemDAOImpl;

/**
 * IineItem is a order line item.
 */
@Entity
@EntityHelper(LineItem.Helper.class)
public interface LineItem extends Serializable {

    @Column(name = "orderid")
    public long getOrderId();
    public void setOrderId(long orderId);

    @Column(name = "linenum")
    public int getLineNumber();
    public void setLineNumber(int lineNumber);

    @Column(name = "itemid")
    public String getItemId();
    public void setItemId(String itemId);

    @Column(name = "unitprice")
    public double getUnitPrice();
    public void setUnitPrice(double unitprice);

    @Column(name = "quantity")
    public int getQuantity();
    public void setQuantity(int quantity);

    @MappedColumn(namedQuery = ItemDAOImpl.GET_ITEM, params = {@ParameterMap(name = "id",value = "$this.getItemId()")})
    public Item getItem();
    public void setItem(Item item);

    public static class Helper {
        public double getTotalPrice(LineItem lineItem) {
            return lineItem.getUnitPrice() * lineItem.getQuantity();
        }
    }
}
