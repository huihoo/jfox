package jfox.platform.function.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import jfox.platform.infrastructure.LocalNamingAndRefInspectableEntityObject;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Entity(name="T_FUNC_NODE")
public class Node extends LocalNamingAndRefInspectableEntityObject {

    @Column(name="BIND_ACTION")
    private String bindAction;

    @Column(name="MODULE_id")
    private long moduleId;

    @Column(name="MENU")
    private int menu;

    @Column(name="NODE_GROUP")
    private String nodeGroup;

    @Column(name="ICON")
    private String icon;

    @Column(name="TYPE")
    private char type;

    public String getBindAction() {
        return bindAction;
    }

    public void setBindAction(String bindAction) {
        this.bindAction = bindAction;
    }

    public long getModuleId() {
        return moduleId;
    }

    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    public int getMenu() {
        return menu;
    }

    public void setMenu(int menu) {
        this.menu = menu;
    }

    public boolean isMenu(){
        return menu == 1;
    }

    public String getNodeGroup() {
        return nodeGroup;
    }

    public void setNodeGroup(String nodeGroup) {
        this.nodeGroup = nodeGroup;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

}
