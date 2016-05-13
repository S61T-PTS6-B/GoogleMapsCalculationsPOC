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
public class SeriesOfLocationsOnRoad {

    private Road road;
    private List<Location> locations;

    public Road getRoad() {
        return road;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public SeriesOfLocationsOnRoad(Road road) {
        this.road = road;
        this.locations = new ArrayList<>();
    }
}
