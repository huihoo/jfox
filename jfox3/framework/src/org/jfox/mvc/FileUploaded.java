/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.mvc;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class FileUploaded {

    private String filename;
    private byte[] content;

    public FileUploaded(String filename, byte[] content) {
        this.filename = filename;
        this.content = content;
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

    public static void main(String[] args) {

    }
}
