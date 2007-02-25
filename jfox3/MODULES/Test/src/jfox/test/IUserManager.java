package jfox.test;

import net.sourceforge.jfox.framework.component.Component;
import net.sourceforge.jfox.framework.annotation.Exported;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Exported
public interface IUserManager extends Component {

    public void addUser(IUser user);

    public void removeUser(IUser user);

    public String listUsers();

}
