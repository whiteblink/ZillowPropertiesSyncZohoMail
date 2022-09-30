package com.whiteblink.PropertiesSync;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

public class AddLinkToJSON {
    public void syncPropertiesFromMapBounds() throws IOException {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("LinkDetailsWithMapBounds.json");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        JSONArray jsonArray = new JSONArray(stringBuilder.toString());

        JSONArray finalJsonArray=new JSONArray();

        ArrayList<String> listOfStates = new ArrayList<>();

        //read txt file form resource folder
        InputStream inputStream1 = getClass().getClassLoader().getResourceAsStream("listOfStates.txt");
        BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream1)));
        String line1;
        while ((line1 = bufferedReader1.readLine()) != null) {
            listOfStates.add(line1);
        }

        for(int i=0;i<jsonArray.length();i++) {
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            for (String d: listOfStates) {
                if(Objects.equals(jsonObject.getString("state"), d)) {
                    jsonObject.put("searchQueryState",getSearchQueryState(jsonObject));
                    jsonObject.put("wants",getWants(jsonObject));
                    finalJsonArray.put(jsonObject);
                }
            }
        }

        PrintWriter pw=new PrintWriter(new FileWriter("FinalDetails.json"));
        pw.write("");
        pw.write(finalJsonArray.toString());
        pw.close();
    }

    private static String readFile(String fileName) {
        StringBuilder fileData=new StringBuilder();
        try {
            java.nio.file.Path path=java.nio.file.Paths.get(fileName);
            java.nio.file.Files.lines(path).forEach(s -> fileData.append(s).append("\n"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileData.toString();
    }

    private static JSONObject getSearchQueryState(JSONObject jsonObject) {
        JSONObject mapBoundObject=new JSONObject("{\"filterState\":{\"sortSelection\":{\"value\":\"pricea\"},\"price\":{\"min\":5000,\"max\":75423},\"beds\":{\"min\":4,\"max\":4},\"isAllHomes\":{\"value\":\"true\"}},\"pagination\":{},\"mapBounds\":{\"east\":-81.374772,\"south\":41.275044,\"north\":42.096402,\"west\":-81.971262},\"usersSearchTerm\":\"Cuyahoga County,OH\",\"isListVisible\":true,\"isMapVisible\":true}");
        mapBoundObject.getJSONObject("filterState").getJSONObject("price").put("min",5000);
        mapBoundObject.getJSONObject("filterState").getJSONObject("price").put("max",(int)jsonObject.getDouble("housePrice"));
        mapBoundObject.getJSONObject("filterState").getJSONObject("beds").put("min",jsonObject.getInt("rooms"));
        mapBoundObject.getJSONObject("filterState").getJSONObject("beds").put("max",jsonObject.getInt("rooms"));
        mapBoundObject.put("mapBounds",jsonObject.getJSONObject("mapBounds"));
        mapBoundObject.put("usersSearchTerm",jsonObject.getString("countyName")+", "+jsonObject.getString("state"));
        return mapBoundObject;
    }

    private static JSONObject getWants(JSONObject jsonObject) {
        JSONObject wantObject=new JSONObject("{\"cat2\":[\"total\"],\"cat1\":[\"listResults\",\"mapResults\"]}");
        return wantObject;
    }
}

