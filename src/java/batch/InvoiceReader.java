package batch;


import java.io.FileReader;
import java.io.Serializable;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import org.json.simple.parser.JSONParser;
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
@Named("InvoiceReader")
public class InvoiceReader implements javax.batch.api.chunk.ItemReader {

    private MyCheckpoint checkpoint;
    @Inject
    JobContext jobCtx;
    JSONArray jsonarr;

    public InvoiceReader() {
    }

    @Override
    public void open(Serializable ckpt) throws Exception {
        if (ckpt == null) {
            checkpoint = new MyCheckpoint();
        } else {
            checkpoint = (MyCheckpoint) ckpt;
        }

        JSONParser parser = new JSONParser();
        JSONObject jsonobj = //TODO GET JSONOBJECT CONTAINER FROM DATABASE
        jsonarr = (JSONArray) jsonobj.get("Invoices");
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public Object readItem() throws Exception {
        if ((int)checkpoint.getLineNum() < jsonarr.size()) {
            Object row = jsonarr.get((int) checkpoint.getLineNum());
            checkpoint.increase();
            return row;
        }
        else
        {
            return null;
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return checkpoint;
    }
}
