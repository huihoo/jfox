/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.petstore.action;

import org.jfox.ejb3.security.JAASLoginRequestCallback;
import org.jfox.ejb3.security.JAASLoginResponseCallback;
import org.jfox.ejb3.security.JAASLoginService;
import org.jfox.entity.mapping.EntityFactory;
import org.jfox.framework.annotation.Inject;
import org.jfox.mvc.ActionContext;
import org.jfox.mvc.ActionSupport;
import org.jfox.mvc.Invocation;
import org.jfox.mvc.PageContext;
import org.jfox.mvc.SessionContext;
import org.jfox.mvc.annotation.Action;
import org.jfox.mvc.annotation.ActionMethod;
import org.jfox.mvc.validate.StringValidation;
import org.jfox.mvc.validate.ValidateException;
import org.jfox.petstore.bo.AccountBO;
import org.jfox.petstore.bo.CategoryBO;
import org.jfox.petstore.entity.Account;
import org.jfox.petstore.entity.Category;

import javax.ejb.EJB;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Action(name = "account")
public class AccountAction extends ActionSupport implements CallbackHandler {

    @Inject
    JAASLoginService loginService;

    @EJB
    AccountBO accountBO;

    @EJB
    CategoryBO categoryBO;

    public static final String ACCOUNT_SESSION_KEY = "__ACCOUNT__";

    private static List<String> languages = new ArrayList<String>();

    static {
        languages.add("English");
//		languages.add("French");
        languages.add("Chinese");
    }


    @ActionMethod(name="newaccount",successView = "NewAccountForm.vhtml", httpMethod = ActionMethod.HttpMethod.GET)
    public void doGetNewAccount(ActionContext actionContext) throws Exception {
        // do nothing
        PageContext pageContext = actionContext.getPageContext();
        pageContext.setAttribute("languages", languages);

        List<Category> categories = categoryBO.getCategoryList();
        pageContext.setAttribute("categories", categories);
    }

    @ActionMethod(name="create", successView = "index.vhtml", errorView = "NewAccountForm.vhtml", invocationClass = NewAccountInvocation.class, httpMethod = ActionMethod.HttpMethod.POST)
    public void doPostCreate(ActionContext actionContext) throws Exception{
        NewAccountInvocation invocation = (NewAccountInvocation)actionContext.getInvocation();
        Account newAccount = EntityFactory.newEntityObject(Account.class);
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


    @ActionMethod(name="signon", successView = "signon.vhtml", httpMethod = ActionMethod.HttpMethod.GET)
    public void doGetSignon(ActionContext actionContext) throws Exception {
        // don't need do anything, just forward to successView
    }

    @ActionMethod(name="signon", successView = "index.vhtml", errorView = "signon.vhtml", invocationClass = SignonInvocation.class, httpMethod = ActionMethod.HttpMethod.POST)
    public void doPostSignon(ActionContext actionContext) throws Exception {
        SignonInvocation invocation = (SignonInvocation)actionContext.getInvocation();

        Account account = (Account)loginService.login(actionContext.getSessionContext(), this, invocation.getUsername(),invocation.getPassword());
        if (account == null) {
            String msg = "Invalid username or password. Signon failed";
            PageContext pageContext = actionContext.getPageContext();
            pageContext.setAttribute("errorMessage", msg);
            throw new Exception(msg);
        }
        else {
            SessionContext sessionContext = actionContext.getSessionContext();
            sessionContext.setAttribute(ACCOUNT_SESSION_KEY, account);
        }
    }

    /**
     * JAAS CallbackHandler method
     */
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

    @ActionMethod(name="signoff", successView = "index.vhtml", httpMethod = ActionMethod.HttpMethod.GET)
    public void doGetSignoff(ActionContext actionContext) throws Exception {
        SessionContext sessionContext = actionContext.getSessionContext();
        sessionContext.clearAttributes();
        actionContext.destroySessionContext();
    }

    @ActionMethod(name="editaccount", successView = "EditAccount.vhtml", errorView = "signon.vhtml", httpMethod = ActionMethod.HttpMethod.GET)
    public void doGetEditAccount(ActionContext actionContext) throws Exception {
        SessionContext sessionContext = actionContext.getSessionContext();
        Account account = (Account)sessionContext.getAttribute(ACCOUNT_SESSION_KEY);

        if(account == null) {
            throw new IllegalArgumentException("Not login, please login first!");
        }

        PageContext pageContext = actionContext.getPageContext();
        pageContext.setAttribute("account", account);
        pageContext.setAttribute("languages", languages);

        List<Category> categories = categoryBO.getCategoryList();
        pageContext.setAttribute("categories", categories);
    }

    @ActionMethod(name="edit", successView = "index.vhtml", errorView = "EditAccount.vhtml", invocationClass = EditAccountInvocation.class, httpMethod = ActionMethod.HttpMethod.POST)
    public void doPostEdit(ActionContext actionContext) throws Exception {
        EditAccountInvocation invocation = (EditAccountInvocation)actionContext.getInvocation();

        SessionContext sessionContext = actionContext.getSessionContext();
        Account account = (Account)sessionContext.getAttribute(ACCOUNT_SESSION_KEY);

        Account newAccount = new Account();
        newAccount.setBannerName(account.getBannerName());
        newAccount.setUsername(account.getUsername());
        newAccount.setStatus(account.getStatus());

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
     * @param actionContext invocationContext
     */
    public void doActionFailed(ActionContext actionContext) {
        if (actionContext.getActionMethod().getName().equals("doPostEdit")) {
            SessionContext sessionContext = actionContext.getSessionContext();
            Account account = (Account)sessionContext.getAttribute(ACCOUNT_SESSION_KEY);

            PageContext pageContext = actionContext.getPageContext();
            pageContext.setAttribute("account", account);
            pageContext.setAttribute("languages", languages);

            List<Category> categories = categoryBO.getCategoryList();
            pageContext.setAttribute("categories", categories);
        }
        else if(actionContext.getActionMethod().getName().equals("doPostCreate")){
            NewAccountInvocation invocation = (NewAccountInvocation)actionContext.getInvocation();
            Account newAccount = EntityFactory.newEntityObject(Account.class);
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
            PageContext pageContext = actionContext.getPageContext();
            pageContext.setAttribute("account", newAccount);
            try {
                doGetNewAccount(actionContext);
            }
            catch(Exception e) {
                logger.error("doActionFailed error.", e);
            }
        }
    }

    public static class SignonInvocation extends Invocation {
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
