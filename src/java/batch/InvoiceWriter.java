package batch;


import Model.Invoice;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Serializable;
import static java.lang.System.out;
import java.util.List;
import javax.batch.runtime.context.JobContext;
import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

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
@Named("InvoiceWriter")
public class InvoiceWriter implements javax.batch.api.chunk.ItemWriter {

    @Inject
    private JobContext jobCtx;

    @Override
    public void open(Serializable ckpt) throws Exception {
        //opent niks
    }

    @Override
    public void writeItems(List<Object> items) throws Exception {
        
        for (Object i : items) {
            Invoice invoice = (Invoice) i;
            //TODO OPSLAAN IN DE DATABASE
        }
    }

    @Override
    public void close() throws Exception {
        //sluit niks
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return new MyCheckpoint();
    }
}
