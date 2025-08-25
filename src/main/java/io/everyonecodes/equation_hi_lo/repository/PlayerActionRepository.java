package io.everyonecodes.equation_hi_lo.repository;

import io.everyonecodes.equation_hi_lo.domain.ActionType;
import io.everyonecodes.equation_hi_lo.domain.PlayerAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerActionRepository extends JpaRepository<PlayerAction, Long> {
        List<PlayerAction> findByRoundIdAndType(Long roundId, ActionType type);
        long countByRoundIdAndType(Long roundId, ActionType type);

}