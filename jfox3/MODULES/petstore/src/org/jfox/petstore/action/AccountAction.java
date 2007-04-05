package org.jfox.petstore.action;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import javax.ejb.EJB;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.jfox.framework.annotation.Service;
import org.jfox.framework.annotation.Inject;
import org.jfox.mvc.ActionSupport;
import org.jfox.mvc.InvocationContext;
import org.jfox.mvc.Invocation;
import org.jfox.mvc.SessionContext;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.validate.ValidateException;
import org.jfox.mvc.validate.StringValidation;
import org.jfox.mvc.annotation.ActionMethod;
import org.jfox.entity.EntityObject;
import org.jfox.entity.dao.MapperEntity;
import org.jfox.petstore.bo.AccountBO;
import org.jfox.petstore.bo.CategoryBO;
import org.jfox.petstore.entity.Account;
import org.jfox.petstore.entity.Category;
import org.jfox.ejb3.security.JAASLoginRequestCallback;
import org.jfox.ejb3.security.JAASLoginResponseCallback;
import org.jfox.ejb3.security.JAASLoginService;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Service(id = "account")
public class AccountAction extends ActionSupport implements CallbackHandler {
    public static final String ACCOUNT_SESSION_KEY = "__ACCOUNT__";

    private static List<String> languages = new ArrayList<String>();

    static {
        languages.add("English");
//		languages.add("French");
        languages.add("Chinese");
    }

    @Inject
    JAASLoginService loginService;

    @EJB
    AccountBO accountBO;

    @EJB
    CategoryBO categoryBO;

    @ActionMethod(successView = "NewAccountForm.vhtml")
    public void doGetNewAccount(InvocationContext invocationContext) throws Exception {
        // do nothing
        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("languages", languages);

        List<Category> categories = categoryBO.getCategoryList();
        pageContext.setAttribute("categories", categories);
    }

    @ActionMethod(successView = "index.vhtml", errorView = "NewAccountForm.vhtml", invocationClass = NewAccountInvocation.class)
    public void doPostCreate(InvocationContext invocationContext) throws Exception{
        NewAccountInvocation invocation = (NewAccountInvocation)invocationContext.getInvocation();
        Account newAccount = MapperEntity.newEntityObject(Account.class);
        newAccount.setUsername(invocation.getUsername());
        newAccount.setStatus("OK");
        newAccount.setPassword(invocation.getPassword());
        newAccount.setAddress1(invocation.getAddress1());
        newAccount.setAddress2(invocation.getAddress2());
        newAccount.setBannerOption(invocation.getBannerOption());
        newAccount.setCity(invocation.getCity());
        newAccount.setCountry(invocation.getCountry());
        newAccount.setEmail(invocation.getEmail());
        newAccount.setFavouriteCategoryId(invocation.getFavouriteCategoryId());
        newAccount.setFirstName(invocation.getFirstName());
        newAccount.setLanguagePreference(invocation.getLanguagePreference());
        newAccount.setLastName(invocation.getLastName());
        newAccount.setListOption(invocation.getListOption());
        newAccount.setPassword(invocation.getPassword());
        newAccount.setPhone(invocation.getPhone());
        newAccount.setState(invocation.getState());
        newAccount.setZip(invocation.getZip());

        try {
            accountBO.insertAccount(newAccount);
        }
        catch (Exception e) {
            // update failed
            throw e;
        }
    }


    @ActionMethod(successView = "signon.vhtml")
    public void doGetSignon(InvocationContext invocationContext) throws Exception {
        // do nothing
    }

