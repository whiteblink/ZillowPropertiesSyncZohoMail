package com.whiteblink.PropertiesSync;

import com.opencsv.CSVReader;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import lombok.SneakyThrows;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncPropertiesToZoho {

    private String zohoAccessToken;

    public Boolean syncPropertyData() throws Exception {
        Unirest.config().reset();
        Unirest.config().concurrency(10, 10);
        Unirest.config().enableCookieManagement(false).proxy("geo.iproyal.com", 22323, "realestatehenselproxy", "9w2n8YWwbHFa_session-ebf9zxmu_lifetime-10s");
        ExecutorService executorService = Executors.newFixedThreadPool(70);
        CSVReader reader = new CSVReader(new FileReader("filteredproperties.csv"));
        String[] line;
        List<String> fetchZpidcolumnNo = new ArrayList<>();
        fetchZpidcolumnNo.addAll(List.of(reader.readNext()));
        int zpidColumnNumber = fetchZpidcolumnNo.indexOf("zpid");
        int hudFmrColNo = fetchZpidcolumnNo.indexOf("hudFmr");
        int cashOnCashColumnNumber = fetchZpidcolumnNo.indexOf("CashOnCash");
        int detailUrlColNo = fetchZpidcolumnNo.indexOf("detailUrl");
        fetchZpidcolumnNo.clear();
        System.gc();
        authenticateZoho();
        while ((line = reader.readNext()) != null) {
            Integer zpid = null;
            Integer hudFmr = null;
            Double cashOnCash = null;
            if (line[zpidColumnNumber] != null && !line[zpidColumnNumber].isEmpty()) {
                zpid = Integer.parseInt(line[zpidColumnNumber]);
            }
            if (line[hudFmrColNo] != null && !line[hudFmrColNo].isEmpty()) {
                hudFmr = Integer.parseInt(line[hudFmrColNo]);
            }
            if (line[cashOnCashColumnNumber] != null && !line[cashOnCashColumnNumber].isEmpty()) {
                cashOnCash = Double.parseDouble(line[cashOnCashColumnNumber]);
            }
            String detailUrl = line[detailUrlColNo];
            Integer finalZpid = zpid;
            Integer finalHudFmr = hudFmr;
            Double finalCashOnCash = cashOnCash;
            if (finalZpid != null && finalHudFmr != null && finalCashOnCash != null) {
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        getZillowPropertyDetailsViaZpid(finalZpid, finalHudFmr, finalCashOnCash, detailUrl, 0);
                        System.gc();
                    }
                };
                executorService.submit(r1);
            }
        }
        reader.close();
        System.out.println("Done");
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            Thread.sleep(1000);
        }
        return true;
    }

    public void getZillowPropertyDetailsViaZpid(Integer zpid, Integer hudFmr, Double CashOnCash, String detailUrl, int count) throws UnirestException {
        if (count < 1000) {
            count++;
            DecimalFormat df2 = new DecimalFormat("#.##");
            try {
                HttpResponse<String> response = Unirest.post("https://www.zillow.com/graphql/")
                        .header("Content-Type", "application/json")
                        .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.83 Safari/537.36")
                        .body(" {\"operationName\": \"ForSaleDoubleScrollFullRenderQuery\",\"variables\": {\"zpid\": " + zpid + "},\r\n\"query\": \"query ForSaleDoubleScrollFullRenderQuery($zpid: ID!) {property(zpid: $zpid) {price bedrooms bathrooms yearBuilt address { streetAddress state city zipcode neighborhood community subdivision } listing_sub_type { is_FSBA is_comingSoon is_newHome is_FSBO is_pending is_foreclosure is_bankOwned is_forAuction} zpid homeStatus countyFIPS description dateSoldString mortgageRates { thirtyYearFixedRate fifteenYearFixedRate arm5Rate} propertyTaxRate lotSize lotAreaValue lotAreaUnits isListedByOwner timeOnZillow pageViewCount parcelId abbreviatedAddress county daysOnZillow listingTypeDimension timeZone hdpUrl stateId countyId cityId isZillowOwned listingDataSource longitude latitude livingArea zestimate propertyTypeDimension livingAreaValue livingAreaUnits rentZestimate restimateLowPercent restimateHighPercent lastSoldPrice country mlsid taxAssessedValue datePostedString taxAssessedYear isRecentStatusChange attributionInfo { agentName agentPhoneNumber agentEmail brokerName brokerPhoneNumber } priceHistory { event time date price pricePerSquareFoot priceChangeRate source buyerAgent { name } sellerAgent { name } showCountyLink postingIsRental} isPremierBuilder postingUrl brokerId hdpTypeDimension currency isPreforeclosureAuction postingProductType priceChangeDate priceChangeDateString marketingName hideZestimate dateSold priceChange datePriceChanged monthlyHoaFee livingAreaUnitsShort zoResaleStartAnOfferEnabled whatILove isFeatured isIncomeRestricted favoriteCount brokerageName taxHistory { time taxPaid taxIncreaseRate value valueIncreaseRate } boroughId isRecentStatusChange isNonOwnerOccupied buildingId county rentalApplicationsAcceptedType featuredListingTypeDimension brokerIdDimension keystoneHomeStatus pageUrlFragment isRentalsLeadCapMet isPaidMultiFamilyBrokerId zipPlusFour regionString homeInsights { insights { modelId treatmentId phrases} } schools { distance name rating level studentsPerTeacher assigned grades link type size totalCount isAssigned } selfTour { hasSelfTour } stateSearchUrl { path } parentRegion { name } pals { name palsId } photos { url } sellingSoon { treatmentId percentile } solarPotential { sunScore buildFactor climateFactor electricityFactor solarFactor} postingContact { name photo { url } } tourViewCount comingSoonOnMarketDate newConstructionType moveInReady moveInCompletionDate contingentListingType zestimateLowPercent zestimateHighPercent longitude latitude city state isListingClaimedByCurrentSignedInUser isCurrentSignedInAgentResponsible virtualTourUrl desktopWebHdpImageLink isCurrentSignedInUserVerifiedOwner hasBadGeocode isCamo vrModel { vrModelGuid revisionId } hasPublicVideo listingSource listingAccount { email } listingFeedID listingProvider { logos { src link } title disclaimerText enhancedVideoURL enhancedDescriptionText showLogos agentName } homeType hoaFee }}\"}")
                        .asString();


                JSONObject dataObject = null;
                try {
                    dataObject = new JSONObject(response.getBody()).optJSONObject("data").optJSONObject("property");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Thread.sleep(3000);
                    getZillowPropertyDetailsViaZpid(zpid, hudFmr, CashOnCash, detailUrl, count);
                }

                if (dataObject != null) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("data", dataObject);

                    JSONObject cDataObject = new JSONObject();

                    JSONObject addressObject = dataObject.optJSONObject("address");
                    JSONObject listingSubType = dataObject.optJSONObject("listing_sub_type");
                    JSONObject attributionInfo = dataObject.optJSONObject("attributionInfo");

                    cDataObject.put("Last_Name", addressObject.optString("streetAddress") + " " + addressObject.optString("city") + " " + addressObject.optString("state") + " " + addressObject.optString("zipcode"));
                    cDataObject.put("county", dataObject.optString("county"));
                    cDataObject.put("livingArea", dataObject.optInt("livingArea") != 0 ? dataObject.optInt("livingArea") : JSONObject.NULL);
                    cDataObject.put("Last_Sold_Price", dataObject.optInt("lastSoldPrice") != 0 ? dataObject.optInt("lastSoldPrice") : JSONObject.NULL);
                    cDataObject.put("State", addressObject.optString("state"));
                    cDataObject.put("timeOnZillow", dataObject.optString("timeOnZillow"));
                    cDataObject.put("Zillow_Rent_Estimate", dataObject.optInt("rentZestimate") != 0 ? dataObject.optInt("rentZestimate") : JSONObject.NULL);
                    cDataObject.put("zestimate", dataObject.optInt("zestimate") != 0 ? dataObject.optInt("zestimate") : JSONObject.NULL);
                    cDataObject.put("streetAddress", addressObject.optString("streetAddress"));
                    cDataObject.put("Country", dataObject.optString("country"));
                    cDataObject.put("isListedByOwner", dataObject.optBoolean("isListedByOwner"));
                    cDataObject.put("homeStatus", dataObject.optString("homeStatus"));
                    cDataObject.put("mlsid", dataObject.optString("mlsid"));
                    cDataObject.put("Tax_Assessed_Value", dataObject.optString("taxAssessedValue"));
                    cDataObject.put("datePostedString", dataObject.optString("datePostedString"));
                    cDataObject.put("listingTypeDimension", dataObject.optString("listingTypeDimension"));
                    cDataObject.put("restimateLowPercent", org.apache.commons.lang3.StringUtils.isNotBlank(dataObject.optString("restimateLowPercent")) ? Integer.parseInt(dataObject.optString("restimateLowPercent")) : null);
                    cDataObject.put("price", dataObject.optInt("price") != 0 ? dataObject.optInt("price") : JSONObject.NULL);
                    cDataObject.put("bathrooms", dataObject.optInt("bathrooms") != 0 ? dataObject.optInt("bathrooms") : JSONObject.NULL);
                    cDataObject.put("bedrooms", dataObject.optInt("bedrooms") != 0 ? dataObject.optInt("bedrooms") : JSONObject.NULL);
                    cDataObject.put("daysOnZillow", dataObject.optInt("daysOnZillow") != 0 ? dataObject.optInt("daysOnZillow") : JSONObject.NULL);
                    cDataObject.put("City", addressObject.optString("city"));
                    cDataObject.put("pageViewCount", dataObject.optInt("pageViewCount") != 0 ? dataObject.optInt("pageViewCount") : JSONObject.NULL);
                    cDataObject.put("taxAssessedYear", dataObject.optInt("taxAssessedYear") != 0 ? dataObject.optInt("taxAssessedYear") : JSONObject.NULL);
                    cDataObject.put("yearBuilt", dataObject.optInt("yearBuilt") != 0 ? dataObject.optInt("yearBuilt") : JSONObject.NULL);
                    cDataObject.put("zipcode", addressObject.optString("zipcode"));
                    cDataObject.put("homeType", dataObject.optString("homeType"));
                    cDataObject.put("lotSize", dataObject.optInt("lotSize") != 0 ? dataObject.optInt("lotSize") : JSONObject.NULL);
                    cDataObject.put("Property_Tax_Rate", dataObject.optDouble("propertyTaxRate") != Double.NaN ? dataObject.optDouble("propertyTaxRate") : null);
                    cDataObject.put("dateSoldString", dataObject.optString("dateSoldString"));
                    cDataObject.put("isRecentStatusChange", dataObject.optBoolean("isRecentStatusChange"));

                    cDataObject.put("zillowUrl", detailUrl);
                    System.out.println("Url: " + detailUrl);

                    cDataObject.put("Description", dataObject.optString("description"));
                    cDataObject.put("isForeclosure", listingSubType.optBoolean("is_foreclosure"));
                    cDataObject.put("isForAuction", listingSubType.optBoolean("is_forAuction"));
                    cDataObject.put("isPending", listingSubType.optBoolean("is_pending"));
                    cDataObject.put("agentName", attributionInfo.optString("agentName"));
                    cDataObject.put("agentPhoneNumber", attributionInfo.optString("agentPhoneNumber"));
                    cDataObject.put("Email", attributionInfo.optString("agentEmail"));
                    cDataObject.put("brokerName", attributionInfo.optString("brokerName"));
                    cDataObject.put("brokerPhoneNumber", attributionInfo.optString("brokerPhoneNumber"));
                    cDataObject.put("zpid", Integer.toString(dataObject.optInt("zpid")));
                    cDataObject.put("HUD_Rent", hudFmr);
                    cDataObject.put("Cash_on_Cash", Double.parseDouble(df2.format(CashOnCash)));

                    jsonObject.put("cData", cDataObject);

                    newSetData(jsonObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Boolean newSetData(JSONObject jsonObject) throws Exception {

        DecimalFormat df2 = new DecimalFormat("#.##");

        try {
            JSONObject dataObject = jsonObject.optJSONObject("data");
            JSONObject cDataObject = jsonObject.optJSONObject("cData");


            int hudFrmRent = cDataObject.optInt("HUD_Rent");
            double houseDownPayment = 0;
            double houseLoanAmount;
            double mortgage = 0;
            double maintainableCost = 0;
            double managementCost = 0;
            double propertyTaxRate;
            double propertyTax = 0;
            double totalExpense = 0;
            double cashOnCash = cDataObject.optInt("Cash_on_Cash");
            double closingCashOnCash = 0;

            int walkScore = 0;
            int bikeScore = 0;
            int transitScore = 0;

            int housePrice = dataObject.optInt("price");
            if (housePrice != 0) {
                String countyFIPS = dataObject.optString("countyFIPS");
                JSONObject addressDataObject = dataObject.optJSONObject("address");
                String houseAddress = addressDataObject.optString("streetAddress") + " " + addressDataObject.optString("city") + " " + addressDataObject.optString("state") + " " + addressDataObject.optString("zipcode");

                if (countyFIPS == null || countyFIPS.equals("")) {
                    String zipCodeResponse = getCountyFipsFromZipCode(addressDataObject.optString("zipcode"));
                    if (!zipCodeResponse.equals("0")) {
                        JSONObject zipCodeResponseJSONObject = new JSONArray(zipCodeResponse).optJSONObject(0);
                        countyFIPS = zipCodeResponseJSONObject.optString("StateFIPS") + zipCodeResponseJSONObject.optString("CountyFIPS");
                        if (countyFIPS.length() < 5) {
                            int noOfZeros = 5 - countyFIPS.length();
                            for (int i = 0; i < noOfZeros; i++) {
                                countyFIPS = "0" + countyFIPS;
                            }
                        }
                    }
                }

                if (countyFIPS != null && !countyFIPS.equals("")) {


                    if (hudFrmRent != 0) {
                        maintainableCost = (hudFrmRent * (10.0f / 100.0f));
                        managementCost = (hudFrmRent * (10.0f / 100.0f));
                        propertyTaxRate = dataObject.opt("propertyTaxRate") != null && !Double.isNaN(dataObject.optDouble("propertyTaxRate")) ? dataObject.optDouble("propertyTaxRate") : 2.99;
                        propertyTax = (housePrice) * (propertyTaxRate / 100.0f);
                        houseDownPayment = ((housePrice) * (20.0f / 100.0f));
                        houseLoanAmount = ((housePrice) - houseDownPayment);
                        mortgage = (mortgageCalculator(houseLoanAmount, 4.00, 30));

                        totalExpense = (mortgage + maintainableCost + managementCost) + (propertyTax / 12);


                        int closingHousePrice = housePrice + 3000;
                        double closingPropertyTax = (closingHousePrice) * (propertyTaxRate / 100.0f);
                        double closingHouseDownPayment = ((closingHousePrice) * (20.0f / 100.0f));
                        double closingHouseLoanAmount = ((closingHousePrice) - closingHouseDownPayment);
                        double closingMortgage = (mortgageCalculator(closingHouseLoanAmount, 4.00, 30));
                        double closingTotalIncome = (hudFrmRent);
                        double closingTotalExpense = (closingMortgage + maintainableCost + managementCost) + (closingPropertyTax / 12);
                        double closingNetIncome = (closingTotalIncome - closingTotalExpense);
                        closingCashOnCash = (closingNetIncome * 12) / closingHouseDownPayment;

                        JSONObject walkScoreJSONObject = getDataFromWalkScoreAPI(houseAddress, dataObject.optDouble("latitude"), dataObject.optDouble("longitude"));

                        JSONObject bikeWalkScore = walkScoreJSONObject.optJSONObject("bike");
                        JSONObject transitJSONObject = walkScoreJSONObject.optJSONObject("transit");
                        walkScore = walkScoreJSONObject.optInt("walkscore");
                        bikeScore = (bikeWalkScore != null) ? bikeWalkScore.optInt("score") : 0;
                        transitScore = (transitJSONObject != null) ? transitJSONObject.optInt("score") : 0;
                        int populationData19 = 0;
                        int populationData14 = 0;
                        JSONArray populationDataArrayJSONObject = getPopulationDataArrayJSONObject(countyFIPS);
                        for (int i = 0; i < populationDataArrayJSONObject.length(); i++) {
                            JSONObject j = populationDataArrayJSONObject.optJSONObject(i);
                            switch (j.getString("DATE_CODE")) {
                                case "7":
                                    cDataObject.put("Population_2014", Integer.parseInt(j.optString("POP")));
                                    populationData14 = Integer.parseInt(j.optString("POP"));
                                    break;
                                case "8":
                                    cDataObject.put("Population_2015", Integer.parseInt(j.optString("POP")));
                                    break;
                                case "9":
                                    cDataObject.put("Population_2016", Integer.parseInt(j.optString("POP")));
                                    break;
                                case "10":
                                    cDataObject.put("Population_2017", Integer.parseInt(j.optString("POP")));
                                    break;
                                case "11":
                                    cDataObject.put("Population_2018", Integer.parseInt(j.optString("POP")));
                                    break;
                                case "12":
                                    cDataObject.put("Population_2019", Integer.parseInt(j.optString("POP")));
                                    populationData19 = Integer.parseInt(j.optString("POP"));
                                    break;
                            }
                        }
                        cDataObject.put("Population_Growth_Rate", Double.parseDouble(df2.format(getPopulationGrowthRate(populationData19, populationData14))));
                    }

                }

                cDataObject.keySet().stream().filter(cDataObject::isNull).forEach(key -> cDataObject.put(key, ""));

                cDataObject.put("Down_Payment", Double.parseDouble(df2.format(houseDownPayment)));
                cDataObject.put("Mortgage", Double.parseDouble(df2.format(mortgage)));
                cDataObject.put("Maintenance_Reserve", Double.parseDouble(df2.format(maintainableCost)));
                cDataObject.put("Management_Cost", Double.parseDouble(df2.format(managementCost)));
                cDataObject.put("Property_Taxes", Double.parseDouble(df2.format(propertyTax)));
                cDataObject.put("Total_Monthly_Expenses", Double.parseDouble(df2.format(totalExpense)));
                cDataObject.put("Closing_Cash_on_Cash", Double.parseDouble(df2.format(closingCashOnCash * 100)));

                cDataObject.put("walkScore", walkScore);
                cDataObject.put("bikeScore", bikeScore);
                cDataObject.put("transitScore", transitScore);

                if (cDataObject.get("homeStatus").equals("FOR_SALE") && cDataObject.get("homeType").equals("SINGLE_FAMILY")) {
                    if (cashOnCash == 0.0) {
                        if (hudFrmRent == 0) {
                            System.out.println("Not Synced( HudFrmRent, " + hudFrmRent + " )");
                        }
                        if (Objects.equals(countyFIPS, "")) {
                            System.out.println("Not Synced( countyFIPS, " + countyFIPS + " )");
                        }
                    } else {
                        if (cashOnCash > 30.0) {
                            String zillowUrl = cDataObject.optString("zillowUrl");
                            if (zillowUrl.chars().filter(ch -> ch == '/').count() >= 6) {
                                JSONObject countyOfficeData = getDataFromCountyOffice(houseAddress.replaceAll(" ", "+"));
                                if (cDataObject.get("State") == "FL") {
                                    if (cDataObject.get("agentName") == "RHP Properties" || cDataObject.get("agentName") == "Treehouse Communities") {
                                        return true;
                                    }
                                }
                                if (countyOfficeData != null) {
                                    Iterator i1 = countyOfficeData.keys();
                                    String tmp_key;
                                    while (i1.hasNext()) {
                                        tmp_key = (String) i1.next();
                                        cDataObject.put(tmp_key, countyOfficeData.get(tmp_key));
                                    }
                                }
                                JSONObject zohoExistingRecord = getSpecificDataFromZohoViaZillowUrl(zillowUrl);
                                String time = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("America/Detroit")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                cDataObject.put("Last_Updated", time);
                                if (zohoExistingRecord == null) {
                                    if (cDataObject.optBoolean("isForAuction") || cDataObject.optBoolean("isForeclosure")) {
                                        cDataObject.put("Lead_Status", "Foreclosure / Auction");
                                    }
                                    if (cDataObject.optBoolean("isPending")) {
                                        cDataObject.put("Lead_Status", "Under Agreement / Sold");
                                    }
                                    JSONObject uploadZohoResponse = uploadDataInZoho(cDataObject);
                                    if (uploadZohoResponse != null) {
                                        String zohoId = uploadZohoResponse.optJSONArray("data").optJSONObject(0).optJSONObject("details").optString("id");
//                                        cDataObject.put("Property_Calculations_spreadsheet", "https://ftgxz3x1a9.execute-api.us-east-2.amazonaws.com/default/zohoLeadXLSX?zohoLeadId=" + zohoId);
//                                        updateDataInZoho(zohoId, cDataObject);
                                    }
                                } else {
                                    String leadStatus = zohoExistingRecord.optJSONArray("data").optJSONObject(0).optString("Lead_Status");
                                    if (leadStatus.equals("Interested") || leadStatus.equals("Unsure") || leadStatus.equals("")) {
                                        String zohoId = zohoExistingRecord.optJSONArray("data").optJSONObject(0).optString("id");
                                        if (cDataObject.optBoolean("isForAuction") || cDataObject.optBoolean("isForeclosure")) {
                                            cDataObject.put("Lead_Status", "Foreclosure / Auction");
                                        }
                                        if (cDataObject.optBoolean("isPending")) {
                                            cDataObject.put("Lead_Status", "Under Agreement / Sold");
                                        }
//                                        cDataObject.put("Property_Calculations_spreadsheet", "https://ftgxz3x1a9.execute-api.us-east-2.amazonaws.com/default/zohoLeadXLSX?zohoLeadId=" + zohoId);
                                        JSONObject updateZohoResponse = updateDataInZoho(zohoId, cDataObject);
                                    } else {
                                        System.out.println("Not Synced(Lead Status, Lead Status = " + leadStatus + ")");
                                    }
                                }
                            } else {
                                System.out.println("Not Synced( Small Url, Small Url = " + zillowUrl + " )");
                            }
                        } else {
                            System.out.println("Not Synced(COC > 0.3, COC = " + cashOnCash + ")");
                        }
                    }
                } else {
                    System.out.println("Not Synced(Home Status, Home Status = " + cDataObject.get("homeStatus") + " )");
                    System.out.println("Not Synced(Home Type, Home Type = " + cDataObject.get("homeType") + " )");
                }
            }
        } catch (Exception e) {
            System.out.println("Not Added : " + jsonObject);
            e.printStackTrace();
        }
        return true;
    }

    public void authenticateZoho() throws Exception {
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();

        HttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
        com.mashape.unirest.http.Unirest.setHttpClient(httpclient);
        String zohoRefreshToken = "1000.ed37d1c8790a890600d436894b2124f3.e08056519a72409669a6188e4ef8d59f";
        String zohoClientSecret = "748cae3c4590953c0244e509cd208e2e806523ecd3";
        String zohoClientId = "1000.L9KZ7QLUEG1W7FD96A6NYID9TVI95C";
        com.mashape.unirest.http.HttpResponse<String> response = com.mashape.unirest.http.Unirest.post("https://accounts.zoho.com/oauth/v2/token?refresh_token=" + zohoRefreshToken + "&client_id=" + zohoClientId + "&client_secret=" + zohoClientSecret + "&grant_type=refresh_token")
                .asString();
        zohoAccessToken = new JSONObject(response.getBody()).optString("access_token");
    }

    private String getCountyFipsFromZipCode(String zipcode) throws Exception {
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();

        HttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
        com.mashape.unirest.http.Unirest.setHttpClient(httpclient);
        com.mashape.unirest.http.HttpResponse<String> zipCodeResponse = com.mashape.unirest.http.Unirest.get("http://myselfhealthapi.abhi.c2k.in/api.php?zipcode=" + zipcode)
                .asString();
        return zipCodeResponse.getBody();
    }

    private double mortgageCalculator(double principle, double rate, double time) {
        // convert rate for month format
        rate = (rate / 100) / 12;

        // convert time in the terms of months
        time = time * 12;

        // M = P [{r*(1+r)^n}/{(1+r)^n – 1}]
        return principle * ((rate * Math.pow(1 + rate, time))
                / (Math.pow(1 + rate, time) - 1));
    }

    private JSONObject getDataFromWalkScoreAPI(String address, double lat, double lon) throws Exception {
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();

        HttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
        com.mashape.unirest.http.Unirest.setHttpClient(httpclient);
        String walkScoreAPIKey = "6e7618f75b0179a37bc3b1132ffabf29";
        com.mashape.unirest.http.HttpResponse<String> response = com.mashape.unirest.http.Unirest.get("https://api.walkscore.com/score?format=json&address=" + URLEncoder.encode(address, StandardCharsets.UTF_8) + "&lat=" + lat + "&lon=" + lon + "&transit=1&bike=1&wsapikey=" + walkScoreAPIKey)
                .asString();
        return new JSONObject(response.getBody());
    }

    public JSONArray getPopulationDataArrayJSONObject(String fipsCode) throws Exception {
        String stateFips = fipsCode.substring(0, 2);
        String countyFips = fipsCode.substring(2, 5);
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();

        HttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
        com.mashape.unirest.http.Unirest.setHttpClient(httpclient);
        String censusAPIKey = "cc3dcb97d78118cc256d6c69bf615df739eb2b6e";
        com.mashape.unirest.http.HttpResponse<String> response = com.mashape.unirest.http.Unirest.get("https://api.census.gov/data/2019/pep/population?get=DATE_CODE,POP,NAME&for=county:" + countyFips + "&in=state:" + stateFips + "&key=" + censusAPIKey)
                .asString();
        JSONArray populationDataFromCensusAPI = new JSONArray(response.getBody());
        JSONArray populationDataOutputJSONArray = new JSONArray();
        JSONArray populationDataFromCensusAPIJSONArrayKeys = populationDataFromCensusAPI.getJSONArray(0);
        int c = 1;
        int d = 0;
        while (c < populationDataFromCensusAPI.length()) {
            JSONArray jsonArray = populationDataFromCensusAPI.getJSONArray(c);
            JSONObject jsonObject = new JSONObject();
            while (d < jsonArray.length()) {
                jsonObject.put(populationDataFromCensusAPIJSONArrayKeys.getString(d), jsonArray.getString(d));
                d++;
            }
            populationDataOutputJSONArray.put(jsonObject);
            d = 0;
            c++;
        }
        return populationDataOutputJSONArray;
    }

    public double getPopulationGrowthRate(int present, int past) {
        double pp = (double) present / (double) past;
        return (Math.pow(pp, 1.0 / 10.0) - 1) * 100;
    }

    public JSONObject getDataFromCountyOffice(String houseAddress) throws IOException {
        String url = "https://www.countyoffice.org/property-records-search/?q=" + houseAddress.replace(" ", "+");

        String command = "curl -L " + url + " -x http://realestatehenselproxy:9w2n8YWwbHFa@91.239.130.17:12323";
        Process process = Runtime.getRuntime().exec(command);

        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        StringBuilder stringBuilder = new StringBuilder();

        while ((line = br.readLine()) != null) {
            stringBuilder.append(line);
        }

        Document document = Jsoup.parse(String.valueOf(stringBuilder));
        if (document.text().contains("Address Not Found")) {
            System.out.println("Address Not Found : " + houseAddress);
            return null;
        } else if (document.text().contains("Daily Limit")) {
            return getDataFromCountyOffice(houseAddress);
        } else {
            return parseCountyOfficeData(document);
        }
    }

    public JSONObject parseCountyOfficeData(Document document) {
        try {
            JSONObject jsonObject = new JSONObject();
            Element parcelElement = document.getElementById("parcel");
            Element structureElement = document.getElementById("structure");
            if (parcelElement != null) {
                JSONObject parcelJson = tableToJson(parcelElement.nextElementSibling().toString());
                Iterator<String> i1 = parcelJson.keys();
                String tmp_key;
                while (i1.hasNext()) {
                    tmp_key = i1.next();
                    String key = tmp_key;
                    if (tmp_key.equals("County")) {
                        key = "County_Office_County";
                    }
                    jsonObject.put(key, parcelJson.get(tmp_key));
                }
            }

            if (structureElement != null) {
                JSONObject propertyStructureJson = tableToJson(structureElement.nextElementSibling().toString());
                Iterator<String> i2 = propertyStructureJson.keys();
                String tmp_key;
                while (i2.hasNext()) {
                    tmp_key = i2.next();
                    String key = tmp_key;
                    if (tmp_key.equals("Bedrooms")) {
                        key = "County_Office_Bedrooms";
                    }
                    if (tmp_key.equals("Bathrooms")) {
                        key = "County_Office_Bathrooms";
                    }
                    jsonObject.put(key, propertyStructureJson.get(tmp_key));
                }
            }

            return jsonObject;
        } catch (Exception e) {
            System.out.println(document.text());
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getSpecificDataFromZohoViaZillowUrl(String zillowURL) throws Exception {
        Thread.sleep(6000);
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();

        HttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
        com.mashape.unirest.http.Unirest.setHttpClient(httpclient);
        com.mashape.unirest.http.HttpResponse<String> response = com.mashape.unirest.http.Unirest.get("https://www.zohoapis.com/crm/v2/Leads/search?criteria=zillowUrl:equals:" + zillowURL)
                .header("Authorization", "Zoho-oauthtoken " + zohoAccessToken)
                .asString();
        JSONObject jsonResponseObject = null;
        if (response.getBody() != null) {
            jsonResponseObject = new JSONObject(response.getBody());
            if (jsonResponseObject.optString("code").equals("INVALID_TOKEN")) {
                authenticateZoho();
                response = com.mashape.unirest.http.Unirest.get("https://www.zohoapis.com/crm/v2/Leads/search?criteria=zillowUrl:equals:" + zillowURL)
                        .header("Authorization", "Zoho-oauthtoken " + zohoAccessToken)
                        .asString();
                if (response.getBody() != null) {
                    jsonResponseObject = new JSONObject(response.getBody());
                }
            }
        }
        if (response.getStatus() == 200) {
            return jsonResponseObject;
        } else {
            return null;
        }
    }

    public JSONObject uploadDataInZoho(JSONObject data) throws Exception {
        Thread.sleep(6000);
        com.mashape.unirest.http.HttpResponse<String> response = com.mashape.unirest.http.Unirest.post("https://www.zohoapis.com/crm/v2/Leads")
                .header("Authorization", "Zoho-oauthtoken " + zohoAccessToken)
                .header("Content-Type", "application/json")
                .body("{ \"data\": [" + data.toString() + "],\"trigger\": [\"approval\",\"workflow\",\"blueprint\"]}")
                .asString();
        JSONObject jsonResponseObject = new JSONObject(response.getBody());
        if (jsonResponseObject.optString("code").equals("INVALID_TOKEN")) {
            authenticateZoho();
            response = com.mashape.unirest.http.Unirest.post("https://www.zohoapis.com/crm/v2/Leads")
                    .header("Authorization", "Zoho-oauthtoken " + zohoAccessToken)
                    .header("Content-Type", "application/json")
                    .body("{ \"data\": [" + data + "],\"trigger\": [\"approval\",\"workflow\",\"blueprint\"]}")
                    .asString();
            jsonResponseObject = new JSONObject(response.getBody());
        }
        if (response.getStatus() == 201) {
            System.out.println("Added To Zoho");
            return jsonResponseObject;
        } else if (response.getStatus() == 202) {
            return jsonResponseObject;
        } else if (response.getStatus() == 429) {
            System.out.println("Zoho API Limit Reached");
            Thread.sleep(43200000);
            return null;
        } else {
            System.out.println("Failed to Add in Zoho : " + jsonResponseObject);
            System.out.println("Not Added : " + data);
            return null;
        }
    }

    public JSONObject updateDataInZoho(String zohoDataId, JSONObject data) throws Exception {
        Thread.sleep(6000);
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();

        HttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
        com.mashape.unirest.http.Unirest.setHttpClient(httpclient);
        com.mashape.unirest.http.HttpResponse<String> response = com.mashape.unirest.http.Unirest.put("https://www.zohoapis.com/crm/v2/Leads/" + zohoDataId)
                .header("Authorization", "Zoho-oauthtoken " + zohoAccessToken)
                .header("Content-Type", "application/json")
                .body("{ \"data\": [" + data.toString() + "],\"trigger\": [\"approval\",\"workflow\",\"blueprint\"]}")
                .asString();

        JSONObject jsonResponseObject = new JSONObject(response.getBody());

        if (jsonResponseObject.optString("code").equals("INVALID_TOKEN") || jsonResponseObject.optString("code").equals("AUTHENTICATION_FAILURE")) {
            authenticateZoho();
            response = com.mashape.unirest.http.Unirest.put("https://www.zohoapis.com/crm/v2/Leads/" + zohoDataId)
                    .header("Authorization", "Zoho-oauthtoken " + zohoAccessToken)
                    .header("Content-Type", "application/json")
                    .body("{ \"data\": [" + data + "],\"trigger\": [\"approval\",\"workflow\",\"blueprint\"]}")
                    .asString();
            jsonResponseObject = new JSONObject(response.getBody());
        }
        if (response.getStatus() == 200) {
            System.out.println("Updated in Zoho");
            return jsonResponseObject;
        } else if (response.getStatus() == 429) {
            System.out.println("Zoho API Limit Reached");
            Thread.sleep(43200000);
            return null;
        } else {
            System.out.println("Failed to Update in Zoho");
            System.out.println("Not Updated : " + data);
            System.out.println(jsonResponseObject);
            return null;
        }
    }

    public JSONObject tableToJson(String source) throws JSONException {
        Document doc = Jsoup.parse(source);
        JSONObject jsonObject = new JSONObject();
        Element table = doc.selectFirst("tbody");
        for (Element row : table.select("tr")) {
            Elements th = row.select("th");
            Elements td = row.select("td");
            jsonObject.put(th.text().replace(" ", "_"), td.text());
        }
        return jsonObject;
    }
}
