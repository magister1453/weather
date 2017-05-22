package com.crossover.trial.weather.adapter.file;

import com.crossover.trial.weather.core.model.Airport;
import com.crossover.trial.weather.core.repository.AirportWeatherRepository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by marc.marais on 2017/05/21.
 */
public class AirportWeatherFileRepository implements AirportWeatherRepository{
    final ConcurrentHashMap<String, Airport> airports = new ConcurrentHashMap<>();
    private static AirportWeatherFileRepository instance = null;

    private AirportWeatherFileRepository(){}

    public static AirportWeatherFileRepository getInstance(){
        if(instance == null)
            instance = new AirportWeatherFileRepository();
        return instance;
    }

    @Override
    public Airport findAirportByIataCode(String iataCode) {
        return this.airports.getOrDefault(iataCode, null);
    }

    @Override
    public List<Airport> findAirports() {
        return this.airports.values().stream().collect(Collectors.toList());
    }

    public void saveAirport(Airport airport){
        this.airports.put(airport.getIata(), airport);
    }

    @Override
    public void deleteAirport(String iataCode) {
        this.airports.remove(iataCode);
    }

    @Override
    public int count() {
        return airports.values().size();
    }
}
