package com.rwardrup.sheiko;

/**
 * Created by rwardrup on 1/22/17.
 */

public class Utils {
    private Double value;
    // Round up number i to nearest v
    public Double roundDouble(Double i, Double v){
        this.value = Double.valueOf((Math.round(i / v) * v));
        return Double.valueOf((Math.round(i/v) * v));
    }
}
