package at.fhtw.swen.tourplanner.service;

import at.fhtw.swen.tourplanner.service.dto.CoordinateDto;

public interface GeocodingServiceClient {

    CoordinateDto geocode(String location);
}