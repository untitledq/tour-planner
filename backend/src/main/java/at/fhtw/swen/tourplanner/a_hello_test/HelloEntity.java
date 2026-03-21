package at.fhtw.swen.tourplanner.a_hello_test;

import jakarta.persistence.*;

@Entity
@Table(name = "text")
public class HelloEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    // getters
    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

}