/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Max
 */
public class Location implements Serializable, Comparable<Location> {

    private double longitude;
    private double latitude;
    private String road;
    private Date date;
    private String city;
    
    public Location(double latitude, double longitude) {
        this.date = new Date();
        this.longitude = longitude;
        this.latitude = latitude;
    }
    
    public Location(double latitude, double longitude, Date date) {
        this.date = date;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public Date getDate() {
        return date;
    }
    
    @Override
    public String toString() {
        return "(" + this.latitude + ", " + this.longitude + ") on road: " + this.road;
    }
    
    @Override
  public int compareTo(Location o) {
    return getDate().compareTo(o.getDate());
  }
}
