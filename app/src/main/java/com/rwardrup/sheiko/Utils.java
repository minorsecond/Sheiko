package com.rwardrup.sheiko;

/**
 * Created by rwardrup on 1/22/17.
 */

public class Utils {

    // Round up number i to nearest v
    public Double roundDouble(Double i, Double v){
        return Double.valueOf((Math.round(i/v) * v));
    }
}
