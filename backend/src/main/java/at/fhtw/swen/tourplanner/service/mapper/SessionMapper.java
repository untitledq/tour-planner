package at.fhtw.swen.tourplanner.service.mapper;

import at.fhtw.swen.tourplanner.persistence.entity.Session;
import at.fhtw.swen.tourplanner.service.dto.SessionDto;

public class SessionMapper {

    public static SessionDto toDto(Session s) {
        return new SessionDto(
                s.getJti(),
                s.getUserId(),
                s.getToken(),
                s.getIssuedAt(),
                s.getExpiresAt(),
                s.isRevoked()
        );
    }

    public static Session toEntity(SessionDto dto) {
        return new Session(
                dto.jti(),
                dto.userId(),
                dto.token(),
                dto.issuedAt(),
                dto.expiresAt(),
                dto.revoked()
        );
    }
}