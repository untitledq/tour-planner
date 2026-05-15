package at.fhtw.swen.tourplanner.controller;

import at.fhtw.swen.tourplanner.controller.response.SearchResultResponse;
import at.fhtw.swen.tourplanner.security.spring.JwtUserDetails;
import at.fhtw.swen.tourplanner.service.SearchService;
import at.fhtw.swen.tourplanner.util.LoggerService;
import org.slf4j.Logger;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;
    private final Logger logger;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
        this.logger = LoggerService.getInstance().getLogger(getClass());
    }

    @GetMapping
    public List<SearchResultResponse> search(
            @RequestParam(name = "q", defaultValue = "") String query,
            @RequestParam(name = "scope", defaultValue = "all") String scope,
            @AuthenticationPrincipal JwtUserDetails userDetails
    ){
        UUID userId = userDetails.getUserId();

        logger.info("Search called by user {}, scope={}", userId, scope);

        return searchService.search(userId, query, scope);
    }
}
