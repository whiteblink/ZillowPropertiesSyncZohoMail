package com.whiteblink.PropertiesSync;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.apache.commons.lang3.ThreadUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FinalSyncProgram {

    static JSONArray allProperties = new JSONArray();
    static JSONArray allCountyWithError = new JSONArray();
    static ExecutorService executorService;
    static CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();

    public void finalSyncCSVGenerator() throws InterruptedException, IOException {
        Unirest.config().reset();
        Unirest.config().concurrency(1000, 1000);
        Unirest.config().enableCookieManagement(false).proxy("geo.iproyal.com", 22323, "realestatehenselproxy", "9w2n8YWwbHFa_session-ebf9zxmu_lifetime-10s");

        executorService = Executors.newFixedThreadPool(1000);

        JSONArray allUrls = new JSONArray(readFile("FinalDetails.json"));
        for (int i = 0; i < allUrls.length(); i++) {
            int finalI = i;
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = allUrls.getJSONObject(finalI);
                        extractAllPropertiesFromZillowAPI(jsonObject);
                    } catch (Exception e) {
                    }
                }
            };
            executorService.submit(r1);
        }

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            Thread.sleep(1000);
        }

        System.out.println("Writing to file");
        System.out.println(new File("AllProperties.json").delete());
        PrintWriter printWriter = new PrintWriter(new FileWriter("AllProperties.json"));
        printWriter.write(allProperties.toString());
        printWriter.close();
        System.out.println("Done");

        System.gc();


        System.out.println("Writing to Error file");
        System.out.println(new File("CountyWithSyncError.json").delete());
        PrintWriter printWriter2 = new PrintWriter(new FileWriter("CountyWithSyncError.json"));
        printWriter2.write(allCountyWithError.toString());
        printWriter2.close();
        System.out.println("Done Error");

        System.gc();



        //CSV Conversion

        HashSet<String> jsonFields = new HashSet<>();

        JSONArray jsonArray=new JSONArray(readFileAsString("AllProperties.json"));
        jsonArray=removeDuplicateJSONObject(jsonArray, "detailUrl");
        JSONArray sanitizedJSONArray=new JSONArray();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Set<String> removeKeys=new HashSet<>();
            jsonObject.put("detailUrl","https://www.zillow.com"+jsonObject.get("detailUrl"));
            try {
                try {
                    jsonObject.put("zpid",jsonObject.getJSONObject("hdpData").getJSONObject("homeInfo").get("zpid"));
                } catch (JSONException e) {
                }
                try {
                    jsonObject.put("price",jsonObject.getJSONObject("hdpData").getJSONObject("homeInfo").get("price"));
                } catch (JSONException e) {
                }
                try {
                    jsonObject.put("priceLabel",jsonObject.getJSONObject("hdpData").getJSONObject("homeInfo").get("priceLabel"));
                } catch (JSONException e) {
                }
                try {
                    jsonObject.put("beds",jsonObject.getJSONObject("hdpData").getJSONObject("homeInfo").get("bedrooms"));
                } catch (JSONException e) {
                }
                try {
                    jsonObject.put("baths",jsonObject.getJSONObject("hdpData").getJSONObject("homeInfo").get("bathrooms"));
                } catch (JSONException e) {
                }
                try {
                    jsonObject.put("area",jsonObject.getJSONObject("hdpData").getJSONObject("homeInfo").get("livingArea"));
                } catch (JSONException e) {
                }
                try {
                    jsonObject.put("city",jsonObject.getJSONObject("hdpData").getJSONObject("homeInfo").get("city"));
                } catch (JSONException e) {
                }
                try {
                    jsonObject.put("state",jsonObject.getJSONObject("hdpData").getJSONObject("homeInfo").get("state"));
                } catch (JSONException e) {
                }
                try {
                    jsonObject.put("zipcode",jsonObject.getJSONObject("hdpData").getJSONObject("homeInfo").get("zipcode"));
                } catch (JSONException e) {
                }
                try {
                    jsonObject.put("homeType",jsonObject.getJSONObject("hdpData").getJSONObject("homeInfo").get("homeType"));
                } catch (JSONException e) {
                }
                try {
                    jsonObject.put("homeStatus",jsonObject.getJSONObject("hdpData").getJSONObject("homeInfo").get("homeStatus"));
                } catch (JSONException e) {

                }

            } catch (JSONException e) {
            }

            jsonObject.put("price",removeNonNumeric(jsonObject.get("price").toString()));


            //Itterate all keys in jsonObject
            for (String key : jsonObject.keySet()) {
                if(jsonObject.get(key) instanceof JSONObject){
                    removeKeys.add(key);
                }
            }

            for (String key : removeKeys) {
                jsonObject.remove(key);
            }

            jsonFields.addAll(jsonObject.keySet());
            sanitizedJSONArray.put(jsonObject);
        }

        System.out.println(sanitizedJSONArray);

        JsonNode jsonTree = new ObjectMapper().readTree(sanitizedJSONArray.toString());

        for (String field : jsonFields) {
            csvSchemaBuilder.addColumn(field);
        }

        System.out.println(jsonTree);

        System.out.println(new File("sourceproperties.csv").delete());
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.writerFor(JsonNode.class)
                .with(csvSchema)
                .writeValue(new File("sourceproperties.csv"), jsonTree);
    }

    public static void extractAllPropertiesFromZillowAPI(JSONObject jsonObject) {
        int retryCount = 0;
        while (true) {
            retryCount++;
            try {
                JSONArray properties = new JSONArray();
                JSONObject searchQueryState = jsonObject.getJSONObject("searchQueryState");
                String api = "https://www.zillow.com/search/GetSearchPageState.htm?searchQueryState=" + encode(searchQueryState.toString()) + "&wants=" + encode(jsonObject.getJSONObject("wants").toString());
                HttpResponse<String> responseString = Unirest.get(api).header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.83 Safari/537.36").asString();
                JSONObject response = new JSONObject(responseString.getBody());
                JSONArray mapResults = response.getJSONObject("cat1").getJSONObject("searchResults").getJSONArray("mapResults");
                System.out.println("County: " + jsonObject.getString("countyName") + ", " + jsonObject.getString("state") + " :::::::: MapResultsLength: " + mapResults.length());
                for (int i = 0; i < mapResults.length(); i++) {
                    JSONObject mapResult = mapResults.getJSONObject(i);
                    mapResult.put("countyName", jsonObject.getString("countyName"));
                    mapResult.put("hudFmr", jsonObject.getDouble("hudFmr"));
                    mapResult.put("CashOnCash", getCoc(Double.parseDouble(removeNonNumeric(mapResult.getString("price"))), jsonObject.getDouble("hudFmr")));
                    properties.put(mapResult);
                }
                addToAllProperties(properties);
                System.out.println("Added");
                break;
            } catch (Exception e) {
                System.out.println("Trying Again!! " + e.getMessage());
            }

            if (ThreadUtils.getAllThreads().size() < 300 && retryCount > 20) {
                //Remove Unirest Proxy
                Unirest.config().reset();
                Unirest.config().concurrency(1000, 1000);
                Unirest.config().proxy(null);

                try {
                    JSONArray properties = new JSONArray();
                    JSONObject searchQueryState = jsonObject.getJSONObject("searchQueryState");
                    String api = "https://www.zillow.com/search/GetSearchPageState.htm?searchQueryState=" + encode(searchQueryState.toString()) + "&wants=" + encode(jsonObject.getJSONObject("wants").toString());
                    HttpResponse<String> responseString = Unirest.get(api).header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.83 Safari/537.36").asString();
                    JSONObject response = new JSONObject(responseString.getBody());
                    JSONArray mapResults = response.getJSONObject("cat1").getJSONObject("searchResults").getJSONArray("mapResults");
                    System.out.println("County: " + jsonObject.getString("countyName") + ", " + jsonObject.getString("state") + " :::::::: MapResultsLength: " + mapResults.length());
                    for (int i = 0; i < mapResults.length(); i++) {
                        JSONObject mapResult = mapResults.getJSONObject(i);
                        mapResult.put("countyName", jsonObject.getString("countyName"));
                        mapResult.put("hudFmr", jsonObject.getDouble("hudFmr"));
                        mapResult.put("CashOnCash", getCoc(Double.parseDouble(removeNonNumeric(mapResult.getString("price"))), jsonObject.getDouble("hudFmr")));
                        properties.put(mapResult);
                    }
                    addToAllProperties(properties);
                    System.out.println("Added");
                    break;
                } catch (Exception e) {
                    System.out.println("Trying Again!! " + e.getMessage());
                }

                break;

            }

        }
    }

    public static double getCoc(double housePrice, double hudFmr) {
        double cashOnCash = ((48 * hudFmr) / housePrice) - 0.37896;
        double cashOnCashPercentage = cashOnCash * 100;
        return cashOnCashPercentage;
    }


    private static synchronized void addToAllProperties(JSONArray jsonArray) {
        allProperties.putAll(jsonArray);
    }


    private static String readFile(String fileName) {
        StringBuilder fileData = new StringBuilder();
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(fileName);
            java.nio.file.Files.lines(path).forEach(s -> fileData.append(s).append("\n"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileData.toString();
    }

    private static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    public static String removeNonNumeric(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                sb.append(str.charAt(i));
            }
        }
        return sb.toString();
    }

    //Add Two Numbers
    public static int add(int a, int b) {
        return a + b;
    }

    //Fetch HTML from URL
    public static String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }


    public static String readFileAsString(String filePath) throws IOException{
        byte[] buffer = new byte[(int) new File(filePath).length()];
        FileInputStream f = new FileInputStream(filePath);
        f.read(buffer);
        f.close();
        return new String(buffer);
    }

    //Remove duplicate JSON Object from JSON Array compare key
    public static JSONArray removeDuplicateJSONObject(JSONArray jsonArray, String key) {
        HashSet<String> set = new HashSet<>();
        JSONArray newArray = new JSONArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            try {
                if (set.add(jsonObject.getString(key))) {
                    newArray.put(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println(jsonObject.toString());
                System.exit(-1);
            }
        }
        return newArray;
    }
}
