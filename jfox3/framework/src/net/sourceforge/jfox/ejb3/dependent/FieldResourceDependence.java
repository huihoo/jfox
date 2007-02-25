package net.sourceforge.jfox.ejb3.dependent;

import java.lang.reflect.Field;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.SessionContext;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TimerService;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

import net.sourceforge.jfox.ejb3.EJBBucket;
import net.sourceforge.jfox.framework.dependent.InjectionException;
import net.sourceforge.jfox.entity.EntityManagerFactoryBuilderImpl;

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
     * @param instance ejb bean instance
     * @throws InjectionException
     */
    public void inject(Object instance) throws InjectionException {
        Object targetObject = null;
        if (field.getType().equals(EJBContext.class) ||
                field.getType().equals(SessionContext.class) ||
                field.getType().equals(MessageDrivenContext.class)) {
            targetObject = getBucket().createEJBContext(instance);
        }
        else if (field.getType().equals(TimerService.class)) {
            targetObject = getBucket().createEJBContext(instance).getTimerService();
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
        else {
            throw new InjectionException("Not support inject type: " + field);
        }
        try {
            field.setAccessible(true);
            field.set(instance, targetObject);
        }
        catch (IllegalAccessException e) {
            throw new InjectionException("Inject EJBContext ");
        }
    }

    public static void main(String[] args) {

    }
}
