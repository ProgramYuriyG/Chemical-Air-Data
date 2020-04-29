package webapplication;

import java.io.IOException;
import java.sql.SQLException;

public class MainRunner {

    public static void main(String[]args) throws IOException, InterruptedException, ClassNotFoundException, SQLException {
        //webapplication.DataCollector collector = new webapplication.DataCollector();
        //collector.collectPollutionData();

        webapplication.DatabaseManager manager = new webapplication.DatabaseManager();
        manager.getDatabaseConnection(true, true);
    }
}
