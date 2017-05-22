package com.crossover.trial.weather.adapter.rest;

import com.crossover.trial.weather.adapter.file.AirportWeatherFileRepository;
import com.crossover.trial.weather.core.model.Airport;
import com.crossover.trial.weather.core.model.AtmosphericInformation;
import com.crossover.trial.weather.core.model.DataPoint;
import com.crossover.trial.weather.core.model.DataPointType;
import com.crossover.trial.weather.core.repository.AirportWeatherRepository;
import com.crossover.trial.weather.core.service.AirportWeatherService;
import com.crossover.trial.weather.core.service.AirportWeatherServiceImpl;
import com.crossover.trial.weather.exception.WeatherException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class WeatherCollectorEndpointImpl implements WeatherCollectorEndpoint {
    public final static Logger LOGGER = Logger.getLogger(WeatherCollectorEndpointImpl.class.getName());

    /** shared gson json to object factory */
    public final static Gson gson = new Gson();

    private AirportWeatherRepository airportWeatherRepository = AirportWeatherFileRepository.getInstance();
    private AirportWeatherService airportWeatherService = new AirportWeatherServiceImpl();

    @Override
    public Response ping() {
        return Response.status(Response.Status.OK).entity("ready").build();
    }

    @Override
    public Response updateWeather(@PathParam("iata") String iataCode,
                                  @PathParam("pointType") String pointType,
                                  String datapointJson) {
        try {
            airportWeatherService.addDataPoint(iataCode, pointType.toUpperCase(), gson.fromJson(datapointJson, DataPoint.class));
        } catch (JsonSyntaxException | WeatherException | NoSuchElementException e) {
            LOGGER.log(Level.INFO, e.getMessage(), e);
            return Response.status(422).build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.OK).build();
    }


    @Override
    public Response getAirports() {
        Set<String> retval = new HashSet<>();
        retval = airportWeatherRepository.findAirports().stream().map(Airport::getIata).collect(Collectors.toSet());
        return Response.status(Response.Status.OK).entity(retval).build();
    }


    @Override
    public Response getAirport(@PathParam("iata") String iata) {
        Airport ad = airportWeatherRepository.findAirportByIataCode(iata);
        if(ad != null)
            return Response.status(Response.Status.OK).entity(ad).build();
        else
            return Response.status(Response.Status.NOT_FOUND).build();
    }


    @Override
    public Response addAirport(@PathParam("iata") String iata,
                               @PathParam("lat") String latString,
                               @PathParam("long") String longString) {
        airportWeatherRepository.saveAirport(new Airport(iata, Double.valueOf(latString), Double.valueOf(longString)));
        return Response.status(Response.Status.OK).build();
    }


    @Override
    public Response deleteAirport(@PathParam("iata") String iata) {
        if (airportWeatherRepository.findAirportByIataCode(iata) == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        else {
            airportWeatherRepository.deleteAirport(iata);
            return Response.status(Response.Status.OK).build();
        }
    }

    @Override
    public Response exit() {
        System.exit(0);
        return Response.noContent().build();
    }
}
