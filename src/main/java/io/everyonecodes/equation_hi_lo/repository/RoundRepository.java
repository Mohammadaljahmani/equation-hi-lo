package io.everyonecodes.equation_hi_lo.repository;

import io.everyonecodes.equation_hi_lo.domain.GameStatus;
import io.everyonecodes.equation_hi_lo.domain.Round;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoundRepository extends JpaRepository<Round, Long> {

    Optional<Round> findFirstByGameIdOrderByRoundNumberDesc(Long gameId);
    List<Round> findByTurnDeadlineBeforeAndGame_StatusIn(LocalDateTime now, List<GameStatus> statuses);
}