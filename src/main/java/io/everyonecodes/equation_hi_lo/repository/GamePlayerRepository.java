package io.everyonecodes.equation_hi_lo.repository;

import io.everyonecodes.equation_hi_lo.domain.GamePlayer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {

}