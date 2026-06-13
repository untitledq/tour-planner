package at.fhtw.swen.tourplanner.persistence.repository.adapter;

import at.fhtw.swen.tourplanner.persistence.entity.UserFavoriteTour;
import at.fhtw.swen.tourplanner.persistence.entity.UserFavoriteTourId;
import at.fhtw.swen.tourplanner.persistence.exception.UserFavoriteTourNotFoundException;
import at.fhtw.swen.tourplanner.persistence.repository.UserFavoriteTourRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class UserFavoriteTourRepositoryAdapter {

    private final UserFavoriteTourRepository userFavoriteTourRepository;

    public UserFavoriteTourRepositoryAdapter(
            UserFavoriteTourRepository userFavoriteTourRepository) {
        this.userFavoriteTourRepository = userFavoriteTourRepository;
    }

    public boolean existsByUserIdAndTourId(UUID userId, UUID tourId) {
        return userFavoriteTourRepository.existsByUserIdAndTourId(userId, tourId);
    }

    public UserFavoriteTour save(UserFavoriteTour favoriteTour) {
        return userFavoriteTourRepository.save(favoriteTour);
    }

    public UserFavoriteTour findById(UserFavoriteTourId id) {
        return userFavoriteTourRepository.findById(id)
                .orElseThrow(() ->
                        new UserFavoriteTourNotFoundException(
                                "Favorite tour relation not found for user "
                                        + id.getUserId()
                                        + " and tour "
                                        + id.getTourId()));
    }

    public void deleteByUserIdAndTourId(UUID userId, UUID tourId) {
        if (!userFavoriteTourRepository.existsByUserIdAndTourId(userId, tourId)) {
            throw new UserFavoriteTourNotFoundException(
                    "Favorite tour relation not found for user "
                            + userId
                            + " and tour "
                            + tourId);
        }

        userFavoriteTourRepository.deleteByUserIdAndTourId(userId, tourId);
    }

    public List<UserFavoriteTour> findAll() {
        return userFavoriteTourRepository.findAll();
    }
}