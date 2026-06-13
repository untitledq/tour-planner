package at.fhtw.swen.tourplanner.service.impl;

import at.fhtw.swen.tourplanner.persistence.entity.UserFavoriteTour;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.TourRepositoryAdapter;
import at.fhtw.swen.tourplanner.persistence.repository.adapter.UserFavoriteTourRepositoryAdapter;
import at.fhtw.swen.tourplanner.service.FavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final UserFavoriteTourRepositoryAdapter userFavoriteTourRepositoryAdapter;
    private final TourRepositoryAdapter tourRepositoryAdapter;

    public FavoriteServiceImpl(UserFavoriteTourRepositoryAdapter userFavoriteTourRepositoryAdapter,
                               TourRepositoryAdapter tourRepositoryAdapter) {
        this.userFavoriteTourRepositoryAdapter = userFavoriteTourRepositoryAdapter;
        this.tourRepositoryAdapter = tourRepositoryAdapter;
    }

    @Transactional
    public void addFavorite(UUID userId, UUID tourId) {

        tourRepositoryAdapter.findById(tourId);

        if (!userFavoriteTourRepositoryAdapter.existsByUserIdAndTourId(userId, tourId)) {
            userFavoriteTourRepositoryAdapter.save(
                    new UserFavoriteTour(userId, tourId)
            );
        }
    }

    @Override
    @Transactional
    public void removeFavorite(UUID userId, UUID tourId) {
        userFavoriteTourRepositoryAdapter.deleteByUserIdAndTourId(userId, tourId);
    }
}