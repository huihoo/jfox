package net.sourceforge.jfox.manager;

import net.sourceforge.jfox.mvc.ActionSupport;
import net.sourceforge.jfox.mvc.InvocationContext;
import net.sourceforge.jfox.mvc.Invocation;
import net.sourceforge.jfox.mvc.FileUploaded;
import net.sourceforge.jfox.mvc.PageContext;
import net.sourceforge.jfox.mvc.annotation.ActionMethod;
import net.sourceforge.jfox.framework.annotation.Service;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
@Service(id = "demoupload")
public class DemoUploadAction extends ActionSupport {

    @ActionMethod(successView = "template/upload.vhtml")
    public void doGetView(InvocationContext invocationContext) throws Exception {
        // donothing
    }

    @ActionMethod(successView = "template/upload.vhtml", invocationClass = UploadInvocation.class)
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

    public static void main(String[] args) {

    }
}
