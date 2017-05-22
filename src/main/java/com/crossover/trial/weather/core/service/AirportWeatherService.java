package com.crossover.trial.weather.core.service;

import com.crossover.trial.weather.core.model.DataPoint;
import com.crossover.trial.weather.exception.WeatherException;

/**
 * Created by marc.marais on 2017/05/21.
 */
public interface AirportWeatherService {
    public void addDataPoint(String iataCode, String pointType, DataPoint dp) throws WeatherException;
}
