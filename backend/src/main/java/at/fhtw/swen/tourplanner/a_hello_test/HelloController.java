package at.fhtw.swen.tourplanner.a_hello_test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloController {

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/api/hello")
    public Map<String, String> hello() {
        return Map.of("message", helloService.getHelloText());
    }
}