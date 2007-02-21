package net.sourceforge.jfox.mvc;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
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
