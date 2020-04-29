package webapplication;

import webapplication.resources.DataManipulation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;

public class HeatMapServlet extends javax.servlet.http.HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/html_files/state_heatmap.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pollutant = request.getParameter("pollutant-select");
        String dateStart = request.getParameter("date-start");
        String dateEnd = request.getParameter("date-end");
        String color = request.getParameter("color");
        if(pollutant != null){
            String pollutantText = "";
            switch(pollutant){
                case "carbonMonoxide":
                    pollutantText = "Carbon Monoxide";
                    break;
                case "lead":
                    pollutantText = "Lead";
                    break;
                case "nitrogenDioxide":
                    pollutantText = "Nitrogen Dioxide";
                    break;
                case "ozone":
                    pollutantText = "Ozone";
                    break;
                case "sulfurDioxide":
                    pollutantText = "Sulfur Dioxide";
                    break;
                case "pm10":
                    pollutantText = "PM10";
                    break;
                case "pm2_5":
                    pollutantText = "PM2.5";
                    break;
            }
            request.getSession().setAttribute("heatmapPollutant", pollutantText);
            request.getSession().setAttribute("heatmapStartDate", dateStart);
            request.getSession().setAttribute("heatmapEndDate", dateEnd);

             /*
            Blue:   #a5c1df #7fa8d1 #5a8ec4 #3e75ad #2c537b #1a3149
            Orange: #f7c4bd #f08f80 #ea6653 #e53e25 #c02d17 #741b0e
            Green:  #83d99e #42c56b #2a8847 #2e954e #1a542c #123a1f
            */
            if(color.equals("Blue")){
                request.getSession().setAttribute("heatmapColor1", "#a5c1df");
                request.getSession().setAttribute("heatmapColor2", "#7fa8d1");
                request.getSession().setAttribute("heatmapColor3", "#5a8ec4");
                request.getSession().setAttribute("heatmapColor4", "#3e75ad");
                request.getSession().setAttribute("heatmapColor5", "#2c537b");
                request.getSession().setAttribute("heatmapColor6", "#1a3149");
            }else if(color.equals("Orange")){
                request.getSession().setAttribute("heatmapColor1", "#f7c4bd");
                request.getSession().setAttribute("heatmapColor2", "#f08f80");
                request.getSession().setAttribute("heatmapColor3", "#ea6653");
                request.getSession().setAttribute("heatmapColor4", "#e53e25");
                request.getSession().setAttribute("heatmapColor5", "#c02d17");
                request.getSession().setAttribute("heatmapColor6", "#741b0e");
            }else{ //Green
                request.getSession().setAttribute("heatmapColor1", "#83d99e");
                request.getSession().setAttribute("heatmapColor2", "#42c56b");
                request.getSession().setAttribute("heatmapColor3", "#2a8847");
                request.getSession().setAttribute("heatmapColor4", "#2e954e");
                request.getSession().setAttribute("heatmapColor5", "#1a542c");
                request.getSession().setAttribute("heatmapColor6", "#123a1f");
            }

            DatabaseManager dbManager = new DatabaseManager();
            DataManipulation manipulation = new DataManipulation();
            try {
                ArrayList<String> nameList = new ArrayList<String>();
                File file = new File(getServletContext().getRealPath("/")+"/stateList.txt");

                BufferedReader br = new BufferedReader(new FileReader(file));
                String state;
                while ((state = br.readLine()) != null){
                    nameList.add(state);
                }

                Dictionary totalStateInformation = dbManager.queryDatabaseForHeatmap(pollutant, nameList, dateStart, dateEnd);
                ArrayList stateAverages = new ArrayList();
                // elements() method :
                String pollutantSearch = "";
                String pollutantUnits = "";
                for (int i = 0; i < nameList.size(); i++){
                    ArrayList concentrationList =  (ArrayList)totalStateInformation.get(nameList.get(i));
                    pollutantSearch = (String)concentrationList.remove(0);
                    pollutantUnits = (String)concentrationList.remove(0);
                    double average = manipulation.returnAverage(concentrationList);
                    stateAverages.add(average);
                }

                double pollutantMin = 99999;
                double pollutantMax = 0;
                for (int valueCount = 0; valueCount < stateAverages.size(); valueCount++)
                {
                    double pollutantValue = (double)stateAverages.get(valueCount);
                    if(pollutantValue > pollutantMax){
                        pollutantMax = pollutantValue;
                    }
                    if(pollutantValue < pollutantMin){
                        pollutantMin = pollutantValue;
                    }
                }

                String nameListString  = "[";
                for (int i = 0; i < nameList.size(); i++){
                    nameList.set(i, nameList.get(i).replace(" ", ""));
                    nameListString += "\"" + nameList.get(i) +"\", ";
                }
                nameListString = nameListString.substring(0, nameListString.length() - 2);
                nameListString += "]";

                double sixthValue = (pollutantMax-pollutantMin)/6;
                double range1 = truncateDecimal(pollutantMin + (sixthValue));
                double range2 = truncateDecimal(pollutantMin + (sixthValue * 2));
                double range3 = truncateDecimal(pollutantMin + (sixthValue * 3));
                double range4 = truncateDecimal(pollutantMin + (sixthValue * 4));
                double range5 = truncateDecimal(pollutantMin + (sixthValue * 5));
                pollutantMin = truncateDecimal(pollutantMin);
                pollutantMax = truncateDecimal(pollutantMax);

                request.getSession().setAttribute("heatmapPollutantSearch", pollutantSearch);
                request.getSession().setAttribute("heatmapPollutantUnits", pollutantUnits);
                request.getSession().setAttribute("heatmapAverageValues", stateAverages);
                request.getSession().setAttribute("heatmapAverageStateNames", nameListString);
                request.getSession().setAttribute("heatmapMax", pollutantMax);
                request.getSession().setAttribute("heatmapMin", pollutantMin);
                request.getSession().setAttribute("range1", range1);
                request.getSession().setAttribute("range2", range2);
                request.getSession().setAttribute("range3", range3);
                request.getSession().setAttribute("range4", range4);
                request.getSession().setAttribute("range5", range5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }else{
            request.getSession().invalidate();
        }
        doGet(request, response);
    }

    private double truncateDecimal(double decimal){
        decimal = decimal * 100;
        decimal = (int)decimal;
        decimal = (double)decimal/100.0;
        return decimal;
    }
}
