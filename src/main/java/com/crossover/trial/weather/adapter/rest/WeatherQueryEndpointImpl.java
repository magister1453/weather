package com.crossover.trial.weather.adapter.rest;

import com.crossover.trial.weather.adapter.file.AirportWeatherFileRepository;
import com.crossover.trial.weather.core.model.Airport;
import com.crossover.trial.weather.core.model.AtmosphericInformation;
import com.crossover.trial.weather.core.model.DataPoint;
import com.crossover.trial.weather.core.repository.AirportWeatherRepository;
import com.google.gson.Gson;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.logging.Logger;

/**
 * The Weather App REST endpoint allows clients to query, update and check health stats. Currently, all data is
 * held in memory. The end point deploys to a single container
 *
 * @author code test administrator
 */
@Path("/query")
public class WeatherQueryEndpointImpl implements WeatherQueryEndpoint {

    public final static Logger LOGGER = Logger.getLogger("WeatherQuery");

    /** earth radius in KM */
    private static final double R = 6372.8;

    /** shared gson json to object factory */
    private static final Gson gson = new Gson();

    /** all known airports */
    static List<Airport> airport = new ArrayList<>();

    /** atmospheric information for each airport, idx corresponds with airport */
    static List<AtmosphericInformation> atmosphericInformation = new LinkedList<>();

    private AirportWeatherRepository airportWeatherRepository = AirportWeatherFileRepository.getInstance();

    /**
     * Internal performance counter to better understand most requested information, this map can be improved but
     * for now provides the basis for future performance optimizations. Due to the stateless deployment architecture
     * we don't want to write this to disk, but will pull it off using a REST request and aggregate with other
     * performance metrics {@link #ping()}
     */
    public static Map<Airport, Integer> requestFrequency = new HashMap<Airport, Integer>();

    public static Map<Double, Integer> radiusFreq = new HashMap<Double, Integer>();

    /**
     * Retrieve service health including total size of valid data points and request frequency information.
     *
     * @return health stats for the service as a string
     */
    @Override
    public String ping() {
        Map<String, Object> retval = new HashMap<>();
        int datasize = 0;
        Map<String, Double> freq = new HashMap<>();
        for (Airport airport : airportWeatherRepository.findAirports()) {
            double frac = (double)requestFrequency.getOrDefault(airport, 0) / requestFrequency.size();
            datasize += airport.getWeather().values().size();
            freq.put(airport.getIata(), frac);
        }
        retval.put("iata_freq", freq);
        retval.put("datasize", datasize);

        int m = radiusFreq.keySet().stream()
                .max(Double::compare)
                .orElse(1000.0).intValue() + 1;

        int[] hist = new int[m];
        for (Map.Entry<Double, Integer> e : radiusFreq.entrySet()) {
            int i = e.getKey().intValue() % 10;
            hist[i] += e.getValue();
        }
        retval.put("radius_freq", hist);

        return gson.toJson(retval);
    }

    /**
     * Given a query in json format {'iata': CODE, 'radius': km} extracts the requested airport information and
     * return a list of matching atmosphere information.
     *
     * @param iata the iataCode
     * @param radiusString the radius in km
     *
     * @return a list of atmospheric information
     */
    @Override
    public Response weather(String iata, String radiusString) {
        double radius = radiusString == null || radiusString.trim().isEmpty() ? 0 : Double.valueOf(radiusString);
        updateRequestFrequency(iata, radius);

        List<AtmosphericInformation> retval = new ArrayList<>();
        if (radius == 0) {
            retval.add(airportWeatherRepository.findAirportByIataCode(iata).getWeather());
        } else {
            Airport ad = airportWeatherRepository.findAirportByIataCode(iata);
            for (Airport airport : airportWeatherRepository.findAirports()){
                if (calculateDistance(ad, airport) <= radius){
                    retval.add(airport.getWeather());
                }
            }
        }
        return Response.status(Response.Status.OK).entity(retval).build();
    }


    /**
     * Records information about how often requests are made
     *
     * @param iata an iata code
     * @param radius query radius
     */
    public void updateRequestFrequency(String iata, Double radius) {
        Airport airport = airportWeatherRepository.findAirportByIataCode(iata);
        requestFrequency.put(airport, requestFrequency.getOrDefault(airport, 0) + 1);
        radiusFreq.put(radius, radiusFreq.getOrDefault(radius, 0));
    }

    /**
     * Haversine distance between two airports.
     *
     * @param ad1 airport 1
     * @param ad2 airport 2
     * @return the distance in KM
     */
    public double calculateDistance(Airport ad1, Airport ad2) {
        double deltaLat = Math.toRadians(ad2.getLatitude() - ad1.getLatitude());
        double deltaLon = Math.toRadians(ad2.getLongitude() - ad1.getLongitude());
        double a =  Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2)
                * Math.cos(ad1.getLatitude()) * Math.cos(ad2.getLatitude());
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

}
