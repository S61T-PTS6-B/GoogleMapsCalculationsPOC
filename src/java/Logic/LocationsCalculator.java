/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import Model.Cordon;
import Model.Invoice;
import Model.Location;
import Model.Road;
import Model.SeriesOfLocationsOnRoad;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Max
 */
public class LocationsCalculator {

    private List<Road> roads;
    private Invoice invoice;
    private List<Cordon> existingCordons;

    public LocationsCalculator() {
        invoice = new Invoice();

        this.roads = new ArrayList<>();
        this.roads.add(new Road("NO_SPECIAL_ROAD", 0.05));
        this.roads.add(new Road("TESTROAD", 0.09));
        this.roads.add(new Road("E25", 0.08));
        this.roads.add(new Road("Rijksweg Den Bosch-Eindhoven", 0.10));

        this.existingCordons = new ArrayList<>();
        this.existingCordons.add(new Cordon("Weert", 10));
        this.existingCordons.add(new Cordon("Best", 5));
        this.existingCordons.add(new Cordon("Boxtel", 8));
    }

    public Invoice getInvoice(List<Location> locations) throws ParseException {

        this.invoice = new Invoice();

        //Stap 1: Sorteren op datum
        Collections.sort(locations);
        
        //Stap 2: Naam van de locatie opvragen en in het locatie object zetten
        for (Location loc : locations) {
            calculateAdress(loc);
        }

        //Stap 3: Kijken of er een cordongebied ingereden is (disctinct of per keer dat je het binnenrijdt?)
        List<Cordon> cordons = new ArrayList<>();
        String lastCordonName = "NO_CORDON";
        
        for (Location loc : locations) {
            boolean hasCordon = false;
            for (Cordon c : existingCordons) {
                
                //Als de stad van de locatie een specifiek cordongebied is
                if (c.getPlaceName().equals(loc.getCity())) {
                    hasCordon = true;
                    
                    //Als de vorige locatie nog niet in dit cordongebied was
                    if (loc.getCity().equals(lastCordonName) == false) {
                        cordons.add(new Cordon(c.getPlaceName(), c.getAmount()));
                        lastCordonName = c.getPlaceName();
                    }
                }
            }
            if (hasCordon == false) {
                lastCordonName = "NO_CORDON";
            }
        }
        for (Cordon c : cordons) {
            invoice.addCordonOccurrence(c);
            System.out.println("Cordon area entered: " + c.toString() + ". Which brings the total amount to " + invoice.getTotalAmount() + "euros");
        }

        //Stap 4: Kijken welke locaties op een snelweg liggen
        // - Lijst van snelweg namen hebben
        // - Als de naam van de locatie een snelweg is, maak een lijst aan van die snelweg
        //      en gooi die locatie erin. verwijder de locatie uit de originele lijst
        List<SeriesOfLocationsOnRoad> seriesOfLocationsOnRoad = new ArrayList<>();
        String lastRoadName = "NO_SPECIAL_ROAD";

        for (Location loc : locations) {
            boolean hasSpecialRoad = false;

            for (Road r : roads) {

                //Als de weg van de locatie een specifieke weg is
                if (loc.getRoad().equals(r.getRoadName())) {
                    hasSpecialRoad = true;

                    //Als de wegnaam van de vorige locatie hetzelfde is als die van de huidige locatie
                    if (loc.getRoad().equals(lastRoadName)) {

                        //Get de laatste serie locaties (waar deze locatie dus bij hoort)
                        SeriesOfLocationsOnRoad currentSerie = seriesOfLocationsOnRoad.get(seriesOfLocationsOnRoad.size() - 1);
                        currentSerie.getLocations().add(loc);
                    } //Als de wegnaam van de vorige locatie anders is dan die van de huidige locatie
                    else {
                        //Start dan een nieuwe serie locaties op de weg
                        SeriesOfLocationsOnRoad newSerie = new SeriesOfLocationsOnRoad(r);
                        newSerie.getLocations().add(loc);
                        seriesOfLocationsOnRoad.add(newSerie);
                    }
                    lastRoadName = loc.getRoad();
                }
            }
            if (hasSpecialRoad == false) {
                if (lastRoadName.equals("NO_SPECIAL_ROAD")) {
                    //Get de laatste serie locaties (waar deze locatie dus bij hoort)
                    SeriesOfLocationsOnRoad currentSerie = seriesOfLocationsOnRoad.get(seriesOfLocationsOnRoad.size() - 1);
                    currentSerie.getLocations().add(loc);
                } //Als de wegnaam van de vorige locatie anders is dan NO_SPECIAL_ROAD
                else {
                    //Start dan een nieuwe serie locaties op de weg
                    SeriesOfLocationsOnRoad newSerie = new SeriesOfLocationsOnRoad(roads.get(0));
                    newSerie.getLocations().add(loc);
                    seriesOfLocationsOnRoad.add(newSerie);
                }
                lastRoadName = "NO_SPECIAL_ROAD";
            }
        }
        
        invoice.setSeriesOfLocationsOnRoad(seriesOfLocationsOnRoad);

//        //Tussenstap: Print alle locaties uit alle serie lijsten op het scherm
//        for (int i = 0; i < seriesOfLocationsOnRoad.size(); i++) {
//            SeriesOfLocationsOnRoad serie = seriesOfLocationsOnRoad.get(i);
//            for (Location l : serie.getLocations()) {
//                System.out.println("List nr: " + i + ", " + l.toString() + ", rate per kilometer: " + serie.getRoad().getRate());
//            }
//        }
        //Stap 5: Bereken van die lijst welke locaties er op een bepaald tarieftijd zijn gelogd
        // - scheid die locaties in lijsten
        // - bereken de afstand van die locaties, en het bedrag dat daarover betaald moet worden
        // - HOU REKENING MET AFSTAND TUSSEN DE LAATSTE IN DE LIJST, EN DE EERSTE IN DE VOLGENDE LIJST!
        this.processSeriesToTotalAmount(seriesOfLocationsOnRoad);

        System.out.println("");
        System.out.println("RESULTS:");
        System.out.println("The total amount of the list is " + invoice.getTotalAmount() + " euro's");
        System.out.println("The total disctance of the list is " + invoice.getTotalDistance() + " kilometers");

        //Stap 6: Return het Invoice object
        return this.invoice;
    }

