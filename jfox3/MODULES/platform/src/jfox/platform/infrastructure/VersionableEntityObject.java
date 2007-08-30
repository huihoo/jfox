package cn.iservicedesk.infrastructure;

import javax.persistence.Column;

/**
 * ֧�ְ汾��¼���ֹ���� EntityObject
 *
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public abstract class VersionableEntityObject extends EntityObject{

    @Column(name="VERSION")
    private int version;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void increaseVersion(){
        this.version++;
    }

}