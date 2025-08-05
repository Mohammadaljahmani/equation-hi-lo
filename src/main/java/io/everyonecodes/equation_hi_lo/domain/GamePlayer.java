package io.everyonecodes.equation_hi_lo.domain;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Data
public class GamePlayer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id") private Player player;

    @ManyToOne
    @JoinColumn(name = "game_id") private Game game;
    private int chipCount;
    private boolean isEliminated;
    private String numberCards;
    private String operatorCards;
}