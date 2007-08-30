package jfox.platform.function.action;

import javax.ejb.EJB;

import jfox.platform.function.bo.ModuleBO;
import jfox.platform.function.entity.Module;
import jfox.platform.infrastructure.SuperAction;
import org.jfox.framework.annotation.Service;
import org.jfox.mvc.Invocation;
import org.jfox.mvc.InvocationContext;
import org.jfox.mvc.annotation.ActionMethod;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id = "module")
public class ModuleAction extends SuperAction {

    @EJB
    ModuleBO moduleBO;

    @ActionMethod(name = "newview", successView = "function/new_module.vhtml")
    public void newViewModule(InvocationContext invocationContext) throws Exception {

    }

    @ActionMethod(name = "list", successView = "function/list_module.vhtml")
    public void listModule(InvocationContext invocationContext) throws Exception {

    }

    @ActionMethod(name = "editview", successView = "function/edit_module.vhtml", invocationClass = EditModuleInvocation.class)
    public void editViewModule(InvocationContext invocationContext) throws Exception {
        EditModuleInvocation invocation = (EditModuleInvocation)invocationContext.getInvocation();
        Module module = moduleBO.getModuleById(invocation.getId());
        invocationContext.getPageContext().setAttribute("module", module);

    }


    @ActionMethod(name = "new", successView = "function/new_module.vhtml", invocationClass = NewModuleInvocation.class)
    public synchronized void newModule(InvocationContext invocationContext) throws Exception {
        NewModuleInvocation invocation = (NewModuleInvocation)invocationContext.getInvocation();
        Module module = new Module();
        module.setBindAction(invocation.getBindAction());
        module.setIcon(invocation.getIcon());
        module.setCreator("Administrator");
        module.setDescription(invocation.getDescription());
        module.setIcon(invocation.getIcon());
        String localName = "";
        if (invocation.getLocalName_en_US() != null && invocation.getLocalName_en_US().trim().length() > 0) {
            localName += "en_US=" + invocation.getLocalName_en_US();
        }
        if (invocation.getLocalName_zh_CN() != null && invocation.getLocalName_zh_CN().trim().length() > 0) {
            if (localName.length() > 0) {
                localName += "\n";
            }
            localName += "zh_CN=" + invocation.getLocalName_zh_CN();
        }
        if (invocation.getLocalName_zh_TW() != null && invocation.getLocalName_zh_TW().trim().length() > 0) {
            if (localName.length() > 0) {
                localName += "\n";
            }
            localName += "zh_TW=" + invocation.getLocalName_zh_TW();
        }
        module.setLocalName(localName);
        module.setName(invocation.getName());
        module.setPriority(invocation.getPriority());
        module.setVstatus(invocation.getVstatus());
        moduleBO.newModule(module);
    }

    public static class EditModuleInvocation extends Invocation {
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    public static class NewModuleInvocation extends Invocation {
        private String name;
        private String localName_en_US;
        private String localName_zh_CN;
        private String localName_zh_TW;

        private String bindAction;
        private String icon;
        private int priority;
        private int vstatus;

        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocalName_en_US() {
            return localName_en_US;
        }

        public void setLocalName_en_US(String localName_en_US) {
            this.localName_en_US = localName_en_US;
        }

        public String getLocalName_zh_CN() {
            return localName_zh_CN;
        }

        public void setLocalName_zh_CN(String localName_zh_CN) {
            this.localName_zh_CN = localName_zh_CN;
        }

        public String getLocalName_zh_TW() {
            return localName_zh_TW;
        }

        public void setLocalName_zh_TW(String localName_zh_TW) {
            this.localName_zh_TW = localName_zh_TW;
        }

        public String getBindAction() {
            return bindAction;
        }

        public void setBindAction(String bindAction) {
            this.bindAction = bindAction;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public int getVstatus() {
            return vstatus;
        }

        public void setVstatus(int vstatus) {
            this.vstatus = vstatus;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
