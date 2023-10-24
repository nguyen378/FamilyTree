/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package donutnv.familytree.DataBase;

import java.util.Date;

/**
 *
 * @author ACER
 */
public class Information {
    int id;
    String name;
    String sex;
    String phoneNumber;
    Date dateOfBirth;
    Date dateOfDeath;
    String place;
    String edu;
    String major;
    String notes;

    public Information(int id, String name, String sex, String phoneNumber, Date dateOfBirth, Date dateOfDeath, String place, String edu, String major, String notes) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeath = dateOfDeath;
        this.place = place;
        this.edu = edu;
        this.major = major;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public Date getDateOfDeath() {
        return dateOfDeath;
    }

    public String getPlace() {
        return place;
    }

    public String getEdu() {
        return edu;
    }

    public String getMajor() {
        return major;
    }

    public String getNotes() {
        return notes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setDateOfDeath(Date dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setEdu(String edu) {
        this.edu = edu;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    
}
