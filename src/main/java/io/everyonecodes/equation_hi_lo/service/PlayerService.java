package io.everyonecodes.equation_hi_lo.service;

import io.everyonecodes.equation_hi_lo.domain.Player;
import io.everyonecodes.equation_hi_lo.dto.PlayerDTO;
import io.everyonecodes.equation_hi_lo.repository.PlayerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    public PlayerService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Player createPlayer(Player player) {
        String plainPassword = player.getPassword();
        String hashedPassword = passwordEncoder.encode(plainPassword);
        player.setPassword(hashedPassword);
        player.setRoles("ROLE_USER");
        return playerRepository.save(player);
    }

    public List<PlayerDTO> findAllPlayers() {
        List<Player> players = playerRepository.findAll();

        return players.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PlayerDTO convertToDTO(Player player) {
        return new PlayerDTO(player.getId(), player.getUsername());
    }

    public Optional<Player> findByUsername(String username) {
        return playerRepository.findByUsername(username);
    }
}