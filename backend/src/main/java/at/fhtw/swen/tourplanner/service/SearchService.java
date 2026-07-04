package at.fhtw.swen.tourplanner.service;

import at.fhtw.swen.tourplanner.controller.response.SearchResultResponse;

import java.util.List;
import java.util.UUID;

public interface SearchService {
    List<SearchResultResponse> search(UUID userId, String query, String scope);
}
