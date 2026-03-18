package at.fhtw.swen.tourplanner.hello;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    private final HelloRepository helloRepository;

    public HelloService(HelloRepository helloRepository) {
        this.helloRepository = helloRepository;
    }

    public String getHelloText() {
        return helloRepository.findFirstByOrderByIdAsc()
                .map(HelloEntity::getContent)
                .orElse("No hello text found");
    }
}