package com.hexanovate.familytree.login.model;
/**
 * Created by lalit on 9/12/2016.
 */
public class User {

    private int id;
    private String forename;
    private String surname;
    private int gender_id;
    private int dateOfBirth_dayOfMonth;
    private int dateOfBirth_month;
    private int dateOfBirth_year;
    private String placeOfBirth;
    private String email;
    private String password;
    private int isAdmin;





    public User(int id,String forename,String surname,int gender_id,int dateOfBirth_dayOfMonth,
                int dateOfBirth_month, int dateOfBirth_year, String placeOfBirth, String email, String password, int isAdmin)
    {
        this.id=id;
        this.forename=forename;
        this.surname=surname;
        this.gender_id=gender_id;
        this.dateOfBirth_dayOfMonth=dateOfBirth_dayOfMonth;
        this.dateOfBirth_month=dateOfBirth_month;
        this.dateOfBirth_year=dateOfBirth_year;
        this.placeOfBirth=placeOfBirth;
        this.email=email;
        this.password=password;
        this.isAdmin=isAdmin;
    }

    public User()
    {
        this.id=0;
        this.forename="";
        this.surname="";
        this.gender_id=0;
        this.dateOfBirth_dayOfMonth=0;
        this.dateOfBirth_month=0;
        this.dateOfBirth_year=0;
        this.placeOfBirth="";
        this.email="";
        this.password="";
        this.isAdmin=0;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getGender_id() {
        return gender_id;
    }

    public void setGender_id(int gender_id) {
        this.gender_id=gender_id;
    }

    public int getDateOfBirth_dayOfMonth() {
        return dateOfBirth_dayOfMonth;
    }

    public void setDateOfBirth_dayOfMonth(int dateOfBirth_dayOfMonth) {
        this.dateOfBirth_dayOfMonth=dateOfBirth_dayOfMonth;
    }

    public int getDateOfBirth_month() {
        return dateOfBirth_month;
    }

    public void setDateOfBirth_month(int dateOfBirth_month) {
        this.dateOfBirth_month=dateOfBirth_month;
    }

    public int getDateOfBirth_year() {
        return dateOfBirth_year;
    }

    public void setDateOfBirth_year(int dateOfBirth_year) {
        this.dateOfBirth_year=dateOfBirth_year;
    }

    public String getPlaceOfBirth(){
        return placeOfBirth;
    }
    public void setPlaceOfBirth(String placeOfBirth){
        this.placeOfBirth=placeOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIsAdmin(){
        return isAdmin;
    }
    public void setIsAdmin(int isAdmin){
        this.isAdmin=isAdmin;
    }
}
