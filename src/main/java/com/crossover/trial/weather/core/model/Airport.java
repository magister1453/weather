package com.crossover.trial.weather.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.*;

/**
 * Basic airport information.
 *
 * @author code test administrator
 */
public class Airport {

    /** the three letter IATA code */
    private String iata;

    /** latitude value in degrees */
    private double latitude;

    /** longitude value in degrees */
    private double longitude;

    @JsonIgnore
    private AirportDetails airportDetails;

    private AtmosphericInformation weather = new AtmosphericInformation();

    public Airport() { }

    public Airport(String iata, double latitude, double longitude) {
        this.iata = iata;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public AirportDetails getAirportDetails() {
        return airportDetails;
    }

    public void setAirportDetails(AirportDetails airportDetails) {
        this.airportDetails = airportDetails;
    }

    public AtmosphericInformation getWeather() {
        return weather;
    }

    public void addWeather(DataPointType dataPointType, DataPoint dataPoint) {
        this.weather.update(dataPointType, dataPoint);
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    public boolean equals(Object other) {
        return other instanceof Airport && ((Airport) other).getIata().equals(this.getIata());

    }
}
