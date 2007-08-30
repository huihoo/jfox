package cn.iservicedesk.infrastructure;

import java.io.ByteArrayInputStream;
import java.util.Locale;
import java.util.Properties;
import javax.persistence.Column;

/**
 * ֧�ֶ����Ե� EntityObject���磺Function, Module
 *
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public abstract class LocalNamingEntityObject extends EntityObject{

    //localName ������ properties �ַ�
    @Column(name="LOCAL_NAME")
    private String localName = "";
    private Properties localNameProperties = null;

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getLocalName(Locale locale) {
        return getLocalName(locale.getDisplayName());
    }

    public String getLocalName(String localeName) {
        Properties localNameProperties = getLocalNameProperties();
        if (localNameProperties.contains(localeName)) {
            return localNameProperties.getProperty(localeName);
        }
        else {
            return getName();
        }
    }

    private Properties getLocalNameProperties() {
        if (localNameProperties == null) {
            localNameProperties = new Properties();
            try {
                localNameProperties.load(new ByteArrayInputStream(localName.getBytes()));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return localNameProperties;
    }

}
