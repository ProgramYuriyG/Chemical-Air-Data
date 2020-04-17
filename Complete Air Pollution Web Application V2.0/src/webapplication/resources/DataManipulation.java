package webapplication.resources;

import java.util.ArrayList;

public class DataManipulation {

    //method to return the average value
    public double returnAverage(ArrayList list){
        double value = 0;
        for(int i=0; i<list.size();i++){
            value += Double.parseDouble((String)list.get(i));
        }
        value = value/list.size();
        return value;
    }

    //method to return the max value
    public double returnMax(ArrayList list){
        double value = 0;
        for(int i=0; i<list.size();i++){
            double listValue = Double.parseDouble((String)list.get(i));
            if(value < listValue){
                value = listValue;
            }
        }
        return value;
    }

    //method to return the min value
    public double returnMin(ArrayList list){
        double value = 999;
        for(int i=0; i<list.size();i++){
            double listValue = Double.parseDouble((String)list.get(i));
            if(value > listValue){
                value = listValue;
            }
        }
        if(value < 0){
            value = 0;
        }
        return value;
    }
}
