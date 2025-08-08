package io.everyonecodes.equation_hi_lo.controller;

import io.everyonecodes.equation_hi_lo.domain.Game;
import io.everyonecodes.equation_hi_lo.domain.GamePlayer;
import io.everyonecodes.equation_hi_lo.dto.CreateGameRequest;
import io.everyonecodes.equation_hi_lo.dto.JoinGameRequest;
import io.everyonecodes.equation_hi_lo.service.GameService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }
    @PostMapping
    public Game createGame(@RequestBody CreateGameRequest request) {
        return gameService.createGame(request.getPlayerId());
    }

    @GetMapping("/{gameId}")
    public Game showGameDetails(@PathVariable Long gameId) {
        return gameService.getGameDetails(gameId);
    }
    @PostMapping("/{gameId}/players")
    public GamePlayer joinGame(@PathVariable Long gameId, @RequestBody JoinGameRequest request) {
        return gameService.joinGame(gameId, request.getPlayerId());
    }

}