    public void calculateAdress(Location loc) throws ParseException {
        double latitude = loc.getLatitude();
        double longitude = loc.getLongitude();
        Client client = ClientBuilder.newClient();
        WebTarget myResource = client.target("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=AIzaSyDSR66C1DixiI6wv_Fv3kTVUQYwMQ0VPIY");
        String response = myResource.request(MediaType.APPLICATION_JSON).get(String.class);

        JSONParser parser = new JSONParser();
        JSONObject jsonobj = (JSONObject) parser.parse(response);
        JSONArray resultslist = (JSONArray) jsonobj.get("results");
        JSONObject result = (JSONObject) resultslist.get(0);
        String resultroad = (String) result.get("formatted_address");
        resultroad = resultroad.substring(0, resultroad.indexOf(","));
        
        loc.setRoad(resultroad);
        
        try {
            result = (JSONObject) resultslist.get(resultslist.size() - 3);
            String resultcity = (String) result.get("formatted_address");
            resultcity = resultcity.substring(0, resultcity.indexOf(","));
            loc.setCity(resultcity);
        } catch (Exception e) {
            
        }
    }

    private Invoice processSeriesToTotalAmount(List<SeriesOfLocationsOnRoad> seriesOfLocationsOnRoad) {
        //Stap 5: Bereken van die lijst welke locaties er op een bepaald tarieftijd zijn gelogd
        // - scheid die locaties in lijsten
        // - bereken de afstand van die locaties, en het bedrag dat daarover betaald moet worden
        // - HOU REKENING MET AFSTAND TUSSEN DE LAATSTE IN DE LIJST, EN DE EERSTE IN DE VOLGENDE LIJST!

        Location lastLocationOfList = null;
        Double rateOfLastList = null;

        for (SeriesOfLocationsOnRoad serie : seriesOfLocationsOnRoad) {

            if (lastLocationOfList != null) {
                double kilometers = calculateDistance(lastLocationOfList, serie.getLocations().get(0));

                //Als het kilometertarief van de laatste lijst kleiner is dan die van de huidige lijst
                if (rateOfLastList != null && rateOfLastList < serie.getRoad().getRate()) {
                    invoice.addToTotalAmount(kilometers, rateOfLastList);
                } else {
                    invoice.addToTotalAmount(kilometers, serie.getRoad().getRate());
                }
            }

            for (int i = 0; i < serie.getLocations().size(); i++) {
                Location loc1 = serie.getLocations().get(i);
                Location loc2;
                try {
                    loc2 = serie.getLocations().get(i + 1);
                } catch (Exception e) {
                    loc2 = null;
                }

                if (loc2 != null) {
                    double kilometers = calculateDistance(loc1, loc2);
                    invoice.addToTotalAmount(kilometers, serie.getRoad().getRate());
                } else {
                    lastLocationOfList = loc1;
                    rateOfLastList = serie.getRoad().getRate();
                }
            }
        }
        return this.invoice;
    }

