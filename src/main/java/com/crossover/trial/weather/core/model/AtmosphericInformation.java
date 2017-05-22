package com.crossover.trial.weather.core.model;

import com.crossover.trial.weather.exception.WeatherException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * encapsulates sensor information for a particular location
 */
public class AtmosphericInformation {

    final Map<DataPointType, DataPoint> weather = new ConcurrentHashMap<DataPointType, DataPoint>();

    /** the last time this data was updated, in milliseconds since UTC epoch */
    private long lastUpdateTime;

    public AtmosphericInformation() {
        lastUpdateTime = System.currentTimeMillis();
    }

    public DataPoint update(DataPointType key, DataPoint value) {
        this.lastUpdateTime = System.currentTimeMillis();
        return weather.put(key, value);
    }

    public DataPoint get(DataPointType key) {
        return weather.get(key);
    }

    public List<DataPoint> values() {
        return weather.values().stream().collect(Collectors.toList());
    }

    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

}
