package net.sourceforge.jfox.petstore.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Column;

import net.sourceforge.jfox.entity.annotation.EntityHelper;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Entity
@EntityHelper(Account.AccountHelper.class)
public interface Account extends Serializable {

    @Column(name="userid") // signon.username
    String getUsername();
    void setUsername(String username);

    @Column(name = "password") // signon.password
    String getPassword();
    void setPassword(String password);

    @Column(name="email")
    String getEmail();
    void setEmail(String email);

    @Column(name="firstname")
    String getFirstName();
    void setFirstName(String firstName);

    @Column(name="lastname")
    String getLastName();
    void setLastName(String lastName);

    @Column(name="status")
    String getStatus();
    void setStatus(String status);

    @Column(name="addr1")
    String getAddress1();
    void setAddress1(String address1);

    @Column(name="addr2")
    String getAddress2();
    void setAddress2(String address2);

    @Column(name="city")
    String getCity();
    void setCity(String city);

    @Column(name="state")
    String getState();
    void setState(String state);

    @Column(name="zip")
    String getZip();
    void setZip(String zip);

    @Column(name="country")
    String getCountry();
    void setCountry(String country);

    @Column(name="phone")
    String getPhone();
    void setPhone(String phone);

    @Column(name="favcategory")
    String getFavouriteCategoryId();
    void setFavouriteCategoryId(String favouriteCategoryId);

    @Column(name="langpref")
    String getLanguagePreference();
    void setLanguagePreference(String languagePreference);

    @Column(name="mylistopt")
    int getListOption();
    void setListOption(int listOption);

    @Column(name="banneropt")
    int getBannerOption();
    void setBannerOption(int bannerOption);

    @Column(name="bannername")    
    String getBannerName();
    void setBannerName(String bannerName);

    public static class AccountHelper {
        public boolean isListOption(Account account) {
            return account.getListOption() == 1 ? true : false;
        }

        public boolean isBannerOption(Account account) {
            return account.getBannerOption() == 1 ? true : false;
        }
    }

}
