package at.fhtw.swen.tourplanner.persistence.repository;

import at.fhtw.swen.tourplanner.persistence.entity.Route;
import at.fhtw.swen.tourplanner.persistence.exception.RouteNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    public void deleteByTourId(UUID tourId) {
        Route route = findByTourId(tourId)
                .orElseThrow(() ->
                        new RouteNotFoundException(
                                "Route for tour " + tourId + " not found"));
        routeRepository.delete(route);
    }

    public void deleteById(UUID id) {
        Route route = findById(id)
                .orElseThrow(() ->
                        new RouteNotFoundException(
                                "Route with id " + id + " not found"));
        routeRepository.delete(route);
    }

    public List<Route> findAll() {
        return routeRepository.findAll();
    }
}