    private double calculateDistance(Location loc1, Location loc2) {
        double lat1, lon1, lat2, lon2;
        lat1 = loc1.getLatitude();
        lon1 = loc1.getLongitude();
        lat2 = loc2.getLatitude();
        lon2 = loc2.getLongitude();
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344;

        invoice.addToTotalDistance(dist);

        System.out.println("The distance from " + loc1.toString() + " to " + loc2.toString() + " was " + dist + " kilometer, which brings the total distance to " + invoice.getTotalDistance());

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public void testGetInvoice() throws ParseException {
        List<Location> testLocations = new ArrayList<>();
        //Rijksweg Den Bosch-Eindhoven
        testLocations.add(new Location(51.535578, 5.380974));
        testLocations.add(new Location(51.536472, 5.380069));
        testLocations.add(new Location(51.536996, 5.379535));
        testLocations.add(new Location(51.537636, 5.378877));
        testLocations.add(new Location(51.539757, 5.376781));
        testLocations.add(new Location(51.541150, 5.375425));
        testLocations.add(new Location(51.545815, 5.370720));
        testLocations.add(new Location(51.548651, 5.367822));
        testLocations.add(new Location(51.550926, 5.365583));
        //E25
        testLocations.add(new Location(51.519691, 5.397518));
        //geen snelweg
        testLocations.add(new Location(51.521286, 5.399769));
        testLocations.add(new Location(51.520895, 5.391893));

        //PRECIES HETZELFDE NOG EEN KEER, OM TE KIJKEN OF HIJ EEN OPEENVOLGENDE REEKS VAN LOCATIES BIJ EEN WEG ERUIT FILTERT
        //Rijksweg Den Bosch-Eindhoven
        testLocations.add(new Location(51.535578, 5.380974));
        testLocations.add(new Location(51.536472, 5.380069));
        testLocations.add(new Location(51.536996, 5.379535));
        testLocations.add(new Location(51.537636, 5.378877));
        testLocations.add(new Location(51.539757, 5.376781));
        testLocations.add(new Location(51.541150, 5.375425));
        testLocations.add(new Location(51.545815, 5.370720));
        testLocations.add(new Location(51.548651, 5.367822));
        testLocations.add(new Location(51.550926, 5.365583));
        //E25
        testLocations.add(new Location(51.519691, 5.397518));
        //geen snelweg
        testLocations.add(new Location(51.521286, 5.399769));

        this.getInvoice(testLocations);

        /**
         * ******************************************************************
         * TESTCODE 
         *******************************************************************
         */
//        double distanceResult = 0;
//
//        List<SeriesOfLocationsOnRoad> seriesOfLocationsOnRoad = new ArrayList<>();
//        SeriesOfLocationsOnRoad testSerie = new SeriesOfLocationsOnRoad(roads.get(1));
//        testSerie.setLocations(testLocations);
//        seriesOfLocationsOnRoad.add(testSerie);
//        Invoice results = this.processSeriesToTotalAmount(seriesOfLocationsOnRoad);
//        
//        System.out.println("TEST OF DE TOTALE DISTANCE VAN ALLE TESTLOCATIES HETZELFDE IS ALS IN DE GESORTEERDE LIJST");
//        System.out.println("The total amount of the list should be " + results.getTotalAmount());
//        System.out.println("The total disctance of the list should be " + results.getTotalDistance());
//
//        this.invoice = new Invoice();
//        
//        //TEST OF DE DISTANCE 5,3 KM IS
//        testLocations = new ArrayList<>();
//        testLocations.add(new Location(51.513869, 5.403157));
//        //geen snelweg
//        testLocations.add(new Location(51.554182, 5.362072));
//
//        distanceResult = 0;
//
//        seriesOfLocationsOnRoad = new ArrayList<>();
//        testSerie = new SeriesOfLocationsOnRoad(roads.get(1));
//        testSerie.setLocations(testLocations);
//        seriesOfLocationsOnRoad.add(testSerie);
//        results = this.processSeriesToTotalAmount(seriesOfLocationsOnRoad);
//
//        System.out.println("TEST OF DE DISTANCE 5,3 KM IS");
//        System.out.println("The total amount of the list should be " + results.getTotalAmount());
//        System.out.println("The total disctance of the list should be " + results.getTotalDistance());
//        
//        this.invoice = new Invoice();
        /**
         * ******************************************************************
         * EINDE TESTCODE 
         *******************************************************************
         */
    }
    
    public void runBatchJob() {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        jobOperator.start("invoiceBatchJob", new Properties());
    }
}
