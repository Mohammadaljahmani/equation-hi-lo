package io.everyonecodes.equation_hi_lo.repository;

import io.everyonecodes.equation_hi_lo.domain.PlayerAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerActionRepository extends JpaRepository<PlayerAction, Long> {

}