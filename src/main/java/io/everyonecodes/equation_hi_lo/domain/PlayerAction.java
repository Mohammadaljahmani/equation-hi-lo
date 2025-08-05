package io.everyonecodes.equation_hi_lo.domain;
import jakarta.persistence.*;
import lombok.Data;
@Entity
@Data
public class PlayerAction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne @JoinColumn(name = "round_id") private Round round;

    @ManyToOne @JoinColumn(name = "game_player_id") private GamePlayer gamePlayer;

    @Enumerated(EnumType.STRING) private ActionType type;
    private int betAmount;
    private String equation;
    private Double result;
    private String target;
}

