package cn.iservicedesk.infrastructure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jfox.entity.QueryExt;
import org.jfox.entity.dao.DAOSupport;

/**
 * //TODO: ֧�ֶ���ݿ�SQLTemplate
 *
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public abstract class SuperDAO extends DAOSupport implements DataAccessObject {
    
    /**
     * ע�� JPA EntityManager
     */
    @PersistenceContext(unitName = "iServiceDesk_DS")
    private EntityManager em = null;

    /**
     * ���� EntityManager��Ĭ��ע����� default
     */
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * ��� id �ҵ� Entity ����
     *
     * @param namedQuery  named native sql
     * @param placeHolderName sql template column place holder name
     * @param id id
     * @return entity instance
     */
    public EntityObject getEntityObject(String namedQuery, String placeHolderName, long id) {
        Map<String, Long> paramMap = new HashMap<String, Long>(1);
        paramMap.put(placeHolderName,id);
        List<? extends EntityObject> entities = processNamedNativeQuery(namedQuery,paramMap);
        if(!entities.isEmpty()) {
            return entities.get(0);
        }
        else {
            return null;
        }
    }


    public int executeNamedNativeUpdate(String namedQuery, Map<String, Object> paramMap) {
        Query query = createNamedNativeQuery(namedQuery);
        if (paramMap != null) {
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return query.executeUpdate();
    }

    /**
     * ʹ��һ��Ԥ����� query �����в�ѯ������ entity list
     *
     * @param namedQuery   named query
     */
    public List<? extends EntityObject> processNamedNativeQuery(String namedQuery, Map<String, ?> paramMap) {
        return processNamedNativeQuery(namedQuery, paramMap, 0, Integer.MAX_VALUE);
    }

    /**
     * ʹ��һ��Ԥ����� query �����в�ѯ������ entity list
     *
     * @param namedQuery   named query
     * @param paramMap parameter map
     * @param firstResult ��һ��ֵ��λ��
     * @param maxResult ȡֵ��Χ
     * @return ���ط����Ҫ�� entity list
     */
    public List<? extends EntityObject> processNamedNativeQuery(String namedQuery, Map<String, ?> paramMap, int firstResult, int maxResult) {
        Query query = createNamedNativeQuery(namedQuery);
        if (paramMap != null) {
            for (Map.Entry<String, ?> entry : paramMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResult);
        return (List<? extends EntityObject>)query.getResultList();
    }

    public QueryExt createNativeQuery(String sql) {
        throw new UnsupportedOperationException("Can not create native query, only named native query supported!");
    }

    public QueryExt createNativeQuery(String sql, Class<?> resultClass) {
        throw new UnsupportedOperationException("Can not create native query, only named native query supported!");
    }
}
