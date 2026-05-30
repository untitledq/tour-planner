package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.persistence.entity.TourType;
import at.fhtw.swen.tourplanner.service.GeocodingServiceClient;
import at.fhtw.swen.tourplanner.service.RouteServiceClient;
import at.fhtw.swen.tourplanner.service.dto.CoordinateDto;
import at.fhtw.swen.tourplanner.service.dto.RouteDto;
import at.fhtw.swen.tourplanner.service.dto.ors.route.OrsRoute;
import at.fhtw.swen.tourplanner.service.dto.ors.route.OrsRouteRequest;
import at.fhtw.swen.tourplanner.service.dto.ors.route.OrsRouteResponse;
import at.fhtw.swen.tourplanner.service.mapper.OrsProfileMapper;
import at.fhtw.swen.tourplanner.util.LoggerService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class RouteServiceClientImpl implements RouteServiceClient {

    private final Logger logger =
            LoggerService.getInstance().getLogger(getClass());

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
    public RouteDto calculateRoute(
            String start,
            String end,
            String tourType
    ) {

        logger.info(
                "Calculating route: start='{}', end='{}', type='{}'",
                start, end, tourType
        );

        CoordinateDto startCoordinate =
                geocodingServiceClient.geocode(start);

        CoordinateDto endCoordinate =
                geocodingServiceClient.geocode(end);

        logger.info(
                "Start coordinates: longitude={}, latitude={}",
                startCoordinate.longitude(),
                startCoordinate.latitude()
        );

        logger.info(
                "End coordinates: longitude={}, latitude={}",
                endCoordinate.longitude(),
                endCoordinate.latitude()
        );

        String profile = mapTourTypeToProfile(tourType);

        logger.info("Using ORS profile '{}'", profile);

        List<List<Double>> coordinates = List.of(
                List.of(startCoordinate.longitude(), startCoordinate.latitude()),
                List.of(endCoordinate.longitude(), endCoordinate.latitude())
        );

        OrsRouteRequest request = new OrsRouteRequest(
                coordinates,
                false // instructions off for simplicity
        );

        OrsRouteResponse response = restClient.post()
                .uri("/" + profile)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(OrsRouteResponse.class);

        if (response == null
                || response.routes() == null
                || response.routes().isEmpty()) {
            logger.error("ORS returned no routes");
            throw new RuntimeException("No route found");
        }

        OrsRoute route = response.routes().getFirst();

        // decode polyline string → list of [lat, lng]
        List<List<Double>> leafletCoordinates =
                decodePolyline(route.geometry())
                        .stream()
                        .toList();

        return new RouteDto(
                (int) route.summary().distance(),
                (int) route.summary().duration(),
                leafletCoordinates
        );
    }

    private String mapTourTypeToProfile(String tourType) {

        if (tourType == null) {
            return "driving-car";
        }

        String normalizedTourType = tourType.trim();

        try {
            return OrsProfileMapper.toOrsProfile(
                    TourType.valueOf(normalizedTourType.toUpperCase())
            );
        } catch (IllegalArgumentException ignored) {
            logger.warn("Unknown tour type '{}', falling back to legacy profile mapping", tourType);
        }

        return switch (normalizedTourType.toLowerCase()) {
            case "walking" -> "foot-walking";
            case "bike" -> "cycling-regular";
            case "car" -> "driving-car";
            default -> "driving-car";
        };
    }

    private List<List<Double>> decodePolyline(String encoded) {

        List<List<Double>> coords = new ArrayList<>();

        int index = 0, lat = 0, lng = 0;

        while (index < encoded.length()) {

            int b, shift = 0, result = 0;

            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;

            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            coords.add(List.of(lat / 1e5d, lng / 1e5d));
        }

        return coords;
    }
}
