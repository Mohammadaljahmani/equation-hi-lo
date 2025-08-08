package io.everyonecodes.equation_hi_lo.service;

import io.everyonecodes.equation_hi_lo.domain.*;
import io.everyonecodes.equation_hi_lo.repository.GamePlayerRepository;
import io.everyonecodes.equation_hi_lo.repository.GameRepository;
import io.everyonecodes.equation_hi_lo.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GamePlayerRepository gamePlayerRepository;

    public GameService(GameRepository gameRepository, PlayerRepository playerRepository, GamePlayerRepository gamePlayerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.gamePlayerRepository = gamePlayerRepository;
    }


    public Game createGame(Long creatorId) {

        Player creator = playerRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Player not found with ID: " + creatorId));

        return createGameWithPlayer(creator);
    }

    public GamePlayer joinGame(Long gameId, Long playerId) {

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with ID: " + gameId));

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found with ID: " + playerId));


        if (game.getStatus() != GameStatus.WAITING) {
            throw new IllegalStateException("Cannot join a game that is not in WAITING status.");
        }

        boolean alreadyJoined = gamePlayerRepository.existsByGameAndPlayer(game, player);
        if (alreadyJoined) {
            throw new IllegalStateException("Player has already joined this game.");
        }

        if (game.getGamePlayers().size() >= 8) {
            throw new IllegalStateException("This game is full. Cannot exceed 8 players.");
        }

        GamePlayer newGamePlayer = new GamePlayer();
        newGamePlayer.setGame(game);
        newGamePlayer.setPlayer(player);
        newGamePlayer.setChipCount(100);
        newGamePlayer.setEliminated(false);

        return gamePlayerRepository.save(newGamePlayer);
    }

    public List<Game> findAllGames() {
        return gameRepository.findAll();
    }

    public Game getGameDetails(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with ID: " + gameId));
    }

    public Game createGameWithPlayer(Player creator) {
        Game newGame = new Game();
        newGame.setStatus(GameStatus.WAITING);
        newGame.setCreatedAt(LocalDateTime.now());
        Game savedGame = gameRepository.save(newGame);

        GamePlayer gamePlayerLink = new GamePlayer();
        gamePlayerLink.setPlayer(creator);
        gamePlayerLink.setGame(savedGame);
        gamePlayerLink.setChipCount(100);
        gamePlayerLink.setEliminated(false);

        gamePlayerRepository.save(gamePlayerLink);

        return savedGame;
    }

}