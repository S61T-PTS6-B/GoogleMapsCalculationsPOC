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
public class Invoice {

    public Invoice() {
        this.seriesOfLocationsOnRoad = new ArrayList<>();
        this.cordonOccurrences = new ArrayList<>();
    }

    private double totalAmount;
    List<SeriesOfLocationsOnRoad> seriesOfLocationsOnRoad;
    private double totalDistance;
    private List<Cordon> cordonOccurrences;
 
    public double getTotalAmount() {
        return totalAmount;
    }

    public void addToTotalAmount(double kilometers, double rate) {
        this.totalAmount += kilometers * rate;
        System.out.println("The rate was " + rate + "| " + kilometers + " kilometers * " + rate + " euro's = " + (kilometers * rate) + " euro's, which brings the total to " + this.totalAmount);
    }

    public List<SeriesOfLocationsOnRoad> getSeriesOfLocationsOnRoad() {
        return seriesOfLocationsOnRoad;
    }

    public void setSeriesOfLocationsOnRoad(List<SeriesOfLocationsOnRoad> seriesOfLocationsOnRoad) {
        this.seriesOfLocationsOnRoad = seriesOfLocationsOnRoad;
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
    
    public void addCordonOccurrence(Cordon cordon) {
        this.totalAmount += cordon.getAmount();
        this.cordonOccurrences.add(cordon);
    }
}