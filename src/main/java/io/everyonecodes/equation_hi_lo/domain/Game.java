package io.everyonecodes.equation_hi_lo.domain;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;
@Entity
@Data
public class Game {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Enumerated(EnumType.STRING) private GameStatus status;
    // mapping an enum into database column and .String tells jpa to store the enum name as a String not like by default as a number

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;
    //game can have many players

    @OneToMany(mappedBy = "game") private Set<Round> rounds;
    //game has multiple rounds
}

