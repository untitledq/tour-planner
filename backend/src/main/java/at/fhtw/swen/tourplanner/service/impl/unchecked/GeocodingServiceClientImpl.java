package at.fhtw.swen.tourplanner.service.impl.unchecked;

import at.fhtw.swen.tourplanner.service.GeocodingServiceClient;
import at.fhtw.swen.tourplanner.service.dto.CoordinateDto;
import at.fhtw.swen.tourplanner.service.dto.ors.geocode.OrsFeature;
import at.fhtw.swen.tourplanner.service.dto.ors.geocode.OrsGeocodeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GeocodingServiceClientImpl implements GeocodingServiceClient {

    private final RestClient restClient;

    public GeocodingServiceClientImpl(
            @Value("${ors.api.key}") String apiKey,
            @Value("${ors.api.geocode-url}") String geocodeUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(geocodeUrl)
                .defaultHeader("Authorization", apiKey)
                .build();
    }

    @Override
    public CoordinateDto geocode(String location) {

        OrsGeocodeResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("text", location)
                        .build())
                .retrieve()
                .body(OrsGeocodeResponse.class);

        if (response == null
                || response.features() == null
                || response.features().isEmpty()) {
            throw new RuntimeException(
                    "No geocoding result for: " + location
            );
        }

        OrsFeature feature = response.features().getFirst();

        return new CoordinateDto(
                feature.geometry().coordinates().get(0),
                feature.geometry().coordinates().get(1)
        );
    }
}