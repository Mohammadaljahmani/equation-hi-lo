package io.everyonecodes.equation_hi_lo.domain;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;
@Entity
@Data
public class Round {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id") private Game game;

    private int roundNumber;

    @OneToMany(mappedBy = "round") private Set<PlayerAction> playerActions;

    private int potSize;
}