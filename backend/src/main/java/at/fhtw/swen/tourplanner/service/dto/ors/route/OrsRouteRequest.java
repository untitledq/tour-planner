package at.fhtw.swen.tourplanner.service.dto.ors.route;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OrsRouteRequest(

        List<List<Double>> coordinates,

        @JsonProperty("instructions")
        Boolean instructions
) {
}