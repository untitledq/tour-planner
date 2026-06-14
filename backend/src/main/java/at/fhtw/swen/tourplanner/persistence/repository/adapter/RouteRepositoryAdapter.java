package at.fhtw.swen.tourplanner.persistence.repository.adapter;

import at.fhtw.swen.tourplanner.persistence.entity.Route;
import at.fhtw.swen.tourplanner.persistence.repository.RouteRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RouteRepositoryAdapter {

    private final RouteRepository routeRepository;

    public RouteRepositoryAdapter(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public Optional<Route> findById(UUID id) {
        return routeRepository.findById(id);
    }

    public Optional<Route> findByTourId(UUID tourId) {
        return routeRepository.findByTourId(tourId);
    }

    public Route save(Route route) {
        return routeRepository.save(route);
    }
}