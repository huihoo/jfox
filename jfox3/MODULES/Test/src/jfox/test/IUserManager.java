package jfox.test;

import org.jfox.framework.component.Component;
import org.jfox.framework.annotation.Exported;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Exported
public interface IUserManager extends Component {

    public void addUser(IUser user);

    public void removeUser(IUser user);

    public String listUsers();

}
