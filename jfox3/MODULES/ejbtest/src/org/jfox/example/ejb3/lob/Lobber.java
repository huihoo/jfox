package org.jfox.example.ejb3.lob;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
@Entity
public interface Lobber {

    @Column(name="id")
    public long getId();
    public void setId(long id);

    @Column(name="blobby")
    public byte[] getBlobby();
    public void setBlobby(byte[] blobby);

    @Column(name="clobby")
    public String getClobby();
    public void setClobby(String clobby);
}