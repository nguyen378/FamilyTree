/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package donutnv.familytree.DataBase;

/**
 *
 * @author Thuong Nguyen
 */
public class Person {
    private String personId;
    private String locationId;
    private String livingStatus;

    public Person(String personId, String locationId, String livingStatus) {
        this.personId = personId;
        this.locationId = locationId;
        this.livingStatus = livingStatus;
    }

    public String getPersonId() {
        return personId;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getLivingStatus() {
        return livingStatus;
    }


}
