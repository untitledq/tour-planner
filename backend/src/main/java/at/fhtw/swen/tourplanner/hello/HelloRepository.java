package at.fhtw.swen.tourplanner.hello;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HelloRepository extends JpaRepository<HelloEntity, Long> {

    Optional<HelloEntity> findFirstByOrderByIdAsc();
}