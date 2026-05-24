package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.controller.response.SearchResultResponse;
import at.fhtw.swen.tourplanner.controller.response.enums.SearchResultType;
import at.fhtw.swen.tourplanner.persistence.entity.Tour;
import at.fhtw.swen.tourplanner.persistence.entity.TourLog;
import at.fhtw.swen.tourplanner.persistence.repository.SearchRepository;
import at.fhtw.swen.tourplanner.persistence.repository.SearchResultRow;
import at.fhtw.swen.tourplanner.persistence.repository.TourLogRepository;
import at.fhtw.swen.tourplanner.persistence.repository.TourRepository;
import at.fhtw.swen.tourplanner.service.SearchService;
import at.fhtw.swen.tourplanner.service.dto.ChildFriendliness;
import at.fhtw.swen.tourplanner.service.dto.Popularity;
import at.fhtw.swen.tourplanner.service.dto.TourComputedAttributesDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    private final SearchRepository searchRepository;
    private final TourRepository tourRepository;
    private final TourLogRepository tourLogRepository;

    public SearchServiceImpl(
            SearchRepository searchRepository,
            TourRepository tourRepository,
            TourLogRepository tourLogRepository
    ) {
        this.searchRepository = searchRepository;
        this.tourRepository = tourRepository;
        this.tourLogRepository = tourLogRepository;
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

        List<SearchResultResponse> textResults = searchRepository.search(
                        userId,
                        tsQuery,
                        normalizedScope.includesTours(),
                        normalizedScope.includesTourLogs()
                )
                .stream()
                .map(this::toResponse)
                .toList();

        if (!normalizedScope.includesTours()) {
            return textResults;
        }

        return mergeResults(textResults, searchComputedTourAttributes(userId, normalizedQuery));
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

    private List<SearchResultResponse> searchComputedTourAttributes(UUID userId, String query) {
        ComputedSearchIntent intent = ComputedSearchIntent.from(query);

        if (intent.isEmpty()) {
            return List.of();
        }

        List<Tour> tours = tourRepository.findAllByCreatedBy(userId);
        Map<UUID, TourComputedAttributesDto> attributesByTourId = getComputedAttributesByTourId(userId);

        return tours.stream()
                .filter(tour -> intent.matches(attributesByTourId.getOrDefault(tour.getId(), TourComputedAttributesDto.empty())))
                .map(tour -> toComputedResponse(tour, attributesByTourId.getOrDefault(tour.getId(), TourComputedAttributesDto.empty())))
                .toList();
    }

    private List<SearchResultResponse> mergeResults(
            List<SearchResultResponse> textResults,
            List<SearchResultResponse> computedResults
    ) {
        Map<String, SearchResultResponse> resultsByKey = new LinkedHashMap<>();
        List<SearchResultResponse> mergedResults = new ArrayList<>();
        mergedResults.addAll(textResults);
        mergedResults.addAll(computedResults);

        for (SearchResultResponse result : mergedResults) {
            resultsByKey.putIfAbsent(result.type() + ":" + result.id(), result);
        }

        return List.copyOf(resultsByKey.values());
    }

    private SearchResultResponse toComputedResponse(Tour tour, TourComputedAttributesDto attributes) {
        return new SearchResultResponse(
                SearchResultType.TOUR,
                tour.getId().toString(),
                tour.getId().toString(),
                tour.getName(),
                computedSnippet(attributes)
        );
    }

    private String computedSnippet(TourComputedAttributesDto attributes) {
        return String.format(
                "Popularity: %s, Child-friendly: %s, Logs: %d, Avg. difficulty: %s, Avg. time: %s, Avg. distance: %s",
                label(attributes.getPopularity()),
                label(attributes.getChildFriendliness()),
                attributes.getLogCount(),
                valueOrNoData(attributes.getAverageDifficulty()),
                valueOrNoData(attributes.getAverageTotalTime()),
                valueOrNoData(attributes.getAverageTotalDistance())
        );
    }

    private String label(Popularity popularity) {
        return switch (popularity) {
            case VERY_POPULAR -> "very popular";
            case POPULAR -> "popular";
            case LOW -> "low popularity";
            case UNKNOWN -> "unknown popularity";
        };
    }

    private String label(ChildFriendliness childFriendliness) {
        return switch (childFriendliness) {
            case VERY_CHILD_FRIENDLY -> "very child-friendly";
            case CHILD_FRIENDLY -> "child-friendly";
            case MODERATE -> "moderate";
            case NOT_CHILD_FRIENDLY -> "not child-friendly";
            case UNKNOWN -> "unknown child-friendliness";
        };
    }

    private String valueOrNoData(Double value) {
        return value == null ? "no data" : value.toString();
    }

    private Map<UUID, TourComputedAttributesDto> getComputedAttributesByTourId(UUID userId) {
        return tourLogRepository.findAllByCreatedBy(userId).stream()
                .collect(Collectors.groupingBy(
                        TourLog::getTourId,
                        Collectors.collectingAndThen(Collectors.toList(), this::computeAttributes)
                ));
    }

    private TourComputedAttributesDto computeAttributes(List<TourLog> logs) {
        if (logs.isEmpty()) {
            return TourComputedAttributesDto.empty();
        }

        int logCount = logs.size();
        double averageDifficulty = average(logs, TourLog::getDifficulty);
        double averageTotalDistance = average(logs, TourLog::getTotalDistance);
        double averageTotalTime = average(logs, TourLog::getTotalTime);

        return TourComputedAttributesDto.builder()
                .logCount(logCount)
                .averageDifficulty(roundOneDecimal(averageDifficulty))
                .averageTotalDistance(roundOneDecimal(averageTotalDistance))
                .averageTotalTime(roundOneDecimal(averageTotalTime))
                .popularity(toPopularity(logCount))
                .childFriendliness(toChildFriendliness(averageDifficulty, averageTotalTime, averageTotalDistance))
                .build();
    }

    private double average(List<TourLog> logs, Function<TourLog, Integer> valueExtractor) {
        return logs.stream()
                .map(valueExtractor)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }

    private double roundOneDecimal(double value) {
        return Math.round(value * 10) / 10.0;
    }

    private Popularity toPopularity(int logCount) {
        if (logCount >= 6) {
            return Popularity.VERY_POPULAR;
        }
        if (logCount >= 3) {
            return Popularity.POPULAR;
        }
        if (logCount >= 1) {
            return Popularity.LOW;
        }
        return Popularity.UNKNOWN;
    }

    private ChildFriendliness toChildFriendliness(
            double averageDifficulty,
            double averageTotalTime,
            double averageTotalDistance
    ) {
        if (averageDifficulty <= 1.5 && averageTotalTime <= 90 && averageTotalDistance <= 5) {
            return ChildFriendliness.VERY_CHILD_FRIENDLY;
        }
        if (averageDifficulty <= 2 && averageTotalTime <= 120 && averageTotalDistance <= 8) {
            return ChildFriendliness.CHILD_FRIENDLY;
        }
        if (averageDifficulty <= 3 && averageTotalTime <= 180 && averageTotalDistance <= 15) {
            return ChildFriendliness.MODERATE;
        }
        return ChildFriendliness.NOT_CHILD_FRIENDLY;
    }

    private record ComputedSearchIntent(
            List<Popularity> popularityMatches,
            List<ChildFriendliness> childFriendlinessMatches
    ) {
        private static ComputedSearchIntent from(String query) {
            String normalizedQuery = normalize(query);
            List<Popularity> popularityMatches = new ArrayList<>();
            List<ChildFriendliness> childFriendlinessMatches = new ArrayList<>();

            if (normalizedQuery.contains("low popularity")) {
                popularityMatches.add(Popularity.LOW);
            } else if (normalizedQuery.contains("very popular")) {
                popularityMatches.add(Popularity.VERY_POPULAR);
            } else if (normalizedQuery.contains("popular") || normalizedQuery.contains("popularity")) {
                popularityMatches.add(Popularity.POPULAR);
                popularityMatches.add(Popularity.VERY_POPULAR);
            }

            if (normalizedQuery.contains("very child friendly")) {
                childFriendlinessMatches.add(ChildFriendliness.VERY_CHILD_FRIENDLY);
            } else if (normalizedQuery.contains("not child friendly")) {
                childFriendlinessMatches.add(ChildFriendliness.NOT_CHILD_FRIENDLY);
            } else if (normalizedQuery.contains("child friendly") || normalizedQuery.contains("child friendliness")) {
                childFriendlinessMatches.add(ChildFriendliness.CHILD_FRIENDLY);
                childFriendlinessMatches.add(ChildFriendliness.VERY_CHILD_FRIENDLY);
            } else if (normalizedQuery.contains("moderate")) {
                childFriendlinessMatches.add(ChildFriendliness.MODERATE);
            }

            return new ComputedSearchIntent(popularityMatches, childFriendlinessMatches);
        }

        private static String normalize(String query) {
            return (query == null ? "" : query)
                    .toLowerCase()
                    .replace('-', ' ')
                    .replace('_', ' ')
                    .replaceAll("[^\\p{L}\\p{N}]+", " ")
                    .trim()
                    .replaceAll("\\s+", " ");
        }

        private boolean isEmpty() {
            return popularityMatches.isEmpty() && childFriendlinessMatches.isEmpty();
        }

        private boolean matches(TourComputedAttributesDto attributes) {
            return popularityMatches.contains(attributes.getPopularity())
                    || childFriendlinessMatches.contains(attributes.getChildFriendliness());
        }
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
