package org.jfox.ejb3.dependent;

import java.lang.reflect.Field;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.MessageDrivenContext;
import javax.ejb.SessionContext;
import javax.ejb.TimerService;
import javax.jms.QueueConnectionFactory;
import javax.jms.TopicConnectionFactory;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import org.jfox.ejb3.AbstractEJBContext;
import org.jfox.ejb3.EJBBucket;
import org.jfox.entity.EntityManagerFactoryBuilderImpl;
import org.jfox.framework.dependent.InjectionException;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
public class FieldResourceDependence extends ResourceDependence {

    private Field field;

    public FieldResourceDependence(EJBBucket bucket, Field field, Resource resource) {
        super(bucket, resource);
        this.field = field;
    }

    /**
     * \@Resource 可以 注入以下类型的数据
     * <p/>
     * \@Resource(mappedName="DefaultDS") private javax.sql.DataSource ds;
     * \@Resource javax.ejb.SessionContext ctx;
     * \@Resource javax.ejb.TimerService timer;
     * \@Resource javax.ejb.UserTransaction ut;
     *
     * @param ejbContext ejb bean ejbContext
     * @throws InjectionException
     */
    public void inject(Object ejbContext) throws InjectionException {
        Object targetObject = null;
        if (field.getType().equals(EJBContext.class) ||
                field.getType().equals(SessionContext.class) ||
                field.getType().equals(MessageDrivenContext.class)) {
            targetObject = (AbstractEJBContext)ejbContext;
        }
        else if (field.getType().equals(TimerService.class)) {
            targetObject = ((AbstractEJBContext)ejbContext).getTimerService();
        }
        else if (field.getType().equals(DataSource.class)) {
            // 无需通过 InitialContext
//            targetObject = getBucket().getEJBContainer().lookup()
            //注入 DataSource
            try {
                if (getResource().name().trim().length() == 0 && getResource().mappedName().trim().length() == 0) {
                    targetObject = EntityManagerFactoryBuilderImpl.getDefaultDataSource();
                }
                else if (getResource().name().trim().length() != 0) {
                    targetObject = EntityManagerFactoryBuilderImpl.getDataSourceByUnitName(getResource().name().trim());
                }
                else {
                    targetObject = EntityManagerFactoryBuilderImpl.getDataSourceByMappedName(getResource().name().trim());
                }
            }
            catch (Exception e) {
                throw new InjectionException("Inject @Resource with DataSource field failed, " + field, e);
            }
        }
        else if (field.getType().equals(UserTransaction.class)) {
            //注入 UserTransaction
            targetObject = getBucket().getEJBContainer().getTransactionManager();
        }
        else if (field.getType().equals(QueueConnectionFactory.class) || field.getType().equals(TopicConnectionFactory.class)) {
            //注入 JMS Connection Factory
            targetObject = getBucket().getEJBContainer().getMessageService();
        }
        else {
            throw new InjectionException("Not support inject type: " + field);
        }
        try {
            field.setAccessible(true);
            field.set(((AbstractEJBContext)ejbContext).getEJBInstance(), targetObject);
        }
        catch (IllegalAccessException e) {
            throw new InjectionException("Inject EJBContext ");
        }
    }

    public static void main(String[] args) {

    }
}
