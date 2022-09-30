package com.whiteblink.PropertiesSync;

import java.io.*;

public class CSVFilter {
    //isUserConfirmedClaim,hudFmr,statusType,listingType,availabilityDate,isBuilding,pgapt,price,badgeInfo,communityName,state,has3DModel,priceLabel,minArea,canSaveBuilding,area,plid,unitCount,isNewYorkState,zipcode,isPropertyResultCDP,isCdpResult,isHomeRec,CashOnCash,info1String,style,detailUrl,beds,homeType,brokerName,isFavorite,city,hasImage,"hasAdditionalAttributions",sgapt,homeStatus,countyName,hasVideo,address,minBeds,zpid,isUserClaimingOwner,lotId,info3String,buildingId,baths,lotAreaString,statusText,visited,streetViewURL,variableData,isFeaturedListing,imgSrc,minBaths,streetViewMetadataURL
    // false,1298,FOR_SALE,,,,ForSale,79000,,,PA,false,"$79K",,,784,,,,19144,,,false,40.96982278481012,,,"https://www.zillow.com/homedetails/5028-Tacoma-St-Philadelphia-PA-19144/10240100_zpid/",2,TOWNHOUSE,,false,Philadelphia,true,false,"For Sale by Agent",FOR_SALE,"Philadelphia County",false,--,,10240100,false,,,,1,,"Townhouse for sale",false,,,false,"https://photos.zillowstatic.com/fp/239d8e9a31697ec8d1e44d9587b43c6e-p_e.jpg",,
    public void finalCSVGeneratorFilteredCSV() throws IOException {
        //Read CSV file
        String csvFile = "sourceproperties.csv";
        String line = "";
        String cvsSplitBy = ",";
        PrintWriter pw=new PrintWriter(new FileWriter("filteredproperties.csv"));
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            pw.println(br.readLine());
            while ((line = br.readLine()) != null) {
                // use comma as sep
                String isUserConfirmedClaim = line.split(cvsSplitBy)[0];
                String hudFmr = line.split(cvsSplitBy)[1];
                String statusType = line.split(cvsSplitBy)[2];
                String listingType = line.split(cvsSplitBy)[3];
                String availabilityDate = line.split(cvsSplitBy)[4];
                String isBuilding = line.split(cvsSplitBy)[5];
                String pgapt = line.split(cvsSplitBy)[6];
                String price = line.split(cvsSplitBy)[7];
                String badgeInfo = line.split(cvsSplitBy)[8];
                String communityName = line.split(cvsSplitBy)[9];
                String state = line.split(cvsSplitBy)[10];
                String has3DModel = line.split(cvsSplitBy)[11];
                String priceLabel = line.split(cvsSplitBy)[12];
                String minArea = line.split(cvsSplitBy)[13];
                String canSaveBuilding = line.split(cvsSplitBy)[14];
                String area = line.split(cvsSplitBy)[15];
                String plid = line.split(cvsSplitBy)[16];
                String unitCount = line.split(cvsSplitBy)[17];
                String isNewYorkState = line.split(cvsSplitBy)[18];
                String zipcode = line.split(cvsSplitBy)[19];
                String isPropertyResultCDP = line.split(cvsSplitBy)[20];
                String isCdpResult = line.split(cvsSplitBy)[21];
                String isHomeRec = line.split(cvsSplitBy)[22];
                String CashOnCash = line.split(cvsSplitBy)[23];
                String info1String = line.split(cvsSplitBy)[24];
                String style = line.split(cvsSplitBy)[25];
                String detailUrl = line.split(cvsSplitBy)[26];
                String beds = line.split(cvsSplitBy)[27];
                String homeType = line.split(cvsSplitBy)[28];
                String brokerName = line.split(cvsSplitBy)[29];
                String isFavorite = line.split(cvsSplitBy)[30];
                String city = line.split(cvsSplitBy)[31];
                String hasImage = line.split(cvsSplitBy)[32];
                String hasAdditionalAttributions = line.split(cvsSplitBy)[33];
                String sgapt = line.split(cvsSplitBy)[34];
                String homeStatus = line.split(cvsSplitBy)[35];
                String countyName = line.split(cvsSplitBy)[36];
                String hasVideo = line.split(cvsSplitBy)[37];
                String address = line.split(cvsSplitBy)[38];
                String minBeds = line.split(cvsSplitBy)[39];
                String zpid = line.split(cvsSplitBy)[40];
                String isUserClaimingOwner = line.split(cvsSplitBy)[41];
                String lotId = line.split(cvsSplitBy)[42];
                String info3String = line.split(cvsSplitBy)[43];
                String buildingId = line.split(cvsSplitBy)[44];
                String baths = line.split(cvsSplitBy)[45];
                String lotAreaString = line.split(cvsSplitBy)[46];
                String statusText = line.split(cvsSplitBy)[47];
                String visited = line.split(cvsSplitBy)[48];
                String streetViewURL = line.split(cvsSplitBy)[49];
                String variableData = line.split(cvsSplitBy)[50];
                String isFeaturedListing = line.split(cvsSplitBy)[51];
                String imgSrc = line.split(cvsSplitBy)[52];

                if(state.toLowerCase().equals("fl")&&(brokerName.toLowerCase().contains("rhp")||brokerName.toLowerCase().contains("treehouse"))){
                    continue;
                }

                if(statusType.toLowerCase().equals("for_sale")  && (homeType.toLowerCase().equals("SINGLE_FAMILY".toLowerCase()))){
                    pw.println(line);
                    System.out.println(line);
                    pw.flush();
                }

            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
