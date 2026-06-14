package at.fhtw.swen.tourplanner;

import at.fhtw.swen.tourplanner.controller.exception.GeocodingException;
import at.fhtw.swen.tourplanner.service.GeocodingServiceClient;
import at.fhtw.swen.tourplanner.service.RouteServiceClient;
import at.fhtw.swen.tourplanner.service.dto.CoordinateDto;
import at.fhtw.swen.tourplanner.service.dto.RouteDto;
import at.fhtw.swen.tourplanner.util.LoggerService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrsClientTests {

    private final Logger logger =
            LoggerService.getInstance().getLogger(getClass());

    @Autowired
    private GeocodingServiceClient geocodingServiceClient;

    @Autowired
    private RouteServiceClient routeServiceClient;

    @Value("${DB_PORT:NOT_FOUND}")
    private String dbPort;

    @Value("${DB_NAME:NOT_FOUND}")
    private String dbName;

    @Value("${ORS_API_KEY:NOT_FOUND}")
    private String orsApiKey;

    @Test
    void envVariables_shouldBeLoaded() {

        logger.info("Checking environment variable loading");

        logger.info("DB_PORT={}", dbPort);
        logger.info("DB_NAME={}", dbName);

        if ("NOT_FOUND".equals(orsApiKey)) {
            logger.warn("ORS_API_KEY was not loaded");
        } else {
            logger.info("ORS_API_KEY successfully loaded");
        }

        assertNotEquals("NOT_FOUND", dbPort);
        assertNotEquals("NOT_FOUND", dbName);
        assertNotEquals("NOT_FOUND", orsApiKey);
    }

    @Test
    void geocode_shouldReturnCoordinates() {

        logger.info("Testing geocoding for Vienna");

        CoordinateDto coordinate =
                geocodingServiceClient.geocode("Vienna");

        logger.info(
                "Received coordinates: longitude={}, latitude={}",
                coordinate.longitude(),
                coordinate.latitude()
        );

        assertNotNull(coordinate);

        assertTrue(coordinate.longitude() > 16.0);
        assertTrue(coordinate.longitude() < 17.0);

        assertTrue(coordinate.latitude() > 48.0);
        assertTrue(coordinate.latitude() < 49.0);
    }

    @Test
    void calculateRoute_shouldReturnRoute() {

        logger.info(
                "Testing car route from Stephansplatz to Schönbrunn"
        );

        RouteDto route = routeServiceClient.calculateRoute(
                "Stephansplatz Vienna",
                "Schönbrunn Vienna",
                "car"
        );

        logger.info(
                "Route received: distance={}m duration={}s",
                route.distance(),
                route.duration()
        );

        assertNotNull(route);

        assertTrue(route.distance() > 0);
        assertTrue(route.duration() > 0);

        List<List<Double>> coordinates = route.coordinates();

        assertNotNull(coordinates);
        assertFalse(coordinates.isEmpty());

        logger.info(
                "Route geometry contains {} coordinate points",
                coordinates.size()
        );

        // Route should contain many geometry points
        assertTrue(
                coordinates.size() > 10,
                "Expected multiple route geometry points"
        );

        // Leaflet format:
        // [latitude, longitude]

        List<Double> firstPoint = coordinates.getFirst();

        assertEquals(2, firstPoint.size());

        logger.info(
                "First route point: latitude={}, longitude={}",
                firstPoint.get(0),
                firstPoint.get(1)
        );
    }

    @Test
    void calculateWalkingRoute_shouldReturnRoute() {

        logger.info(
                "Testing walking route from Karlsplatz to Praterstern"
        );

        RouteDto route = routeServiceClient.calculateRoute(
                "Karlsplatz Vienna",
                "Praterstern Vienna",
                "walking"
        );

        logger.info(
                "Walking route received: distance={}m duration={}s",
                route.distance(),
                route.duration()
        );

        logger.info(
                "Walking route geometry contains {} coordinate points",
                route.coordinates().size()
        );

        assertNotNull(route);

        assertTrue(route.distance() > 0);
        assertTrue(route.duration() > 0);

        assertTrue(route.coordinates().size() > 10);
    }

    @Test
    void geocode_invalidLocation_shouldThrowException() {
        logger.info("Testing invalid geocoding request");

        GeocodingException exception = assertThrows(
                GeocodingException.class,
                () -> geocodingServiceClient.geocode("asdhjasdhjasdhjkasdhjk")
        );

        logger.info(
                "Received expected exception: {}",
                exception.getMessage()
        );

        assertNotNull(exception.getMessage());
    }
}