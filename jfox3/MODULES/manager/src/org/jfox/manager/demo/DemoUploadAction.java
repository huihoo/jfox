/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.manager.demo;

import org.jfox.mvc.ActionSupport;
import org.jfox.mvc.InvocationContext;
import org.jfox.mvc.Invocation;
import org.jfox.mvc.FileUploaded;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.annotation.ActionMethod;
import org.jfox.framework.annotation.Service;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id = "demoupload")
public class DemoUploadAction extends ActionSupport {

    @ActionMethod(successView = "demo/upload.vhtml")
    public void doGetView(InvocationContext invocationContext) throws Exception {
        // donothing
    }

    @ActionMethod(successView = "demo/upload.vhtml", invocationClass = UploadInvocation.class)
    public void doPostUpload(InvocationContext invocationContext) throws Exception {
        UploadInvocation invocation = (UploadInvocation)invocationContext.getInvocation();
        FileUploaded uploadFile = invocation.getUploadFile();
        String filename = uploadFile.getFilename();
        int size = uploadFile.getContent().length;
        String content = new String(uploadFile.getContent());
        // escape html tag
        content = content.replace("<","&lt;");

        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("filename",filename);
        pageContext.setAttribute("size", size);
        pageContext.setAttribute("content", content);
    }

    public static class UploadInvocation extends Invocation {

        private FileUploaded uploadFile;

        public FileUploaded getUploadFile() {
            return uploadFile;
        }

        public void setUploadFile(FileUploaded uploadFile) {
            this.uploadFile = uploadFile;
        }
    }

}
