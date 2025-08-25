package io.everyonecodes.equation_hi_lo.domain;
import jakarta.persistence.FetchType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
public class GamePlayer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id") private Player player;

//    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    private Set<GamePlayer> gamePlayers;

    @ManyToOne
    @JoinColumn(name = "game_id") private Game game;
    private int chipCount;
    private boolean hasFolded;
    private boolean isEliminated;
    private String numberCards;
    private String operatorCards;
}