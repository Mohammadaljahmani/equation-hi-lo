package io.everyonecodes.equation_hi_lo.repository;

import io.everyonecodes.equation_hi_lo.domain.Game;
import io.everyonecodes.equation_hi_lo.domain.GamePlayer;
import io.everyonecodes.equation_hi_lo.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {

    boolean existsByGameAndPlayer(Game game, Player player);

    List<GamePlayer> findByGameId(Long gameId);

}