package io.everyonecodes.equation_hi_lo.controller;

import io.everyonecodes.equation_hi_lo.domain.Player;
import io.everyonecodes.equation_hi_lo.dto.PlayerResponseDTO;
import io.everyonecodes.equation_hi_lo.service.PlayerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public PlayerResponseDTO createPlayer(@RequestBody Player player) {
        return playerService.createPlayer(player);
    }
}