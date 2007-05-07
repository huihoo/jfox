/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package jfox.test.ejb3.lob;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Entity
public class Lobber {

    @Column(name="id")
    long id;

    @Column(name="blobby")
    byte[] blobby;

    @Column(name="clobby")
    String clobby;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClobby() {
        return clobby;
    }

    public void setClobby(String clobby) {
        this.clobby = clobby;
    }

    public byte[] getBlobby() {
        return blobby;
    }

    public void setBlobby(byte[] blobby) {
        this.blobby = blobby;
    }
}