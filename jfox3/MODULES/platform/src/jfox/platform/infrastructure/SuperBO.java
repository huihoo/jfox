package cn.iservicedesk.infrastructure;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */

public abstract class SuperBO implements BusinessObject{

    @Resource
    private SessionContext sessionContext;

    protected SessionContext getSessionContext(){
        return sessionContext;
    }

    /**
     * ����EntityObject�Ƿ�����
     *
     * @param entityObject entity
     */
    protected boolean isEntityReferenced(RefInspectableEntityObject entityObject){
        return entityObject.isReferenced();
    }

    /**
     * ��� Version �Ƿ񱻸���
     * ʹ�õ� namedQuery ����Ϊͨ�� ID ��� Entity �� query
     * ��ʹ�������Ա��⵱ǰ�����޷���֪��ݸ��µ�����
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isVersionValid(VersionableEntityObject entity, String namedQuery) {
        VersionableEntityObject storedEntity = (VersionableEntityObject)getDataAccessObject().getEntityObject(namedQuery,"ID",entity.getId());
        return storedEntity.getVersion() < entity.getVersion();
    }

    public abstract DataAccessObject getDataAccessObject();
}
