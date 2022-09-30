package com.whiteblink.PropertiesReSync;

import com.opencsv.CSVReader;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncPropertiesCSVtoCSV {

    //Id,Owner,Company,First_Name,Last_Name,Designation,Email,Phone,Fax,Mobile,Website,Lead_Source,Lead_Status,Industry,No_of_Employees,Annual_Revenue,Rating,Campaign_Source,Created_By,Modified_By,Created_Time,Modified_Time,Full_Name,Street,City,State,Zip_Code,Country,Description,Skype_ID,Email_Opt_Out,Salutation,Secondary_Email,Currency,Exchange_Rate,Last_Activity_Time,Twitter,Layout,Score,Positive_Score,Negative_Score,Positive_Touch_Point_Score,Touch_Point_Score,Negative_Touch_Point_Score,Tag,Record_Image,LAST_ACTION,LAST_SENT_TIME,LAST_ACTION_TIME,User_Modified_Time,System_Related_Activity_Time,User_Related_Activity_Time,System_Modified_Time,Converted_Date_Time,Record_Approval_Status,Is_Record_Duplicate,Lead_Conversion_Time,Unsubscribed_Mode,Unsubscribed_Time,Converted_Account,Converted_Contact,Converted_Deal,Territories,Change_Log_Time__s,Converted__s,$converted,listingTypeDimension,county,homeType,isListedByOwner,homeStatus,streetAddress,isRecentStatusChange,mlsid,zipcode,zillowUrl,livingArea,daysOnZillow,bedrooms,datePostedString,bathrooms,dateSoldString,lotSize,yearBuilt,Cash_on_Cash,taxAssessedYear,pageViewCount,transitScore,walkScore,bikeScore,timeOnZillow,price,zestimate,HUD_Rent,Zillow_Rent_Estimate,Management_Cost,Last_Sold_Price,Mortgage,Down_Payment,Tax_Assessed_Value,Maintenance_Reserve,Total_Monthly_Expenses,Property_Taxes,Free_Cash_Year_2,Free_Cash,Property_Tax_Rate,isForeclosure,Last_Updated,brokerName,agentName,brokerPhoneNumber,agentPhoneNumber,agentEmail,isPending,isForAuction,Next_Action_Note,Next_Action_Date,Next_Action,First_Visited_Time,Visitor_Score,Referrer,Average_Time_Spent_Minutes,Last_Visited_Time,First_Visited_URL,Number_Of_Chats,Days_Visited,Property_Calculations_spreadsheet,Closing_Cash_on_Cash,Secondary_Review,zpid,Neigborhood,Year,Land_Use_Category,Stories,Tax_Account,County_Office_Bedrooms,Lot_Number,Structure_Condition,Total_Area,Interior_Walls,Rooms,Address,Single_Line_28,County_Office_Bathrooms,Carrier_Code,Foundation,Flooring_Types,Subdivision,Exterior_Walls,Zoning_Designation,Roof_Style,County_Office_County,Fireplaces,Depth,Air_Conditioning,Frontage,Heating,Census_Tract,Roof_Material,Area,Legal_Description,Land_Use_Code,Population_2018,Population_2017,Population_2019,Population_2014,Population_2016,Population_2015,Population_Growth_Rate,Locked__s,Last_Enriched_Time__s,Enrich_Status__s

    public void syncPropertiesCSVtoCSV(String fileName) throws Exception {
        Unirest.config().reset();
        Unirest.config().concurrency(10, 10);
        Unirest.config().enableCookieManagement(false).proxy("geo.iproyal.com", 22323, "realestatehenselproxy", "9w2n8YWwbHFa_session-ebf9zxmu_lifetime-10s");
        ExecutorService executorService = Executors.newFixedThreadPool(70);
        JSONArray jsonArray = new JSONArray();
        //read CSV file`
        CSVReader reader = new CSVReader(new FileReader(fileName));
        String[] nextLine;
        int count = 0;
        while ((nextLine = reader.readNext()) != null) {
            if (count == 0) {
                count++;
                continue;
            }
            String id = nextLine[0];
            String owner = nextLine[1];
            String company = nextLine[2];
            String firstName = nextLine[3];
            String lastName = nextLine[4];
            String designation = nextLine[5];
            String email = nextLine[6];
            String phone = nextLine[7];
            String fax = nextLine[8];
            String mobile = nextLine[9];
            String website = nextLine[10];
            String leadSource = nextLine[11];
            String leadStatus = nextLine[12];
            String industry = nextLine[13];
            String noOfEmployees = nextLine[14];
            String annualRevenue = nextLine[15];
            String rating = nextLine[16];
            String campaignSource = nextLine[17];
            String createdBy = nextLine[18];
            String modifiedBy = nextLine[19];
            String createdTime = nextLine[20];
            String modifiedTime = nextLine[21];
            String fullName = nextLine[22];
            String street = nextLine[23];
            String city = nextLine[24];
            String state = nextLine[25];
            String zipCode = nextLine[26];
            String country = nextLine[27];
            String description = nextLine[28];
            String skypeID = nextLine[29];
            String emailOptOut = nextLine[30];
            String salutation = nextLine[31];
            String secondaryEmail = nextLine[32];
            String currency = nextLine[33];
            String exchangeRate = nextLine[34];
            String lastActivityTime = nextLine[35];
            String twitter = nextLine[36];
            String layout = nextLine[37];
            String score = nextLine[38];
            String positiveScore = nextLine[39];
            String negativeScore = nextLine[40];
            String positiveTouchPointScore = nextLine[41];
            String touchPointScore = nextLine[42];
            String negativeTouchPointScore = nextLine[43];
            String tag = nextLine[44];
            String recordImage = nextLine[45];
            String lastAction = nextLine[46];
            String lastSentTime = nextLine[47];
            String lastActionTime = nextLine[48];
            String userModifiedTime = nextLine[49];
            String systemRelatedActivityTime = nextLine[50];
            String userRelatedActivityTime = nextLine[51];
            String systemModifiedTime = nextLine[52];
            String convertedDateTime = nextLine[53];
            String recordApprovalStatus = nextLine[54];
            String isRecordDuplicate = nextLine[55];
            String leadConversionTime = nextLine[56];
            String unsubscribedMode = nextLine[57];
            String unsubscribedTime = nextLine[58];
            String convertedAccount = nextLine[59];
            String convertedContact = nextLine[60];
            String convertedDeal = nextLine[61];
            String territories = nextLine[62];
            String changeLogTime_s = nextLine[63];
            String converted_s = nextLine[64];
            String listingTypeDimension = nextLine[65];
            String county = nextLine[66];
            String homeType = nextLine[67];
            String isListedByOwner = nextLine[68];
            String homeStatus = nextLine[69];
            String streetAddress = nextLine[70];
            String isRecentStatusChange = nextLine[71];
            String mlsid = nextLine[72];
            String zipcode = nextLine[73];
            String zillowUrl = nextLine[74];
            String livingArea = nextLine[75];
            String daysOnZillow = nextLine[76];
            String bedrooms = nextLine[77];
            String datePostedString = nextLine[78];
            String bathrooms = nextLine[79];
            String dateSoldString = nextLine[80];
            String lotSize = nextLine[81];
            String yearBuilt = nextLine[82];
            String Cash_on_Cash = nextLine[83];
            String taxAssessedYear = nextLine[84];
            String pageViewCount = nextLine[85];
            String transitScore = nextLine[86];
            String walkScore = nextLine[87];
            String bikeScore = nextLine[88];
            String timeOnZillow = nextLine[89];
            String price = nextLine[90];
            String zestimate = nextLine[91];
            String HUD_Rent = nextLine[92];
            String Zillow_Rent_Estimate = nextLine[93];
            String Management_Cost = nextLine[94];
            String Last_Sold_Price = nextLine[95];
            String Mortgage = nextLine[96];
            String Down_Payment = nextLine[97];
            String Tax_Assessed_Value = nextLine[98];
            String Maintenance_Reserve = nextLine[99];
            String Total_Monthly_Expenses = nextLine[100];
            String Property_Taxes = nextLine[101];
            String Free_Cash_Year_2 = nextLine[102];
            String Free_Cash = nextLine[103];
            String Property_Tax_Rate = nextLine[104];
            String isForeclosure = nextLine[105];
            String Last_Updated = nextLine[106];
            String brokerName = nextLine[107];
            String agentName = nextLine[108];
            String brokerPhoneNumber = nextLine[109];
            String agentPhoneNumber = nextLine[110];
            String brokerEmail = nextLine[111];
            String agentEmail = nextLine[112];
            String isPending = nextLine[113];
            String isForAuction = nextLine[114];
            String Next_Action_Note = nextLine[115];
            String Next_Action_Date = nextLine[116];
            String Next_Action = nextLine[117];
            String First_Visited_Time = nextLine[118];
            String Visitor_Score = nextLine[119];
            String Referrer = nextLine[120];
            String Average_Time_Spent_Minutes = nextLine[121];
            String Last_Visited_Time = nextLine[122];
            String First_Visited_URL = nextLine[123];
            String Number_Of_Chats = nextLine[124];
            String Days_Visited = nextLine[125];
            String Property_Calculations_spreadsheet = nextLine[126];
            String Closing_Cash_on_Cash = nextLine[127];
            String Secondary_Review = nextLine[128];
            String zpid = nextLine[129];
            String Neigborhood = nextLine[130];
            String Year = nextLine[131];
            String Land_Use_Category = nextLine[132];
            String Stories = nextLine[133];
            String Tax_Account = nextLine[134];
            String County_Office_Bedrooms = nextLine[135];
            String Lot_Number = nextLine[136];
            String Structure_Condition = nextLine[137];
            String Total_Area = nextLine[138];
            String Interior_Walls = nextLine[139];
            String Rooms = nextLine[140];
            String Address = nextLine[141];
            String Single_Line_28 = nextLine[142];
            String County_Office_Bathrooms = nextLine[143];
            String Carrier_Code = nextLine[144];
            String Foundation = nextLine[145];
            String Flooring_Types = nextLine[146];
            String Subdivision = nextLine[147];
            String Exterior_Walls = nextLine[148];
            String Zoning_Designation = nextLine[149];
            String Roof_Style = nextLine[150];
            String County_Office_County = nextLine[151];
            String Fireplaces = nextLine[152];
            String Depth = nextLine[153];
            String Air_Conditioning = nextLine[154];
            String Frontage = nextLine[155];
            String Heating = nextLine[156];
            String Census_Tract = nextLine[157];
            String Roof_Material = nextLine[158];
            String Area = nextLine[159];
            String Legal_Description = nextLine[160];
            String Land_Use_Code = nextLine[161];
            String Population_2018 = nextLine[162];
            String Population_2017 = nextLine[163];
            String Population_2019 = nextLine[164];
            String Population_2014 = nextLine[165];
            String Population_2016 = nextLine[166];
            String Population_2015 = nextLine[167];
            String Population_Growth_Rate = nextLine[168];
            String Locked__s = nextLine[169];
            String Last_Enriched_Time__s = nextLine[170];

            if(zpid!=null && !zpid.isEmpty() && HUD_Rent!=null && !HUD_Rent.isEmpty() && Cash_on_Cash!=null && !Cash_on_Cash.isEmpty()){
                JSONObject jsonObject = getZillowPropertyDetailsViaZpid(Integer.parseInt(zpid), Double.parseDouble(HUD_Rent), Double.parseDouble(Cash_on_Cash), zillowUrl, 0);
                if(jsonObject != null) {
                    JSONObject cData = jsonObject.getJSONObject("cData");
                    JSONObject cDataObject = new JSONObject();
                    //get the zillow property details and update the csv file with the specific details via zpid
                    if (cData != null) {
                        cDataObject.put("Last_Name", cData.optString("Last_Name"));
                        cDataObject.put("county", cData.optString("county"));
                        cDataObject.put("livingArea", cData.optInt("livingArea") != 0 ? cData.optInt("livingArea") : JSONObject.NULL);
                        cDataObject.put("Last_Sold_Price", cData.optInt("Last_Sold_Price") != 0 ? cData.optInt("Last_Sold_Price") : JSONObject.NULL);
                        cDataObject.put("State", cData.optString("State"));
                        cDataObject.put("timeOnZillow", cData.optString("timeOnZillow"));
                        cDataObject.put("Zillow_Rent_Estimate", cData.optInt("Zillow_Rent_Estimate") != 0 ? cData.optInt("Zillow_Rent_Estimate") : JSONObject.NULL);
                        cDataObject.put("zestimate", cData.optInt("zestimate") != 0 ? cData.optInt("zestimate") : JSONObject.NULL);
                        cDataObject.put("streetAddress", cData.optString("streetAddress"));
                        cDataObject.put("Country", cData.optString("Country"));
                        cDataObject.put("isListedByOwner", cData.optBoolean("isListedByOwner"));
                        cDataObject.put("homeStatus", cData.optString("homeStatus"));
                        cDataObject.put("mlsid", cData.optString("mlsid"));
                        cDataObject.put("Tax_Assessed_Value", cData.optString("Tax_Assessed_Value"));
                        cDataObject.put("datePostedString", cData.optString("datePostedString"));
                        cDataObject.put("listingTypeDimension", cData.optString("listingTypeDimension"));
                        cDataObject.put("restimateLowPercent", org.apache.commons.lang3.StringUtils.isNotBlank(cData.optString("restimateLowPercent")) ? Integer.parseInt(cData.optString("restimateLowPercent")) : null);
                        cDataObject.put("price", cData.optInt("price") != 0 ? cData.optInt("price") : JSONObject.NULL);
                        cDataObject.put("bathrooms", cData.optInt("bathrooms") != 0 ? cData.optInt("bathrooms") : JSONObject.NULL);
                        cDataObject.put("bedrooms", cData.optInt("bedrooms") != 0 ? cData.optInt("bedrooms") : JSONObject.NULL);
                        cDataObject.put("daysOnZillow", cData.optInt("daysOnZillow") != 0 ? cData.optInt("daysOnZillow") : JSONObject.NULL);
                        cDataObject.put("City", cData.optString("City"));
                        cDataObject.put("pageViewCount", cData.optInt("pageViewCount") != 0 ? cData.optInt("pageViewCount") : JSONObject.NULL);
                        cDataObject.put("taxAssessedYear", cData.optInt("taxAssessedYear") != 0 ? cData.optInt("taxAssessedYear") : JSONObject.NULL);
                        cDataObject.put("yearBuilt", cData.optInt("yearBuilt") != 0 ? cData.optInt("yearBuilt") : JSONObject.NULL);
                        cDataObject.put("zipcode", cData.optString("zipcode"));
                        cDataObject.put("homeType", cData.optString("homeType"));
                        cDataObject.put("lotSize", cData.optInt("lotSize") != 0 ? cData.optInt("lotSize") : JSONObject.NULL);
                        cDataObject.put("Property_Tax_Rate", cData.optDouble("Property_Tax_Rate") != Double.NaN ? cData.optDouble("Property_Tax_Rate") : null);
                        cDataObject.put("dateSoldString", cData.optString("dateSoldString"));
                        cDataObject.put("isRecentStatusChange", cData.optBoolean("isRecentStatusChange"));
                        cDataObject.put("zillowUrl", cData.optString("zillowUrl"));
                        cDataObject.put("Description", cData.optString("Description"));
                        cDataObject.put("isForeclosure", cData.optBoolean("isForeclosure"));
                        cDataObject.put("isForAuction", cData.optBoolean("isForAuction"));
                        cDataObject.put("isPending", cData.optBoolean("isPending"));
                        cDataObject.put("agentName", cData.optString("agentName"));
                        cDataObject.put("agentPhoneNumber", cData.optString("agentPhoneNumber"));
                        cDataObject.put("Email", cData.optString("Email"));
                        cDataObject.put("brokerName", cData.optString("brokerName"));
                        cDataObject.put("brokerPhoneNumber", cData.optString("brokerPhoneNumber"));
                        cDataObject.put("zpid", cData.optString("zpid"));
                        cDataObject.put("HUD_Rent", cData.optString("HUD_Rent"));
                        cDataObject.put("Cash_on_Cash", cData.optString("Cash_on_Cash"));
                        jsonArray.put(cDataObject);
                    }
                }
            }
        }
        System.out.println("jsonArray: " + jsonArray);

    }

    public JSONObject getZillowPropertyDetailsViaZpid(Integer zpid, Double hudFmr, Double CashOnCash, String detailUrl, int count) throws UnirestException {
        if (count < 100) {
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

                    return jsonObject;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
