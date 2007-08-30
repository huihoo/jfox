package jfox.platform.infrastructure;

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
     * 检查该EntityObject是否被引用
     *
     * @param entityObject entity
     */
    protected boolean isEntityReferenced(RefInspectableEntityObject entityObject){
        return entityObject.isReferenced();
    }

    /**
     * 检查 Version 是否被更新
     * 使用的 namedQuery 必须为通过 ID 获得 Entity 的 query
     * 不使用事务，以避免当前事务无法感知数据更新的问题
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isVersionValid(VersionableEntityObject entity, String namedQuery) {
        VersionableEntityObject storedEntity = (VersionableEntityObject)getDataAccessObject().getEntityObject(namedQuery,"ID",entity.getId());
        return storedEntity.getVersion() < entity.getVersion();
    }

    public abstract DataAccessObject getDataAccessObject();
}
