package at.fhtw.swen.tourplanner.infrastructure.security;

import at.fhtw.swen.tourplanner.infrastructure.LoggerService;
import at.fhtw.swen.tourplanner.domain.model.Session;
import at.fhtw.swen.tourplanner.domain.ports.SessionRepository;
import at.fhtw.swen.tourplanner.domain.ports.TokenPayload;
import at.fhtw.swen.tourplanner.domain.ports.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final SessionRepository sessionRepository;

    // --- Logger ---
    private final org.slf4j.Logger logger = LoggerService.getInstance().getLogger(getClass());

    public JwtAuthenticationFilter(TokenService tokenService, SessionRepository sessionRepository) {
        this.tokenService = tokenService;
        this.sessionRepository = sessionRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
        logger.info("Authorization header: {}", header);

        if (header == null || !header.startsWith("Bearer ")) {
            logger.info("No Bearer token found, skipping authentication");
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            TokenPayload tokenPayload = tokenService.parseAndValidate(token);

            Optional<Session> optionalSession = sessionRepository.findByJti(tokenPayload.sessionId());
            logger.info("Session found in DB: {}", optionalSession.isPresent());

            if (optionalSession.isPresent()) {
                Session session = optionalSession.get();

                boolean valid = !session.isRevoked()
                        && session.getExpiresAt().isAfter(Instant.now())
                        && session.getToken().equals(token);

                logger.info("Session valid: {}", valid);

                if (valid) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    tokenPayload.username(),
                                    null,
                                    AuthorityUtils.NO_AUTHORITIES
                            );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("User {} authenticated successfully", tokenPayload.username());
                }
            }
        } catch (Exception e) {
            logger.warn("JWT authentication failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}