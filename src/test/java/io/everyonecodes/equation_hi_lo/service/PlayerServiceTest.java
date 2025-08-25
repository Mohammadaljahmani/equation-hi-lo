package io.everyonecodes.equation_hi_lo.service;

import io.everyonecodes.equation_hi_lo.domain.Player;
import io.everyonecodes.equation_hi_lo.dto.PlayerResponseDTO;
import io.everyonecodes.equation_hi_lo.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository mockPlayerRepository;
    @Mock
    private PasswordEncoder mockPasswordEncoder;

    @InjectMocks
    private PlayerService playerService;

    @Test
    void createPlayer_HashesPasswordAndSetsRole_andReturnsDTO() { // Renamed for clarity
        // 1. ARRANGE
        Player inputPlayer = new Player();
        inputPlayer.setUsername("testUser");
        inputPlayer.setPassword("plainPassword");

        // This is what the saved Player entity *should* look like
        Player savedPlayer = new Player();
        savedPlayer.setId(1L); // Assign an ID to the saved object
        savedPlayer.setUsername("testUser");
        savedPlayer.setPassword("hashedPassword123");
        savedPlayer.setRoles("ROLE_USER");

        // Program our mock password encoder
        when(mockPasswordEncoder.encode("plainPassword")).thenReturn("hashedPassword123");

        // Program our mock repository to return our fully formed "saved" player.
        when(mockPlayerRepository.save(Mockito.any(Player.class))).thenReturn(savedPlayer);

        // 2. ACT
        // --- THIS IS THE FIX ---
        // The return type is now correctly PlayerResponseDTO
        PlayerResponseDTO resultDTO = playerService.createPlayer(inputPlayer);

        // 3. ASSERT

        // --- Use an ArgumentCaptor for a more robust test ---
        // This lets us "capture" the object that was passed to the save method
        // so we can inspect it.
        ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);
        verify(mockPlayerRepository).save(playerCaptor.capture());
        Player playerPassedToSave = playerCaptor.getValue();

        // Assertions on the object BEFORE it was saved
        assertEquals("hashedPassword123", playerPassedToSave.getPassword());
        assertEquals("ROLE_USER", playerPassedToSave.getRoles());

        // Assertions on the DTO that was returned to the client
        assertNotNull(resultDTO);
        assertEquals(1L, resultDTO.getId());
        assertEquals("testUser", resultDTO.getUsername());
    }
}