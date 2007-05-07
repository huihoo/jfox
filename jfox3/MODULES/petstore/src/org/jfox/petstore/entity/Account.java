/*
 * JFox - The most lightweight Java EE Application Server!
 * more details please visit http://www.huihoo.org/jfox or http://www.jfox.org.cn.
 *
 * JFox is licenced and re-distributable under GNU LGPL.
 */
package org.jfox.petstore.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author <a href="mailto:jfox.young@gmail.com">Young Yang</a>
 */
@Entity
public class Account implements Serializable {

    @Column(name="userid") // signon.username
    String username;

    @Column(name = "password") // signon.password
    String password;

    @Column(name="email")
    String email;

    @Column(name="firstname")
    String firstName;

    @Column(name="lastname")
    String lastName;

    @Column(name="status")
    String status;

    @Column(name="addr1")
    String address1;

    @Column(name="addr2")
    String address2;

    @Column(name="city")
    String city;

    @Column(name="state")
    String state;

    @Column(name="zip")
    String zip;

    @Column(name="country")
    String country;

    @Column(name="phone")
    String phone;

    @Column(name="favcategory")
    String favouriteCategoryId;

    @Column(name="langpref")
    String languagePreference;

    @Column(name="mylistopt")
    int listOption;

    @Column(name="banneropt")
    int bannerOption;

    @Column(name="bannername")    
    String bannerName;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFavouriteCategoryId() {
        return favouriteCategoryId;
    }

    public void setFavouriteCategoryId(String favouriteCategoryId) {
        this.favouriteCategoryId = favouriteCategoryId;
    }

    public String getLanguagePreference() {
        return languagePreference;
    }

    public void setLanguagePreference(String languagePreference) {
        this.languagePreference = languagePreference;
    }

    public int getListOption() {
        return listOption;
    }

    public void setListOption(int listOption) {
        this.listOption = listOption;
    }

    public int getBannerOption() {
        return bannerOption;
    }

    public void setBannerOption(int bannerOption) {
        this.bannerOption = bannerOption;
    }

    public String getBannerName() {
        return bannerName;
    }

    public void setBannerName(String bannerName) {
        this.bannerName = bannerName;
    }

    public static class AccountHelper {
        public static boolean isListOption(Account account) {
            return account.getListOption() == 1 ? true : false;
        }

        public static boolean isBannerOption(Account account) {
            return account.getBannerOption() == 1 ? true : false;
        }
    }

}
