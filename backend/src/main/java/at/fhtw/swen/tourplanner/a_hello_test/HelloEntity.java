package at.fhtw.swen.tourplanner.a_hello_test;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "text")
public class HelloEntity {

    // getters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

}