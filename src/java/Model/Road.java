/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Max
 */
public class Road {

    private String roadName;
    private double rate;

    public String getRoadName() {
        return roadName;
    }

    public double getRate() {
        return rate;
    }

    public Road(String roadName, double rate) {
        this.roadName = roadName;
        this.rate = rate;
    }    
}
