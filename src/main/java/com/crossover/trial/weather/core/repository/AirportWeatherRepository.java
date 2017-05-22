package com.crossover.trial.weather.core.repository;

import com.crossover.trial.weather.core.model.Airport;

import java.util.List;

/**
 * Created by marc.marais on 2017/05/21.
 */
public interface AirportWeatherRepository {
    public Airport findAirportByIataCode(String iataCode);
    public List<Airport> findAirports();
    public void saveAirport(Airport airport);
    public void deleteAirport(String iataCode);
    public int count();
}
