/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package code.google.webactioncontainer;

import java.io.File;

/**
 * 用来装上传的文件
 * 
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class FileUploaded {

    private String fieldname;
    private String filename;
    private byte[] content;

    public FileUploaded(String fieldname, String filename, byte[] content) {
        this.fieldname = fieldname;
        this.filename = filename;
        this.content = content;
    }

    public String getFieldname() {
        return fieldname;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getContent() {
        return content;
    }

    public String toString() {
        return "Uploaded file, name=" + getFilename();
    }

    /**
     * TODO:
     * @param file
     */
    public void saveTO(File file){

    }

    public static void main(String[] args) {

    }
}
