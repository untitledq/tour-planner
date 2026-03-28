package at.fhtw.swen.tourplanner.config;

import at.fhtw.swen.tourplanner.service.AuthService;
import at.fhtw.swen.tourplanner.service.UserService;
import at.fhtw.swen.tourplanner.service.impl.AuthServiceImpl;
import at.fhtw.swen.tourplanner.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public AuthService authService(AuthServiceImpl impl) {
        return impl; // exposes the implementation as the interface
    }

    @Bean
    public UserService userService(UserServiceImpl impl) {
        return impl; // exposes the implementation as the interface
    }
}