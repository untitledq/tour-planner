package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.controller.response.SearchResultResponse;
import at.fhtw.swen.tourplanner.controller.response.enums.SearchResultType;
import at.fhtw.swen.tourplanner.persistence.repository.SearchRepository;
import at.fhtw.swen.tourplanner.persistence.repository.SearchResultRow;
import at.fhtw.swen.tourplanner.service.SearchService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SearchServiceImpl implements SearchService {
    private final SearchRepository searchRepository;

    public SearchServiceImpl(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Override
    public List<SearchResultResponse> search(UUID userId, String query, String scope) {
        String normalizedQuery = query == null ? "" : query.trim();

        if (normalizedQuery.isBlank()) {
            return List.of();
        }

        String tsQuery = toPrefixTsQuery(normalizedQuery);

        if (tsQuery.isBlank()) {
            return List.of();
        }

        SearchScope normalizedScope = SearchScope.from(scope);

        return searchRepository.search(
                        userId,
                        tsQuery,
                        normalizedScope.includesTours(),
                        normalizedScope.includesTourLogs()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private SearchResultResponse toResponse(SearchResultRow row) {
        return new SearchResultResponse(
                SearchResultType.valueOf(row.type()),
                row.id(),
                row.tourId(),
                row.title(),
                row.snippet()
        );
    }

    private String toPrefixTsQuery(String query) {
        String[] terms = query.toLowerCase().split("[^\\p{L}\\p{N}]+");

        return java.util.Arrays.stream(terms)
                .filter(term -> !term.isBlank())
                .map(term -> term + ":*")
                .reduce((left, right) -> left + " & " + right)
                .orElse("");
    }

    private enum SearchScope {
        ALL(true, true),
        TOURS(true, false),
        TOUR_LOGS(false, true);

        private final boolean includesTours;
        private final boolean includesTourLogs;

        SearchScope(boolean includesTours, boolean includesTourLogs) {
            this.includesTours = includesTours;
            this.includesTourLogs = includesTourLogs;
        }

        private boolean includesTours() {
            return includesTours;
        }

        private boolean includesTourLogs() {
            return includesTourLogs;
        }

        private static SearchScope from(String value) {
            if (value == null) {
                return ALL;
            }

            return switch (value.trim().toLowerCase()) {
                case "tour", "tours" -> TOURS;
                case "tour-log", "tour-logs", "logs" -> TOUR_LOGS;
                default -> ALL;
            };
        }
    }
}
