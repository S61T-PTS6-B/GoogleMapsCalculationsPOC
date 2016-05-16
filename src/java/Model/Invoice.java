/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.List;

/**
 *
 * @author Max
 */
public class Invoice {

    private double totalAmount;
    private List<Location> locations;
    private double totalDistance;
    private List<Cordon> cordonOccurrences;
 
    public double getTotalAmount() {
        return totalAmount;
    }

    public void addToTotalAmount(double kilometers, double rate) {
        this.totalAmount += kilometers * rate;
        System.out.println("The rate was " + rate + "| " + kilometers + " kilometers * " + rate + " euro's = " + (kilometers * rate) + " euro's, which brings the total to " + this.totalAmount);
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void addToTotalDistance(double distance) {
        this.totalDistance += distance;
    }

    public List<Cordon> getCordonOccurrences() {
        return cordonOccurrences;
    }

    public void setCordonOccurrences(List<Cordon> cordonOccurrences) {
        this.cordonOccurrences = cordonOccurrences;
    }
}