package com.crossover.trial.weather.core.util;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple airport loader which reads a file from disk and sends entries to the webservice
 *
 * TODO: Implement the Airport Loader
 * 
 * @author code test administrator
 */
public class AirportLoader {

    private WebTarget collectWebTarget;
    private static final String BASE_URI = "http://localhost:9090";

    public AirportLoader() {
        Client client = ClientBuilder.newClient();
        collectWebTarget = client.target(BASE_URI).path("collect");
    }

    private static final String SEPARATOR = ",";

    public void upload() throws IOException {
        BufferedReader br = null;
        try {
            URL url = AirportLoader.class
                    .getClassLoader().getResource("airports_1000.dat");
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            System.out.println("upload");
            br.lines().map(line -> Arrays.asList(line.split(SEPARATOR)))
                    .forEach(strings -> {

                        String path = "/airport/" + strings.get(4).replaceAll("\"", "") + "/" + strings.get(6) + "/" + strings.get(7);
                        collectWebTarget.path(path).request().post(Entity.entity("", MediaType.TEXT_HTML_TYPE));
                    });
        } finally {
            if (br != null) {
                br.close();
            }
        }

    }

    public static void main(String args[]) throws IOException, NullPointerException{

        AirportLoader loader = new AirportLoader();
        loader.upload();
        System.exit(0);
    }
}
