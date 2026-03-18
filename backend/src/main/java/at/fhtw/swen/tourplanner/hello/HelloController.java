package at.fhtw.swen.tourplanner.hello;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200") // allow frontend dev server
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

//package at.fhtw.swen.tourplanner.hello;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//@RestController
//public class HelloController {
//
//    private final HelloService helloService;
//
//    public HelloController(HelloService helloService) {
//        this.helloService = helloService;
//    }
//    @GetMapping("/api/hello")
//    public Map<String, String> hello() {
//        return Map.of("message", helloService.getHelloText());
//    }
//}