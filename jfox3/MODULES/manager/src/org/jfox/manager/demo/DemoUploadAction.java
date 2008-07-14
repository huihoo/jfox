/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.manager.demo;

import org.jfox.mvc.ActionContext;
import org.jfox.mvc.ActionSupport;
import org.jfox.mvc.FileUploaded;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.ParameterObject;
import org.jfox.mvc.annotation.Action;
import org.jfox.mvc.annotation.ActionMethod;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Action(name = "demoupload")
public class DemoUploadAction extends ActionSupport {

    @ActionMethod(name="view", successView = "demo/upload.vhtml", httpMethod = ActionMethod.HttpMethod.GET)
    public void doGetView(ActionContext actionContext) throws Exception {
        // donothing
    }
    
    @ActionMethod(name="upload", successView = "demo/upload.vhtml", parameterClass = UploadParameterObject.class, httpMethod = ActionMethod.HttpMethod.POST)
    public void doPostUpload(ActionContext actionContext) throws Exception {
        UploadParameterObject invocation = (UploadParameterObject)actionContext.getParameterObject();
        FileUploaded uploadFile = invocation.getUploadFile();
        String filename = uploadFile.getFilename();
        int size = uploadFile.getContent().length;
        String content = new String(uploadFile.getContent());
        // escape html tag
        content = content.replace("<","&lt;");

        PageContext pageContext = actionContext.getPageContext();
        pageContext.setAttribute("filename",filename);
        pageContext.setAttribute("size", size);
        pageContext.setAttribute("content", content);
    }

    public static class UploadParameterObject extends ParameterObject {

        private FileUploaded uploadFile;

        public FileUploaded getUploadFile() {
            return uploadFile;
        }

        public void setUploadFile(FileUploaded uploadFile) {
            this.uploadFile = uploadFile;
        }
    }

}
