package at.fhtw.swen.tourplanner;

import at.fhtw.swen.tourplanner.infrastructure.LoggerService;
import at.fhtw.swen.tourplanner.a_hello_test.HelloEntity;
import at.fhtw.swen.tourplanner.a_hello_test.HelloRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TourPlannerApplication implements CommandLineRunner {

    private final Logger logger = LoggerService.getInstance().getLogger(getClass());

    public static void main(String[] args) {
        SpringApplication.run(TourPlannerApplication.class, args);
    }

    @Autowired
    private HelloRepository helloRepository;

    @Override
    public void run(String... args) {
        logger.info(" ### TestLog ###");

        String dbText = helloRepository.findFirstByOrderByIdAsc()
                .map(HelloEntity::getContent)
                .orElse("No hello text found");
        logger.info("First text from DB: " + dbText);
        // testzweck
    }
}