    //use JAAS LoginService
    @ActionMethod(successView = "index.vhtml", errorView = "signon.vhtml", invocationClass = SignonInvocation.class)
    public void doPostSignon(InvocationContext invocationContext) throws Exception {
        SignonInvocation invocation = (SignonInvocation)invocationContext.getInvocation();

        Account account = (Account)loginService.login(invocationContext.getSessionContext(), this, invocation.getUsername(),invocation.getPassword());
        if (account == null) {
            String msg = "Invalid username or password. Signon failed";
            PageContext pageContext = invocationContext.getPageContext();
            pageContext.setAttribute("errorMessage", msg);
            throw new Exception(msg);
        }
        else {
            SessionContext sessionContext = invocationContext.getSessionContext();
            sessionContext.setAttribute(ACCOUNT_SESSION_KEY, account);
        }
    }

    //JAAS CallbackHandler method
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        JAASLoginRequestCallback requestCallback = (JAASLoginRequestCallback)callbacks[0];
        JAASLoginResponseCallback responseCallback = (JAASLoginResponseCallback)callbacks[1];

        // first parameter is username
        String username = requestCallback.getParams().get(0);
        // second parameter is password
        String password = requestCallback.getParams().get(1);
        Account account = accountBO.getAccount(username, password);
        // set callback object, will return by LoginService.login
        responseCallback.setCallbackObject(account);
        // set principal name
        responseCallback.setPrincipalName(username);
        // set role
        responseCallback.setRole(username);
    }

    @ActionMethod(successView = "index.vhtml")
    public void doGetSignoff(InvocationContext invocationContext) throws Exception {
        SessionContext sessionContext = invocationContext.getSessionContext();
        sessionContext.destroy();
    }

    @ActionMethod(successView = "EditAccount.vhtml", errorView = "signon.vhtml")
    public void doGetEditAccount(InvocationContext invocationContext) throws Exception {
        SessionContext sessionContext = invocationContext.getSessionContext();
        Account account = (Account)sessionContext.getAttribute(ACCOUNT_SESSION_KEY);

        if(account == null) {
            throw new IllegalArgumentException("Not login, please login first!");
        }

        PageContext pageContext = invocationContext.getPageContext();
        pageContext.setAttribute("account", account);
        pageContext.setAttribute("languages", languages);

        List<Category> categories = categoryBO.getCategoryList();
        pageContext.setAttribute("categories", categories);
    }

    @ActionMethod(successView = "index.vhtml", errorView = "EditAccount.vhtml", invocationClass = EditAccountInvocation.class)
    public void doPostEdit(InvocationContext invocationContext) throws Exception {
        EditAccountInvocation invocation = (EditAccountInvocation)invocationContext.getInvocation();

        SessionContext sessionContext = invocationContext.getSessionContext();
        Account account = (Account)sessionContext.getAttribute(ACCOUNT_SESSION_KEY);

        Account newAccount = (Account)((EntityObject)account).clone();
        newAccount.setPassword(invocation.getPassword());
        newAccount.setAddress1(invocation.getAddress1());
        newAccount.setAddress2(invocation.getAddress2());
        newAccount.setBannerOption(invocation.getBannerOption());
        newAccount.setCity(invocation.getCity());
        newAccount.setCountry(invocation.getCountry());
        newAccount.setEmail(invocation.getEmail());
        newAccount.setFavouriteCategoryId(invocation.getFavouriteCategoryId());
        newAccount.setFirstName(invocation.getFirstName());
        newAccount.setLanguagePreference(invocation.getLanguagePreference());
        newAccount.setLastName(invocation.getLastName());
        newAccount.setListOption(invocation.getListOption());
        newAccount.setPassword(invocation.getPassword());
        newAccount.setPhone(invocation.getPhone());
        newAccount.setState(invocation.getState());
        newAccount.setZip(invocation.getZip());

        try {
            accountBO.updateAccount(newAccount);
            sessionContext.setAttribute(ACCOUNT_SESSION_KEY, newAccount);
        }
        catch (Exception e) {
            // update failed
            throw e;
        }
    }

    /**
     * 在 doPostEdit 发生异常时，通过该方法设置 PageContext account，
     * 以便跳转到 errorView 时，可以预设数据
     *
     * @param invocationContext invocationContext
     */
    protected void doActionFailed(InvocationContext invocationContext) {
        if (invocationContext.getActionMethod().getName().equals("doPostEdit")) {
            SessionContext sessionContext = invocationContext.getSessionContext();
            Account account = (Account)sessionContext.getAttribute(ACCOUNT_SESSION_KEY);

            PageContext pageContext = invocationContext.getPageContext();
            pageContext.setAttribute("account", account);
            pageContext.setAttribute("languages", languages);

            List<Category> categories = categoryBO.getCategoryList();
            pageContext.setAttribute("categories", categories);
        }
        else if(invocationContext.getActionMethod().getName().equals("doPostCreate")){
            NewAccountInvocation invocation = (NewAccountInvocation)invocationContext.getInvocation();
            Account newAccount = MapperEntity.newEntityObject(Account.class);
            newAccount.setUsername(invocation.getUsername());
            newAccount.setStatus("OK");
            newAccount.setPassword(invocation.getPassword());
            newAccount.setAddress1(invocation.getAddress1());
            newAccount.setAddress2(invocation.getAddress2());
            newAccount.setBannerOption(invocation.getBannerOption());
            newAccount.setCity(invocation.getCity());
            newAccount.setCountry(invocation.getCountry());
            newAccount.setEmail(invocation.getEmail());
            newAccount.setFavouriteCategoryId(invocation.getFavouriteCategoryId());
            newAccount.setFirstName(invocation.getFirstName());
            newAccount.setLanguagePreference(invocation.getLanguagePreference());
            newAccount.setLastName(invocation.getLastName());
            newAccount.setListOption(invocation.getListOption());
            newAccount.setPassword(invocation.getPassword());
            newAccount.setPhone(invocation.getPhone());
            newAccount.setState(invocation.getState());
            newAccount.setZip(invocation.getZip());
            PageContext pageContext = invocationContext.getPageContext();
            pageContext.setAttribute("account", newAccount);
            try {
                doGetNewAccount(invocationContext);
            }
            catch(Exception e) {
                logger.error("doActionFailed error.", e);
            }
        }
    }

    public static class SignonInvocation extends Invocation {
        //TODO: test Validator
        @StringValidation(minLength = 4, nullable = false)
        private String username;
        
        @StringValidation(minLength = 4, nullable = false)
        private String password;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    public static class EditAccountInvocation extends Invocation {

        @StringValidation(minLength = 4, nullable = false)
        private String password;

        private String repeatpassword;

        private String email;

        private String firstName;

        private String lastName;

        private String address1;

        private String address2;

        private String city;

        private String state;

        private String zip;

        private String country;

        private String phone;

        private String favouriteCategoryId;

        private String languagePreference;

        private int listOption;

        private int bannerOption;

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
        }

        public int getBannerOption() {
            return bannerOption;
        }

        public void setBannerOption(int bannerOption) {
            this.bannerOption = bannerOption;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFavouriteCategoryId() {
            return favouriteCategoryId;
        }

        public void setFavouriteCategoryId(String favouriteCategoryId) {
            this.favouriteCategoryId = favouriteCategoryId;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLanguagePreference() {
            return languagePreference;
        }

        public void setLanguagePreference(String languagePreference) {
            this.languagePreference = languagePreference;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public int getListOption() {
            return listOption;
        }

        public void setListOption(int listOption) {
            this.listOption = listOption;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public String getRepeatpassword() {
            return repeatpassword;
        }

        public void setRepeatpassword(String repeatpassword) {
            this.repeatpassword = repeatpassword;
        }

        public void validateAll() throws ValidateException {
            //验证密码是否一致
            if (!getPassword().equals(getRepeatpassword())) {
                throw new ValidateException("password twice input are different.", "password", getPassword());
            }
        }

    }

    public static class NewAccountInvocation extends EditAccountInvocation {
        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    public static void main(String[] args) {

    }
}
