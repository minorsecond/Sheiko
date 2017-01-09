package com.rwardrup.sheiko;

/**
 * Created by rwardrup on 1/8/17.
 */

public class UserMaxEntry {

    private String units;
    private Double squatMax;
    private Double benchMax;
    private Double deadliftMax;
    private Double wilks;
    private String date;

    public UserMaxEntry() {
    }

    public UserMaxEntry(String units, Double squatMax, Double benchMax, Double deadliftMax,
                        Double wilks, String date) {
        super();
        this.units = units;
        this.squatMax = squatMax;
        this.benchMax = benchMax;
        this.deadliftMax = deadliftMax;
        this.wilks = wilks;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Max entry [units=" + units + ", squatMax=" + squatMax + ", benchMax=" + benchMax +
                ", deadliftMax=" + deadliftMax + ", wilks=" + wilks + ", date=" + date + "]";
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Double getSquatMax() {
        return squatMax;
    }

    public void setSquatMax(Double squatMax) {
        this.squatMax = squatMax;
    }

    public Double getBenchMax() {
        return benchMax;
    }

    public void setBenchMax(Double benchMax) {
        this.benchMax = benchMax;
    }

    public Double getDeadliftMax() {
        return deadliftMax;
    }

    public void setDeadliftMax(Double deadliftMax) {
        this.deadliftMax = deadliftMax;
    }

    public Double getWilks() {
        return wilks;
    }

    public void setWilks(Double wilks) {
        this.wilks = wilks;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
