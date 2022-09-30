package com.whiteblink.PropertiesReSync;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AllZohoProperties {

    private String zohoAccessToken;

    public void getAllZohoProperties() throws Exception {

        authenticateZoho();
        HttpResponse<String> response = Unirest.post("https://www.zohoapis.com/crm/bulk/v3/read")
                .header("Authorization", "Zoho-oauthtoken " + zohoAccessToken)
                .header("Content-Type", "application/json")
                .body("{\"query\": {\"module\": {\"api_name\": \"Leads\"},\"file_type\": \"csv\"}}")
                .asString();
        JSONObject createBulkRequestResponse=new JSONObject(response.getBody());
        String bulkRequestID=createBulkRequestResponse.getJSONArray("data").getJSONObject(0).getJSONObject("details").getString("id");
        System.out.println("Bulk Request ID: "+bulkRequestID);

        while(true){
            HttpResponse<String> bulkRequestStatusResponse = Unirest.get("https://www.zohoapis.com/crm/bulk/v3/read/"+bulkRequestID)
                    .header("Authorization", "Zoho-oauthtoken " + zohoAccessToken)
                    .header("Content-Type", "application/json")
                    .asString();
            JSONObject bulkRequestStatus=new JSONObject(bulkRequestStatusResponse.getBody());

            if(bulkRequestStatus.optString("code").equals("INVALID_TOKEN") || bulkRequestStatus.optString("code").equals("AUTHENTICATION_FAILURE")){
                authenticateZoho();
                bulkRequestStatusResponse = Unirest.get("https://www.zohoapis.com/crm/bulk/v3/read/"+bulkRequestID)
                        .header("Authorization", "Zoho-oauthtoken " + zohoAccessToken)
                        .asString();
                bulkRequestStatus=new JSONObject(bulkRequestStatusResponse.getBody());
            }

            String status=bulkRequestStatus.getJSONArray("data").getJSONObject(0).getString("state");
            System.out.println("Bulk Request Status: "+status);
            if(status.equals("COMPLETED")){
                System.out.println("Bulk Request Completed");
                break;
            }
            Thread.sleep(50000);
        }

        File bulkRequestDownloadResponse = Unirest.get("https://www.zohoapis.com/crm/bulk/v3/read/"+bulkRequestID+"/result")
                .header("Authorization", "Zoho-oauthtoken " + zohoAccessToken)
                .asFile("ZohoLeads.zip").getBody();
        System.out.println("Bulk Request Downloaded");

        unzip("ZohoLeads.zip","ZohoLeads");
    }

    public void authenticateZoho() throws Exception {
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();

        HttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
        com.mashape.unirest.http.Unirest.setHttpClient(httpclient);
        String zohoRefreshToken = "1000.bbfd04e97cd0c9aa559323e6f337821b.0efa14306c15996f754549fe6db26109";
        String zohoClientSecret = "748cae3c4590953c0244e509cd208e2e806523ecd3";
        String zohoClientId = "1000.L9KZ7QLUEG1W7FD96A6NYID9TVI95C";
        com.mashape.unirest.http.HttpResponse<String> response = com.mashape.unirest.http.Unirest.post("https://accounts.zoho.com/oauth/v2/token?refresh_token=" + zohoRefreshToken + "&client_id=" + zohoClientId + "&client_secret=" + zohoClientSecret + "&grant_type=refresh_token")
                .asString();
        zohoAccessToken = new JSONObject(response.getBody()).optString("access_token");
    }

    private static void unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
