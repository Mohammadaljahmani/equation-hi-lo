package io.everyonecodes.equation_hi_lo.repository;

import io.everyonecodes.equation_hi_lo.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}