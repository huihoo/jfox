package cn.iservicedesk.infrastructure;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.persistence.Column;

/**
 * 可以检查引用的 Entity
 * 采用 Property 格式，如：{ACCOUNT=1,2,3,4 USER=12,34,56}
 *
 * @author <a href="mailto:yang_y@sysnet.com.cn">Young Yang</a>
 */
public abstract class LocalNamingAndRefInspectableEntityObject extends EntityObject {
    // 是否已经初始化 refs
    private boolean refsInit = false;
    private boolean refsChanged = false;

    @Column(name = "REFS")
    private String refs;

    private Map<String, List<Long>> refsMap = new HashMap<String, List<Long>>();

    public void setRefs(String refs) {
        this.refs = refs;
        refsInit = false;
        initReference();
    }

    public synchronized String getRefs() {
        if(refsChanged) {
            StringBuffer sb = new StringBuffer();
            for(Map.Entry<String, List<Long>> entry : refsMap.entrySet()){
                String tableName = entry.getKey();
                sb.append(tableName).append("=");
                List<Long> refIds = entry.getValue();
                for(int i=0; i< refIds.size(); i++){
                    if(i>0) {
                        sb.append(",");
                    }
                    sb.append(refIds.get(i));
                }
                sb.append("\n");
            }
            refs = sb.toString();
            refsChanged = false;
        }
        return refs;
    }

    public boolean isReferenced() {
        initReference();
        return !refsMap.isEmpty();
    }

    public void addReference(String tableName, long entityId) {
        initReference();
        List<Long> refIds = refsMap.get(tableName);
        if (refIds == null) {
            refIds = new ArrayList<Long>(1);
            refIds.add(entityId);
        }
        refsChanged = true;
    }

    public void removeReference(String tableName, long entityId) {
        initReference();
        List<Long> refIds = refsMap.get(tableName);
        if (refIds != null) {
            refIds.remove(entityId);
            refsChanged = true;
        }
    }

    private synchronized void initReference() {
        if (!refsInit) {
            if (refs != null && refs.trim().length() > 0) {
                Properties refProperties = new Properties();
                try {
                    refProperties.load(new ByteArrayInputStream(refs.getBytes()));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                for (Map.Entry<Object, Object> entry : refProperties.entrySet()) {
                    String tabName = entry.getKey().toString();
                    String value = entry.getValue().toString();
                    String[] refIds = value.split(",");
                    List<Long> refIdList = new ArrayList<Long>(refIds.length);
                    for (String refId : refIds) {
                        refIdList.add(Long.parseLong(refId));
                    }
                    refsMap.put(tabName, refIdList);
                }
            }
            refsInit = true;
        }
    }

    public Long[] getReferenceIds(String key) {
        initReference();
        List<Long> refIds = refsMap.get(key);
        return refIds.toArray(new Long[refIds.size()]);
    }

    //-- Local Naming
        //localName 多语言 properties 字符串
    @Column(name="LOCAL_NAME")
    private String localName = "";
    private Properties localNameProperties = null;

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getLocalName(){
        return localName;
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

    public static void main(String[] args) {

    }
}