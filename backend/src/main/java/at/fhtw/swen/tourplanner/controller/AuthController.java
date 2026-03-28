package at.fhtw.swen.tourplanner.controller;

import at.fhtw.swen.tourplanner.service.impl.AuthServiceImpl;
import at.fhtw.swen.tourplanner.__domain.model.User;
import at.fhtw.swen.tourplanner.service.impl.UserServiceImpl;
import at.fhtw.swen.tourplanner.__domain.ports.TokenPayload;
import at.fhtw.swen.tourplanner.controller.request.LoginRequest;
import at.fhtw.swen.tourplanner.controller.request.RegisterRequest;
import at.fhtw.swen.tourplanner.controller.response.AuthResponse;
import at.fhtw.swen.tourplanner.controller.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserServiceImpl userServiceImpl;
    private final AuthServiceImpl authServiceImpl;


    public AuthController(UserServiceImpl userServiceImpl, AuthServiceImpl authServiceImpl) {
        this.userServiceImpl = userServiceImpl;
        this.authServiceImpl = authServiceImpl;
    }

    @PostMapping("/user/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody RegisterRequest request) {
        User user = userServiceImpl.register(
                request.username(),
                request.password(),
                request.email()
        );
        LoginResponse login = authServiceImpl.login(
                request.username(),
                request.password()
        );

        return new AuthResponse(
                user.id(),
                user.username(),
                user.createdAt(),
                login.token()
        );
    }

    @PostMapping("/user/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authServiceImpl.login(
                request.username(),
                request.password()
        );
    }

    @PostMapping("/user/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header");
        }

        String token = header.substring(7);
        authServiceImpl.logout(token);
    }

    @DeleteMapping("/user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new SecurityException("Missing or invalid Authorization header");
        }

        String token = header.substring(7);

        TokenPayload payload = authServiceImpl.parseToken(token);
        userServiceImpl.deleteUser(payload.userId());
    }
}
