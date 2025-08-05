package io.everyonecodes.equation_hi_lo.repository;

import io.everyonecodes.equation_hi_lo.domain.Round;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundRepository extends JpaRepository<Round, Long> {
}