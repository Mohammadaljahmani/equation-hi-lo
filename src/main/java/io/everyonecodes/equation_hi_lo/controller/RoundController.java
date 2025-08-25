package io.everyonecodes.equation_hi_lo.controller;

import io.everyonecodes.equation_hi_lo.domain.PlayerAction;
import io.everyonecodes.equation_hi_lo.dto.PlayerActionRequest;
import io.everyonecodes.equation_hi_lo.service.GameService;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/api/games")
public class RoundController {

    private final GameService gameService;

    public RoundController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/{gameId}/players/{gamePlayerId}/actions")
    public PlayerAction performPlayerAction(
            @PathVariable Long gameId,
            @PathVariable Long gamePlayerId,
            @RequestBody PlayerActionRequest request
    ) {

        return gameService.performPlayerAction(gameId, gamePlayerId, request);
    }
}