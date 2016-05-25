package batch;


import Logic.LocationsCalculator;
import Model.Invoice;
import Model.Location;
import static java.lang.System.out;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Max
 */
@Dependent
@Named("InvoiceProcessor")
public class InvoiceProcessor implements javax.batch.api.chunk.ItemProcessor {
    
    public InvoiceProcessor() {
    }

    @Override
    public Object processItem(Object obj) throws Exception {
        JSONObject containerobj = (JSONObject) obj;
        JSONArray locArray = (JSONArray) containerobj.get("list");
        List<Location> locList = new ArrayList<>();
        
        for (int i = 0; i < locArray.size(); i++) {
            JSONObject location = (JSONObject) locArray.get(i);
            double latitude = (double) location.get("latitude");
            double longitude = (double) location.get("longitude");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse((String) location.get("date"));
            Location newLocation = new Location(latitude, longitude, date);
            locList.add(newLocation);
        }
        LocationsCalculator lc = new LocationsCalculator();
        Invoice newInvoice = lc.getInvoice(locList);
        
        return newInvoice;
    }
}
