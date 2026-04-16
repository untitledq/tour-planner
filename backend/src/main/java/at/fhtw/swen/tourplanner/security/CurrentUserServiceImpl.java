package at.fhtw.swen.tourplanner.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {

    @Override
    public UUID getUserId() {
        JwtUserDetails user = getPrincipal();
        return user.getUserId();
    }

    @Override
    public String getUsername() {
        return getPrincipal().getUsername();
    }

    private JwtUserDetails getPrincipal() {
        return (JwtUserDetails) Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getPrincipal();
    }
}