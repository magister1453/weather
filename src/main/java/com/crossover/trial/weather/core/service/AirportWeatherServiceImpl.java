package com.crossover.trial.weather.core.service;

import com.crossover.trial.weather.adapter.file.AirportWeatherFileRepository;
import com.crossover.trial.weather.core.model.Airport;
import com.crossover.trial.weather.core.model.DataPoint;
import com.crossover.trial.weather.core.model.DataPointType;
import com.crossover.trial.weather.core.repository.AirportWeatherRepository;

/**
 * Created by marc.marais on 2017/05/21.
 */
public class AirportWeatherServiceImpl implements AirportWeatherService{

    private AirportWeatherRepository airportWeatherRepository = AirportWeatherFileRepository.getInstance();

    @Override
    public void addDataPoint(String iataCode, String pointType, DataPoint dataPoint) {
        Airport airport = airportWeatherRepository.findAirportByIataCode(iataCode);
        airport.addWeather(DataPointType.valueOf(pointType.toUpperCase()), dataPoint);
        airportWeatherRepository.saveAirport(airport);
    }
}
