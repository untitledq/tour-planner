package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.service.GeocodingServiceClient;
import at.fhtw.swen.tourplanner.service.RouteServiceClient;
import at.fhtw.swen.tourplanner.service.dto.CoordinateDto;
import at.fhtw.swen.tourplanner.service.dto.OrsRoute;
import at.fhtw.swen.tourplanner.service.dto.OrsRouteRequest;
import at.fhtw.swen.tourplanner.service.dto.OrsRouteResponse;
import at.fhtw.swen.tourplanner.service.dto.RouteDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class RouteServiceClientImpl implements RouteServiceClient {

    private final RestClient restClient;

    private final GeocodingServiceClient geocodingServiceClient;

    public RouteServiceClientImpl(
            @Value("${ors.api.key}") String apiKey,
            @Value("${ors.api.directions-url}") String apiUrl,
            GeocodingServiceClient geocodingServiceClient
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", apiKey)
                .build();

        this.geocodingServiceClient = geocodingServiceClient;
    }

    @Override
    public RouteDto calculateRoute(String start, String end, String tourType) {

        CoordinateDto startCoordinate =
                geocodingServiceClient.geocode(start);

        CoordinateDto endCoordinate =
                geocodingServiceClient.geocode(end);

        // ORS expects coordinates in format:
        // [longitude, latitude]

        List<List<Double>> coordinates = List.of(
                List.of(
                        startCoordinate.longitude(),
                        startCoordinate.latitude()
                ),
                List.of(
                        endCoordinate.longitude(),
                        endCoordinate.latitude()
                )
        );

        String profile = mapTourTypeToProfile(tourType);

        OrsRouteRequest request = new OrsRouteRequest(coordinates);

        OrsRouteResponse response = restClient.post()
                .uri("/" + profile)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(OrsRouteResponse.class);

        if (response == null
                || response.routes() == null
                || response.routes().isEmpty()) {
            throw new RuntimeException("No route found");
        }

        OrsRoute route = response.routes().getFirst();

        return new RouteDto(
                (int) route.summary().distance(),
                (int) route.summary().duration(),
                coordinates
        );
    }

    private String mapTourTypeToProfile(String tourType) {

        if (tourType == null) {
            return "driving-car";
        }

        return switch (tourType.toLowerCase()) {
            case "walking" -> "foot-walking";
            case "bike" -> "cycling-regular";
            case "car" -> "driving-car";
            default -> "driving-car";
        };
    }
}