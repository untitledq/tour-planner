package at.fhtw.swen.tourplanner.service;

import at.fhtw.swen.tourplanner.controller.response.RouteResponse;

public interface RouteServiceClient {
    RouteResponse calculateRoute(String start, String end, String tourType);
}
