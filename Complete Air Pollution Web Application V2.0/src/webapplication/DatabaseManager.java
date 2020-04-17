package webapplication;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;

/*
 * This class uses jdbc to connect to Microsoft SQL Server Database
 *
 * To connect to the database through windows authentication, need to install the mssql-jdbc and put the
 * authorization into the jdk folder that the project uses
 */

public class DatabaseManager {

    public void getDatabaseConnection(boolean createTables, boolean insertTable) throws ClassNotFoundException {

        // Create a variable for the connection string.
        String connectionUrl ="jdbc:sqlserver://LAPTOP-SEB0OMKO;integratedSecurity=true;";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {

            System.out.println("Connected to the SQL Server Database!");

            if (createTables) {
                createDatabaseTables(stmt);
            }
            if (insertTable){
                insertTableValues(con, stmt);
            }
            /*
             * Date     : Date
             *       SELECT convert(datetime, '10/23/2016', 101) -- mm/dd/yyyy
             * Source   : VarChar(10)
             * Site ID  : INT(10)
             * POC      : INT(2)
             * Daily Max 8-hour CO Concentration    : DECIMAL(3, 1) Changes depending on what chemical
             * UNITS    : Changes depending on what chemical
             * DAILY_AQI_VALUE  : VarChar(3)    Changes depending on what chemical
             * Site Name        : VarChar(100)
             * DAILY_OBS_COUNT  : Int(2)
             * PERCENT_COMPLETE : Int(3)
             * AQS_PARAMETER_CODE   : VarChar(5)    /   Int(5)
             * AQS_PARAMETER_DESC   : VarChar(25)
             *
             * CBSA_CODE            : VarChar(5)    /   Int(5)
             * CBSA_NAME            : VarChar(75)
             * STATE_CODE           : Int(2)
             * STATE                : VarChar(20)
             * COUNTY_CODE          : VarChar(3)
             * COUNTY               : VarChar(50)
             * SITE_LATITUDE        : Decimal(3, 6)
             * SITE_LONGITUDE       : Decimal(3, 6)
             */
        }
        // Handle any errors that may have occurred.
        catch (SQLException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // method used to drop the tables of data if theyre already created
    public void dropDatabaseTables(Statement dbStatement) throws SQLException {
        String sqlQuery;

        System.out.println("Dropping Database Tables...");
        //create the database is it has not been created yet
        sqlQuery = "IF DB_ID('AirPollutionUnitedStates') IS NULL\n" +
                "BEGIN\n" +
                "\tCREATE DATABASE AirPollutionUnitedStates;\n" +
                "END";
        dbStatement.execute(sqlQuery);

        //drop the tables if they are already present in the database
        sqlQuery = "USE AirPollutionUnitedStates\n" +
                "\n" +
                "IF OBJECT_ID('PollutantCarbonMonoxide') IS NOT NULL\n" +
                "BEGIN\n" +
                "\tALTER TABLE PollutantCarbonMonoxide\n" +
                "\tDROP CONSTRAINT carbonMonoxide_fk_siteid\n" +
                "\tDROP TABLE PollutantCarbonMonoxide\n" +
                "END\n" +
                "\n" +
                "IF OBJECT_ID('PollutantLead') IS NOT NULL\n" +
                "BEGIN\n" +
                "\tALTER TABLE PollutantLead\n" +
                "\tDROP CONSTRAINT lead_fk_siteid\n" +
                "\tDROP TABLE PollutantLead\n" +
                "END\n" +
                "\n" +
                "IF OBJECT_ID('PollutantNitrogenDioxide') IS NOT NULL\n" +
                "BEGIN\n" +
                "\tALTER TABLE PollutantNitrogenDioxide\n" +
                "\tDROP CONSTRAINT nitrogenDioxide_fk_siteid\n" +
                "\tDROP TABLE PollutantNitrogenDioxide\n" +
                "END\n" +
                "\n" +
                "IF OBJECT_ID('PollutantOzone') IS NOT NULL\n" +
                "BEGIN\n" +
                "\tALTER TABLE PollutantOzone\n" +
                "\tDROP CONSTRAINT ozone_fk_siteid\n" +
                "\tDROP TABLE PollutantOzone\n" +
                "END\n" +
                "\n" +
                "IF OBJECT_ID('PollutantPM10') IS NOT NULL\n" +
                "BEGIN\n" +
                "\tALTER TABLE PollutantPM10\n" +
                "\tDROP CONSTRAINT pm10_fk_siteid\n" +
                "\tDROP TABLE PollutantPM10\n" +
                "END\n" +
                "\n" +
                "IF OBJECT_ID('PollutantPM2_5') IS NOT NULL\n" +
                "BEGIN\n" +
                "\tALTER TABLE PollutantPM2_5\n" +
                "\tDROP CONSTRAINT pm2_5_fk_siteid\n" +
                "\tDROP TABLE PollutantPM2_5\n" +
                "END\n" +
                "\n" +
                "IF OBJECT_ID('PollutantSulfurDioxide') IS NOT NULL\n" +
                "BEGIN\n" +
                "\tALTER TABLE PollutantSulfurDioxide\n" +
                "\tDROP CONSTRAINT sulfureDioxide_fk_siteid\n" +
                "\tDROP TABLE PollutantSulfurDioxide\n" +
                "END\n" +
                "\n" +
                "IF OBJECT_ID('MonitorSites') IS NOT NULL\n" +
                "BEGIN\n" +
                "\tDROP TABLE MonitorSites\n" +
                "END";
        dbStatement.execute(sqlQuery);
    }

    // method used to create the tables of data
    public void createDatabaseTables(Statement dbStatement) throws SQLException {
        //drops the database tables
        dropDatabaseTables(dbStatement);
        String sqlQuery = "";

        System.out.println("Creating Database Tables...");
        //create the master table which will hold all of the site information
        sqlQuery = "CREATE TABLE MonitorSites(\n" +
                "    Site_ID\t\t\tVarChar(10) NOT NULL,\n" +
                "    Site_Name\t\tVarChar(100),\n" +
                "    CBSA_CODE\t\tNUMERIC (5, 0) DEFAULT -1,\n" +
                "    CBSA_NAME\t\tVarChar(75) DEFAULT NULL,\n" +
                "    STATE_CODE\t\tNumeric(2,0) DEFAULT -1,\n" +
                "    [STATE]\t\t\tVarChar(20) DEFAULT NULL,\n" +
                "    COUNTY_CODE\t\tNumeric(3, 0) DEFAULT -1,\n" +
                "    COUNTY\t\t\tVarChar(50) DEFAULT NULL,\n" +
                "    SITE_LATITUDE\tDecimal(17,13) DEFAULT 999999,\n" +
                "    SITE_LONGITUDE\tDecimal(17, 13) DEFAULT 999999\n" +
                "    Constraint sites_pk_sideid Primary Key (Site_ID)  WITH (IGNORE_DUP_KEY = ON)"+
                ");";
        dbStatement.execute(sqlQuery);

        //create the carbon Monoxide table to hold the data
        sqlQuery = "CREATE TABLE PollutantCarbonMonoxide(\n" +
                "\tcarbonMonoxide_site_id\tVarChar(10) NOT NULL,\n" +
                "\t[Date]\t\tDate,\n" +
                "    [Source]\tVarChar(10),\n" +
                "    POC\t\t\tNUMERIC (2, 0),\n" +
                "    Daily_Max_8_hour_CO_Concentration\tDECIMAL(3, 1),\n" +
                "    UNITS\t\t\tchar(3),\n" +
                "    DAILY_AQI_VALUE  VarChar(3),\n" +
                "    DAILY_OBS_COUNT  VarChar(2),\n" +
                "    PERCENT_COMPLETE DECIMAL (7, 4),\n" +
                "    AQS_PARAMETER_CODE   VarChar(5),\n" +
                "    AQS_PARAMETER_DESC   VarChar(20),\n" +
                "    CONSTRAINT carbonMonoxide_fk_siteid FOREIGN KEY (carbonMonoxide_site_id) REFERENCES MonitorSites(Site_ID)" +
                ");";
        dbStatement.execute(sqlQuery);


        //create the lead table to hold the data
        sqlQuery = "CREATE TABLE PollutantLead(\n" +
                "\tlead_site_id\tVarChar(10) NOT NULL,\n" +
                "\t[Date]\t\tDate,\n" +
                "    [Source]\tVarChar(10),\n" +
                "    POC\t\t\tNUMERIC (2, 0),\n" +
                "    Daily_Mean_Pb_Concentration\tDECIMAL(5, 3),\n" +
                "    UNITS\t\t\tchar(8),\n" +
                "    DAILY_AQI_VALUE  VarChar(3),\n" +
                "    DAILY_OBS_COUNT  VarChar(2),\n" +
                "    PERCENT_COMPLETE DECIMAL (7, 4),\n" +
                "    AQS_PARAMETER_CODE   VarChar(5),\n" +
                "    AQS_PARAMETER_DESC   VarChar(15),\n" +
                "    CONSTRAINT lead_fk_siteid FOREIGN KEY (lead_site_id) REFERENCES MonitorSites(Site_ID)" +
                ");";
        dbStatement.execute(sqlQuery);

        //create the Nitrogen Dioxide table to hold the data
        sqlQuery = "CREATE TABLE PollutantNitrogenDioxide(\n" +
                "\tnitrogenDioxide_site_id\tVarChar(10) NOT NULL,\n" +
                "\t[Date]\t\tDate,\n" +
                "    [Source]\tVarChar(10),\n" +
                "    POC\t\t\tNUMERIC (2, 0),\n" +
                "    Daily_Max_1_hour_NO2_Concentration\tDECIMAL(4, 1),\n" +
                "    UNITS\t\t\tchar(3),\n" +
                "    DAILY_AQI_VALUE  VarChar(3),\n" +
                "    DAILY_OBS_COUNT  VarChar(2),\n" +
                "    PERCENT_COMPLETE DECIMAL (7, 4),\n" +
                "    AQS_PARAMETER_CODE   VarChar(5),\n" +
                "    AQS_PARAMETER_DESC   VarChar(25),\n" +
                "    CONSTRAINT nitrogenDioxide_fk_siteid FOREIGN KEY (nitrogenDioxide_site_id) REFERENCES MonitorSites(Site_ID)" +
                ");";
        dbStatement.execute(sqlQuery);

        //create the Ozone table to hold the data
        sqlQuery = "CREATE TABLE PollutantOzone(\n" +
                "\tozone_site_id\tVarChar(10) NOT NULL,\n" +
                "\t[Date]\t\tDate,\n" +
                "    [Source]\tVarChar(10),\n" +
                "    POC\t\t\tNUMERIC (2, 0),\n" +
                "    Daily_Max_8_hour_Ozone_Concentration\tDECIMAL(5, 3),\n" +
                "    UNITS\t\t\tchar(3),\n" +
                "    DAILY_AQI_VALUE  VarChar(3),\n" +
                "    DAILY_OBS_COUNT  VarChar(2),\n" +
                "    PERCENT_COMPLETE DECIMAL (7, 4),\n" +
                "    AQS_PARAMETER_CODE   VarChar(5),\n" +
                "    AQS_PARAMETER_DESC   VarChar(5),\n" +
                "    CONSTRAINT ozone_fk_siteid FOREIGN KEY (ozone_site_id) REFERENCES MonitorSites(Site_ID)" +
                ");";
        dbStatement.execute(sqlQuery);

        //create the PM10 table to hold the data
        sqlQuery = "CREATE TABLE PollutantPM10(\n" +
                "\tpm10_site_id\tVarChar(10) NOT NULL,\n" +
                "\t[Date]\t\tDate,\n" +
                "    [Source]\tVarChar(10),\n" +
                "    POC\t\t\tNUMERIC (2, 0),\n" +
                "    Daily_Mean_PM10_Concentration\tDECIMAL(4, 1),\n" +
                "    UNITS\t\t\tchar(8),\n" +
                "    DAILY_AQI_VALUE  VarChar(3),\n" +
                "    DAILY_OBS_COUNT  VarChar(2),\n" +
                "    PERCENT_COMPLETE DECIMAL (7, 4),\n" +
                "    AQS_PARAMETER_CODE   VarChar(5),\n" +
                "    AQS_PARAMETER_DESC   VarChar(25),\n" +
                "    CONSTRAINT pm10_fk_siteid FOREIGN KEY (pm10_site_id) REFERENCES MonitorSites(Site_ID)" +
                ");";
        dbStatement.execute(sqlQuery);

        //create the PM2.5 table to hold the data
        sqlQuery = "CREATE TABLE PollutantPM2_5(\n" +
                "\tpm2_5_site_id\tVarChar(10) NOT NULL,\n" +
                "\t[Date]\t\tDate,\n" +
                "    [Source]\tVarChar(10),\n" +
                "    POC\t\t\tNUMERIC (2, 0),\n" +
                "    Daily_Mean_PM2_5_Concentration\tDECIMAL(3, 1),\n" +
                "    UNITS\t\t\tchar(8),\n" +
                "    DAILY_AQI_VALUE  VarChar(3),\n" +
                "    DAILY_OBS_COUNT  VarChar(2),\n" +
                "    PERCENT_COMPLETE DECIMAL (7, 4),\n" +
                "    AQS_PARAMETER_CODE   VarChar(5),\n" +
                "    AQS_PARAMETER_DESC   VarChar(40),\n" +
                "    CONSTRAINT pm2_5_fk_siteid FOREIGN KEY (pm2_5_site_id) REFERENCES MonitorSites(Site_ID)" +
                ");";
        dbStatement.execute(sqlQuery);

        //create the Sulfur dioxide table to hold the data
        sqlQuery = "CREATE TABLE PollutantSulfurDioxide(\n" +
                "\tsulfurDioxide_site_id\tVarChar(10) NOT NULL,\n" +
                "\t[Date]\t\tDate,\n" +
                "    [Source]\tVarChar(10),\n" +
                "    POC\t\t\tNUMERIC (2, 0),\n" +
                "    Daily_Max_1_hour_SO2_Concentration\tDECIMAL(4, 1),\n" +
                "    UNITS\t\t\tchar(3),\n" +
                "    DAILY_AQI_VALUE  VarChar(3),\n" +
                "    DAILY_OBS_COUNT  VarChar(2),\n" +
                "    PERCENT_COMPLETE DECIMAL (7, 4),\n" +
                "    AQS_PARAMETER_CODE   VarChar(5),\n" +
                "    AQS_PARAMETER_DESC   VarChar(15),\n" +
                "    CONSTRAINT sulfureDioxide_fk_siteid FOREIGN KEY (sulfurDioxide_site_id) REFERENCES MonitorSites(Site_ID)" +
                ");";
        dbStatement.execute(sqlQuery);

    }

    // method used to insert data into the tables from the database folder located in the project
    public void insertTableValues(Connection dbCon, Statement dbStatement) throws SQLException, IOException, InterruptedException {
        File folder = new File("datasets/");
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        String concentrationName = "";
        String tableInsert = "";
        System.out.println("Inserting Values into Tables...");
        for (int fileIndex = 0; fileIndex< listOfFiles.length; fileIndex++) {
            if (listOfFiles[fileIndex].isFile()) {
                try (
                        Reader reader = Files.newBufferedReader(Paths.get(String.valueOf(listOfFiles[fileIndex])));
                        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                                .withHeader("Date", "Source", "Site ID", "POC", "", "UNITS", "DAILY_AQI_VALUE", "Site Name"
                                        , "DAILY_OBS_COUNT", "PERCENT_COMPLETE", "AQS_PARAMETER_CODE"
                                        , "AQS_PARAMETER_DESC", "CBSA_CODE", "CBSA_NAME", "STATE_CODE", "STATE", "COUNTY_CODE"
                                        , "COUNTY", "SITE_LATITUDE", "SITE_LONGITUDE")
                                .withIgnoreHeaderCase()
                                .withTrim());
                ) {
                    int count = 0;
                    for (CSVRecord csvRecord : csvParser) {
                        // Accessing Values by Column name
                        String date = csvRecord.get("Date");
                        String source = csvRecord.get("Source");
                        String siteId = csvRecord.get("Site ID");
                        String poc = csvRecord.get("POC");
                        String concentration = csvRecord.get(4);
                        String units = csvRecord.get("UNITS");
                        String aqiValue = csvRecord.get("DAILY_AQI_VALUE");
                        String siteName = csvRecord.get("Site Name");
                        String obsCount = csvRecord.get("DAILY_OBS_COUNT");
                        String percentComplete = csvRecord.get("PERCENT_COMPLETE");
                        String aqsParameterCode = csvRecord.get("AQS_PARAMETER_CODE");
                        String aqsParameterDesc = csvRecord.get("AQS_PARAMETER_DESC");
                        String cbsaCode = csvRecord.get("CBSA_CODE");
                        String cbsaName = csvRecord.get("CBSA_NAME");
                        String stateCode = csvRecord.get("STATE_CODE");
                        String state = csvRecord.get("STATE");
                        String countyCode = csvRecord.get("COUNTY_CODE");
                        String county = csvRecord.get("COUNTY");
                        String siteLatitude = csvRecord.get("SITE_LATITUDE");
                        String siteLongitude = csvRecord.get("SITE_LONGITUDE");

                        //fixes the values for insertion
                        //siteName = siteName.replaceAll(",", "\\\\,");

                        //Daily Max 8-hour CO Concentration
                        //Daily Mean Pb Concentration
                        //Daily Max 1-hour NO2 Concentration
                        //Daily Max 8-hour Ozone Concentration
                        //Daily Mean PM10 Concentration
                        //Daily Mean PM2.5 Concentration
                        //Daily Max 1-hour SO2 Concentration
                        if (count == 0) {
                            count++;
                            switch (concentration) {
                                case "Daily Max 8-hour CO Concentration":
                                    tableInsert = "PollutantCarbonMonoxide";
                                    concentrationName = "Carbon monoxide";
                                    break;
                                case "Daily Mean Pb Concentration":
                                    tableInsert = "PollutantLead";
                                    concentrationName = "Lead";
                                    break;
                                case "Daily Max 1-hour NO2 Concentration":
                                    tableInsert = "PollutantNitrogenDioxide";
                                    concentrationName = "Nitrogen dioxide";
                                    break;
                                case "Daily Max 8-hour Ozone Concentration":
                                    tableInsert = "PollutantOzone";
                                    concentrationName = "Ozone";
                                    break;
                                case "Daily Mean PM10 Concentration":
                                    tableInsert = "PollutantPM10";
                                    concentrationName = "Particulate matter <= 10μm in diameter.";
                                    break;
                                case "Daily Mean PM2.5 Concentration":
                                    tableInsert = "PollutantPM2_5";
                                    concentrationName = "Particulate matter <= 2.5μm in diameter.";
                                    break;
                                case "Daily Max 1-hour SO2 Concentration":
                                    tableInsert = "PollutantSulfurDioxide";
                                    concentrationName = "Sulfur dioxide";
                                    break;
                                default:
                                    concentrationName = "Name Not Found";
                                    break;
                            }
                        } else {
                            String sqlQuery = "";
                            //Insert into the Master Table
                            sqlQuery = "USE AirPollutionUnitedStates\n " +
                                    "INSERT INTO MonitorSites\n" +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
                                    /*"VALUES (" + siteId + ", " + siteName + ", " + cbsaCode + ", " + cbsaName
                                        + ", " + stateCode + ", " + state + ", " + countyCode + ", " + county
                                        + ", " + siteLatitude + ", " + siteLongitude + ");";*/
                            PreparedStatement preparedStatement = dbCon.prepareStatement(sqlQuery);
                            preparedStatement.setString(1, siteId);
                            preparedStatement.setString(2, siteName);
                            preparedStatement.setString(3, (cbsaCode.isEmpty())? null :cbsaCode);
                            preparedStatement.setString(4, cbsaName);
                            preparedStatement.setString(5, stateCode);
                            preparedStatement.setString(6, state);
                            preparedStatement.setString(7, countyCode);
                            preparedStatement.setString(8, county);
                            preparedStatement.setString(9, siteLatitude);
                            preparedStatement.setString(10, siteLongitude);
                            preparedStatement.execute();

                            //Insert into the respective pollutant table
                            sqlQuery = "USE AirPollutionUnitedStates\n " +
                                    "INSERT INTO "+tableInsert+"\n" +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
                                    /*"VALUES (" + siteId + ", " + date + ", " + source + ", " + poc
                                    + ", " + concentration + ", " + units + ", " + aqiValue + ", " + obsCount
                                    + ", " + percentComplete + ", " + aqsParameterCode + ", " + aqsParameterDesc + ");";*/
                            preparedStatement = dbCon.prepareStatement(sqlQuery);
                            preparedStatement.setString(1, siteId);
                            preparedStatement.setString(2, date);
                            preparedStatement.setString(3, source);
                            preparedStatement.setString(4, poc);
                            preparedStatement.setString(5, concentration);
                            preparedStatement.setString(6, units);
                            preparedStatement.setString(7, aqiValue);
                            preparedStatement.setString(8, obsCount);
                            preparedStatement.setString(9, percentComplete);
                            preparedStatement.setString(10, aqsParameterCode);
                            preparedStatement.setString(11, aqsParameterDesc);
                            preparedStatement.execute();
                        }

                    }
                }catch (Exception e){

                    System.out.println("Error From File: " + String.valueOf(listOfFiles[fileIndex]));
                    e.printStackTrace();
                    System.out.println();
                    Thread.sleep(500);
                    //System.out.println("Error Inserting data into " + tableInsert);
                }
            }
        }
    }

    //method used to query information
    public ArrayList queryDatabase(String pollutant, String searchState, String dateStart, String dateEnd) throws ClassNotFoundException {
        // Create a variable for the connection string.
        String connectionUrl ="jdbc:sqlserver://LAPTOP-SEB0OMKO;integratedSecurity=true;";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
            /*
            <option value="carbonMonoxide">Carbon Monoxide</option>
                <option value="lead">Lead</option>
                <option value="nitrogenDioxide">Nitrogen Dioxide</option>
                <option value="ozone">Ozone</option>
                <option value="sulfurDioxide">Sulfur Dioxide</option>
                <option value="pm10">PM10</option>
                <option value="pm2_5">PM2.5</option>
             */
            String sqlQuery = "USE AirPollutionUnitedStates\n" + "\n";
            String pollutantSearchValue = "";
            String pollutantUnits = "";
            switch(pollutant){
                case "carbonMonoxide":
                    pollutantUnits = "ppm";
                    pollutantSearchValue = "Daily Max 8 hour CO Concentration";
                    sqlQuery += "SELECT P.Daily_Max_8_hour_CO_Concentration\n" +
                        "FROM PollutantCarbonMonoxide P\n" +
                        "LEFT JOIN MonitorSites M\n" +
                        "ON P.carbonMonoxide_site_id = M.Site_ID\n";
                    break;
                case "lead":
                    pollutantUnits = "ug/m3 SC";
                    pollutantSearchValue = "Daily Mean Pb Concentration";
                    sqlQuery += "SELECT P.Daily_Mean_Pb_Concentration\n" +
                            "FROM PollutantLead P\n" +
                            "LEFT JOIN MonitorSites M\n" +
                            "ON P.lead_site_id = M.Site_ID\n";
                    break;
                case "nitrogenDioxide":
                    pollutantUnits = "ppb";
                    pollutantSearchValue = "Daily Max 1 hour NO2 Concentration";
                    sqlQuery += "SELECT P.Daily_Max_1_hour_NO2_Concentration\n" +
                            "FROM PollutantNitrogenDioxide P\n" +
                            "LEFT JOIN MonitorSites M\n" +
                            "ON P.nitrogenDioxide_site_id = M.Site_ID\n";
                    break;
                case "ozone":
                    pollutantUnits = "ppm";
                    pollutantSearchValue = "Daily Max 8 hour Ozone Concentration";
                    sqlQuery += "SELECT P.Daily_Max_8_hour_Ozone_Concentration\n" +
                            "FROM PollutantOzone P\n" +
                            "LEFT JOIN MonitorSites M\n" +
                            "ON P.ozone_site_id = M.Site_ID\n";
                    break;
                case "sulfurDioxide":
                    pollutantUnits = "ppb";
                    pollutantSearchValue = "Daily Max 1 hour SO2 Concentration";
                    sqlQuery += "SELECT P.Daily_Max_1_hour_SO2_Concentration\n" +
                            "FROM PollutantSulfurDioxide P\n" +
                            "LEFT JOIN MonitorSites M\n" +
                            "ON P.sulfurDioxide_site_id = M.Site_ID\n";
                    break;
                case "pm10":
                    pollutantUnits = "ug/m3 SC";
                    pollutantSearchValue = "Daily Mean PM10 Concentration";
                    sqlQuery += "SELECT P.Daily_Mean_PM10_Concentration\n" +
                            "FROM PollutantPM10 P\n" +
                            "LEFT JOIN MonitorSites M\n" +
                            "ON P.pm10_site_id = M.Site_ID\n";
                    break;
                case "pm2_5":
                    pollutantUnits = "ug/m3 LC";
                    pollutantSearchValue = "Daily Mean PM 2.5 Concentration";
                    sqlQuery += "SELECT P.Daily_Mean_PM2_5_Concentration\n" +
                            "FROM PollutantPM2_5 P\n" +
                            "LEFT JOIN MonitorSites M\n" +
                            "ON P.pm2_5_site_id = M.Site_ID\n";
                    break;

            }
            //Insert into the Master Table
            sqlQuery +=
                    "WHERE M.STATE='"+searchState+"' AND P.Date between '"+dateStart+"' and '"+dateEnd+"'\n";
            PreparedStatement preparedStatement = con.prepareStatement(sqlQuery);
            ResultSet rs = preparedStatement.executeQuery();

            ArrayList list = new ArrayList();
            list.add(pollutantSearchValue);
            list.add(pollutantUnits);
            while(rs.next()){
                list.add(rs.getString(1));
            }
            rs.close();
            return list;
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
