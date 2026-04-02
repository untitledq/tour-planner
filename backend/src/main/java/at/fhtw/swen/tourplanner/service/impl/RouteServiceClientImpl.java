package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.controller.response.RouteResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteServiceClientImpl {

    public RouteResponse calculateRoute(String start, String end, String tourType) {

        // TODO: replace hard-coded demo route with calculation
        // OpenRouteservice.org

        List<List<Double>> coords = List.of(
                List.of(48.2082, 16.3738), // Stephansplatz
                List.of(48.2065, 16.3500), // Naschmarkt
                List.of(48.1845, 16.3122)  // Schloss Schönbrunn
        );

        return new RouteResponse(
                5200,   // meters
                900,    // seconds
                coords
        );
    }
}