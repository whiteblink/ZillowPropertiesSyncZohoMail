package com.whiteblink;

import com.whiteblink.PropertiesReSync.AllZohoProperties;
import com.whiteblink.PropertiesReSync.SyncPropertiesCSVtoCSV;
import com.whiteblink.PropertiesSync.*;

import java.time.LocalDate;

public class App {
    public static void main( String[] args ) {
        AddLinkToJSON addLinkToJSON=new AddLinkToJSON();
        FinalSyncProgram finalSyncProgram=new FinalSyncProgram();
        CSVFilter csvFilter=new CSVFilter();
        CSVMailer csvMailer=new CSVMailer();
        SyncPropertiesToZoho syncPropertiesToZoho=new SyncPropertiesToZoho();
        AllZohoProperties allZohoProperties=new AllZohoProperties();
        SyncPropertiesCSVtoCSV syncPropertiesCSVtoCSV=new SyncPropertiesCSVtoCSV();
        try {
//            addLinkToJSON.syncPropertiesFromMapBounds();
//            finalSyncProgram.finalSyncCSVGenerator();
//            csvFilter.finalCSVGeneratorFilteredCSV();
//            csvMailer.sendMail("Zillow Properties "+ LocalDate.now(),"Please find the attached CSV file for the properties");
//            syncPropertiesToZoho.syncPropertyData();
//            csvMailer.sendMail("Success! Zillow->Zoho "+ LocalDate.now(),"Zillow properties synced to zoho successfully!");
//            allZohoProperties.getAllZohoProperties();
            syncPropertiesCSVtoCSV.syncPropertiesCSVtoCSV("ZohoLeads/5156249000006940251.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
