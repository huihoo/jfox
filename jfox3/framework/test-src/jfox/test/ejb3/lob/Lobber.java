package jfox.test.ejb3.lob;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
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