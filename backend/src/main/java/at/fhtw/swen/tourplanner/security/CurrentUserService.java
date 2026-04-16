package at.fhtw.swen.tourplanner.security;

import java.util.UUID;

public interface CurrentUserService {
    UUID getUserId();
    String getUsername();
}