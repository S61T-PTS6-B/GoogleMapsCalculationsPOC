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
public class Location implements Serializable {

    private double longitude;
    private double latitude;
    private String road;
    private Date date;
    
    public Location(double latitude, double longitude) {
        this.date = new Date();
        this.longitude = longitude;
        this.latitude = latitude;
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
}
