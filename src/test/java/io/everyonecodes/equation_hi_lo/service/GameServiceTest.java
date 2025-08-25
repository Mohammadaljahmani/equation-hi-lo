package io.everyonecodes.equation_hi_lo.service;

import io.everyonecodes.equation_hi_lo.domain.*;
import io.everyonecodes.equation_hi_lo.dto.PlayerActionRequest;
import io.everyonecodes.equation_hi_lo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {
 //Mock is unit testing, test a method objects whose methods depends on another objects methods
    @Mock private GameRepository mockGameRepository;
    @Mock private PlayerRepository mockPlayerRepository;
    @Mock private GamePlayerRepository mockGamePlayerRepository;
    @Mock private PlayerActionRepository mockPlayerActionRepository;
    @Mock private RoundRepository mockRoundRepository;
    @Mock private EquationService mockEquationService;

    // @InjectMocks creates a REAL GameService instance. automatically injects all the @Mock objects declared above into its constructor.
    @InjectMocks
    private GameService gameService;

    private Game testGame;
    private Round testRound;
    private Player testPlayer1;
    private GamePlayer testGamePlayer1;
    private Player testPlayer2;
    private GamePlayer testGamePlayer2;

    @BeforeEach
    void setUp() {
        testGame = new Game();
        testGame.setId(1L);

        testRound = new Round();
        testRound.setId(100L);
        testRound.setGame(testGame);

        testPlayer1 = new Player();
        testPlayer1.setId(1L);
        testPlayer1.setUsername("Moha");

        testGamePlayer1 = new GamePlayer();
        testGamePlayer1.setId(10L);
        testGamePlayer1.setPlayer(testPlayer1);
        testGamePlayer1.setGame(testGame);

        testPlayer2 = new Player();
        testPlayer2.setId(2L);
        testPlayer2.setUsername("Moha1");

        testGamePlayer2 = new GamePlayer();
        testGamePlayer2.setId(11L);
        testGamePlayer2.setPlayer(testPlayer2);
        testGamePlayer2.setGame(testGame);
    }

    @Test
    void startGame_SuccessfullySetsUpRound() {
        testGame.setStatus(GameStatus.WAITING);
        when(mockGameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(mockGamePlayerRepository.findByGameId(1L)).thenReturn(List.of(testGamePlayer1, testGamePlayer2));
        when(mockRoundRepository.findFirstByGameIdOrderByRoundNumberDesc(1L)).thenReturn(Optional.of(testRound));

        gameService.startGame(1L);


        assertEquals(GameStatus.BETTING, testGame.getStatus());


        assertNotNull(testRound.getTurnDeadline());
        assertEquals(testGamePlayer1.getId(), testRound.getCurrentTurnPlayerId());

        verify(mockGameRepository).save(testGame);
        verify(mockRoundRepository).save(testRound);
        verify(mockGamePlayerRepository, times(2)).save(any(GamePlayer.class));
    }

    @Test
    void performPlayerAction_ValidBet_ProcessesAndAdvancesTurn() {
        testGame.setStatus(GameStatus.BETTING);
        testRound.setCurrentTurnPlayerId(testGamePlayer1.getId());
        testGamePlayer1.setChipCount(100);

        when(mockGameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(mockRoundRepository.findFirstByGameIdOrderByRoundNumberDesc(1L)).thenReturn(Optional.of(testRound));
        when(mockGamePlayerRepository.findById(10L)).thenReturn(Optional.of(testGamePlayer1));
        when(mockGamePlayerRepository.findByGameId(1L)).thenReturn(List.of(testGamePlayer1, testGamePlayer2));
        PlayerActionRequest betRequest = new PlayerActionRequest();
        betRequest.setActionType(ActionType.BET);
        betRequest.setBetAmount(50);

        gameService.performPlayerAction(1L, 10L, betRequest);

        assertEquals(50, testGamePlayer1.getChipCount());
        assertEquals(50, testRound.getPotSize());
        assertEquals(testGamePlayer2.getId(), testRound.getCurrentTurnPlayerId());

        verify(mockGamePlayerRepository).save(testGamePlayer1);
        verify(mockRoundRepository).save(testRound);
        verify(mockPlayerActionRepository).save(any(PlayerAction.class));
    }

    @Test
    void performPlayerAction_InvalidTurn_ThrowsException() {
        testGame.setStatus(GameStatus.BETTING);
        testRound.setCurrentTurnPlayerId(testGamePlayer2.getId());

        when(mockGameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(mockRoundRepository.findFirstByGameIdOrderByRoundNumberDesc(1L)).thenReturn(Optional.of(testRound));
        when(mockGamePlayerRepository.findById(10L)).thenReturn(Optional.of(testGamePlayer1));

        PlayerActionRequest betRequest = new PlayerActionRequest();
        betRequest.setActionType(ActionType.BET);


        assertThrows(IllegalStateException.class, () -> {
            gameService.performPlayerAction(1L, 10L, betRequest);
        });
    }

    @Test
    void handleTurnTimeout_ForcesFoldAndAdvances() {

        testGame.setStatus(GameStatus.BETTING);
        testRound.setCurrentTurnPlayerId(testGamePlayer1.getId());

        when(mockRoundRepository.findFirstByGameIdOrderByRoundNumberDesc(1L)).thenReturn(Optional.of(testRound));
        when(mockGameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(mockGamePlayerRepository.findById(10L)).thenReturn(Optional.of(testGamePlayer1));
        when(mockGamePlayerRepository.findByGameId(1L)).thenReturn(List.of(testGamePlayer1, testGamePlayer2));


        gameService.handleTurnTimeout(1L);


        assertTrue(testGamePlayer1.isHasFolded());
        assertEquals(testGamePlayer2.getId(), testRound.getCurrentTurnPlayerId());
        verify(mockPlayerActionRepository).save(any(PlayerAction.class));
    }
}