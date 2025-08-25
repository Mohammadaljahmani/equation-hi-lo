package io.everyonecodes.equation_hi_lo.service;

import io.everyonecodes.equation_hi_lo.domain.GameStatus;
import io.everyonecodes.equation_hi_lo.domain.Round;
import io.everyonecodes.equation_hi_lo.repository.RoundRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledTaskService {

    private final RoundRepository roundRepository;
    private final GameService gameService;

//@lazy used to configure the lazy initialization of the beans. bean is not created at startup, created when it first requested
    public ScheduledTaskService(RoundRepository roundRepository, @Lazy GameService gameService) {
        this.roundRepository = roundRepository;
        this.gameService = gameService;
    }
//Run this method automatically on a schedule every 5 seconds
    @Scheduled(fixedRate = 5000)
    public void checkTurnTimers() {
        List<Round> expiredRounds = roundRepository.findByTurnDeadlineBeforeAndGame_StatusIn(
                LocalDateTime.now(),
                List.of(GameStatus.BETTING, GameStatus.REVEALING)
        );

        for (Round round : expiredRounds) {
            System.out.println("-----> Timer expired for Round #" + round.getId() + " on Player Turn #" + round.getCurrentTurnPlayerId());
            gameService.handleTurnTimeout(round.getGame().getId());
        }
    }
}