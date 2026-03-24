package at.fhtw.swen.tourplanner;

import at.fhtw.swen.tourplanner.infrastructure.LoggerService;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TourPlannerApplication implements CommandLineRunner {

    private final Logger logger = LoggerService.getInstance().getLogger(getClass());

    public static void main(String[] args) {
        SpringApplication.run(TourPlannerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        logger.info(" ###  TourPlanner started  ### ");
    }
}