package webapplication;

import webapplication.resources.DataManipulation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class StateServlet extends javax.servlet.http.HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/html_files/state_map.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String state = request.getParameter("state-value");
        String stateId = request.getParameter("state-id");
        if(state == null){
            state = (String)request.getSession().getAttribute("stateName");
            stateId = (String)request.getSession().getAttribute("stateId");
        }else{
            request.getSession().invalidate();
        }
        String pollutant = request.getParameter("pollutant-select");
        String dateStart = request.getParameter("date-start");
        String dateEnd = request.getParameter("date-end");
        request.getSession().setAttribute("stateName", state);
        request.getSession().setAttribute("stateId", stateId);
        if(pollutant != null) {
            request.getSession().setAttribute("startDate", dateStart);
            request.getSession().setAttribute("endDate", dateEnd);
            DatabaseManager dbManager = new DatabaseManager();
            DataManipulation manipulation = new DataManipulation();
            try {
                ArrayList values = dbManager.queryDatabaseForAverage(pollutant, state, dateStart, dateEnd);
                request.getSession().setAttribute("pollutantSearch", values.remove(0));
                request.getSession().setAttribute("pollutantUnits", values.remove(0));
                if(!values.isEmpty()){
                    DecimalFormat df = new DecimalFormat("#.#####");
                    df.setRoundingMode(RoundingMode.CEILING);
                    double average = manipulation.returnAverage(values);
                    double min = manipulation.returnMin(values);
                    double max = manipulation.returnMax(values);
                    request.getSession().setAttribute("stateAverage", df.format(average));
                    request.getSession().setAttribute("stateMax", df.format(max));
                    request.getSession().setAttribute("stateMin", df.format(min));
                }else{
                    request.getSession().setAttribute("stateAverage", "empty");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        doGet(request, response);
    }
}
