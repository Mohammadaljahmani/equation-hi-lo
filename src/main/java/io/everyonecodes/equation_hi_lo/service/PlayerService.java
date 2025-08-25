package io.everyonecodes.equation_hi_lo.service;

import io.everyonecodes.equation_hi_lo.domain.Player;
import io.everyonecodes.equation_hi_lo.dto.PlayerResponseDTO;
import io.everyonecodes.equation_hi_lo.repository.PlayerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    public PlayerService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public PlayerResponseDTO createPlayer(Player player) {

        String plainPassword = player.getPassword();
        player.setPassword(passwordEncoder.encode(plainPassword));
        player.setRoles("ROLE_USER");

        Player savedPlayer = playerRepository.save(player);


        return convertToResponseDTO(savedPlayer);
    }


    private PlayerResponseDTO convertToResponseDTO(Player player) {
        return new PlayerResponseDTO(player.getId(), player.getUsername());
    }


    public Optional<Player> findByUsername(String username) {
        return playerRepository.findByUsername(username);
    }
}