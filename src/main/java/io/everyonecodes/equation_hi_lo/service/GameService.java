package io.everyonecodes.equation_hi_lo.service;

import io.everyonecodes.equation_hi_lo.domain.*;
import io.everyonecodes.equation_hi_lo.dto.PlayerActionRequest;
import io.everyonecodes.equation_hi_lo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
@Transactional
//Spring annotation used to define the scope of a transaction (all or nothing: If all steps succeed(save changes), if not rollback(undo changes))
//when method is called a transaction start...
public class GameService {


    private static final int BETTING_TURN_SECONDS = 15;
    private static final int REVEAL_TURN_SECONDS = 30;


    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GamePlayerRepository gamePlayerRepository;
    private final PlayerActionRepository playerActionRepository;
    private final RoundRepository roundRepository;
    private final EquationService equationService;


    public GameService(GameRepository gameRepository, PlayerRepository playerRepository,
                       GamePlayerRepository gamePlayerRepository, PlayerActionRepository playerActionRepository,
                       RoundRepository roundRepository, EquationService equationService) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.gamePlayerRepository = gamePlayerRepository;
        this.playerActionRepository = playerActionRepository;
        this.roundRepository = roundRepository;
        this.equationService = equationService;
    }

    public Game createGameWithPlayer(Player creator) {
        Game newGame = new Game();
        newGame.setStatus(GameStatus.WAITING);
        newGame.setCreatedAt(LocalDateTime.now());
        gameRepository.save(newGame);

        GamePlayer gp = new GamePlayer();
        gp.setPlayer(creator);
        gp.setGame(newGame);
        gp.setChipCount(100);
        gamePlayerRepository.save(gp);

        Round round = new Round();
        round.setGame(newGame);
        round.setRoundNumber(1);
        roundRepository.save(round);

        return newGame;
    }

    public GamePlayer joinGame(Long gameId, Long playerId) {
        Game game = getGameDetails(gameId);
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found: " + playerId));

        if (game.getStatus() != GameStatus.WAITING) {
            throw new IllegalStateException("Game is not in WAITING state.");
        }
        if (gamePlayerRepository.existsByGameAndPlayer(game, player)) {
            throw new IllegalStateException("Player has already joined this game.");
        }

        GamePlayer newGp = new GamePlayer();
        newGp.setGame(game);
        newGp.setPlayer(player);
        newGp.setChipCount(100);
        return gamePlayerRepository.save(newGp);
    }

    public Game startGame(Long gameId) {
        Game game = getGameDetails(gameId);
        if (game.getStatus() != GameStatus.WAITING) throw new IllegalStateException("Game not in WAITING state.");
        List<GamePlayer> participants = gamePlayerRepository.findByGameId(gameId);
        if (participants.size() < 2) throw new IllegalStateException("Not enough players to start.");

        game.setStatus(GameStatus.BETTING);
        for (GamePlayer participant : participants) {
            participant.setHasFolded(false);
            dealCards(participant);
        }

        Round currentRound = getCurrentRound(gameId);
        currentRound.setPotSize(0);
        setNextTurn(currentRound, participants.get(0));

        return gameRepository.save(game);
    }



    public PlayerAction performPlayerAction(Long gameId, Long gamePlayerId, PlayerActionRequest request) {
        Game game = getGameDetails(gameId);
        Round round = getCurrentRound(gameId);
        GamePlayer player = gamePlayerRepository.findById(gamePlayerId).orElseThrow();

        if (!round.getCurrentTurnPlayerId().equals(player.getId())) throw new IllegalStateException("It is not your turn.");
        if (game.getStatus() != GameStatus.BETTING && game.getStatus() != GameStatus.REVEALING) {
            throw new IllegalStateException("It is not the right time to perform this action.");
        }

        PlayerAction action = new PlayerAction();
        action.setRound(round);
        action.setGamePlayer(player);
        action.setType(request.getActionType());

        switch (request.getActionType()) {
            case BET, RAISE, CALL:
                if (player.getChipCount() < request.getBetAmount()) throw new IllegalStateException("Not enough chips.");
                player.setChipCount(player.getChipCount() - request.getBetAmount());
                round.setPotSize(round.getPotSize() + request.getBetAmount());
                action.setBetAmount(request.getBetAmount());
                gamePlayerRepository.save(player);
                break;
            case FOLD:
                player.setHasFolded(true);
                gamePlayerRepository.save(player);
                break;
            case REVEAL:
                if (game.getStatus() != GameStatus.REVEALING) throw new IllegalStateException("It is not time to reveal.");
                action.setEquation(request.getEquation());
                action.setTarget(request.getTarget());
                break;
        }

        playerActionRepository.save(action);
        advanceTurn(gameId);
        return action;
    }

    public void handleTurnTimeout(Long gameId) {
        Round round = getCurrentRound(gameId);
        GamePlayer timedOutPlayer = gamePlayerRepository.findById(round.getCurrentTurnPlayerId()).orElse(null);
        if (timedOutPlayer == null) { advanceTurn(gameId); return; }

        System.out.println("-----> Forcing action for timed-out player: " + timedOutPlayer.getPlayer().getUsername());
        PlayerAction forcedAction = new PlayerAction();
        forcedAction.setRound(round);
        forcedAction.setGamePlayer(timedOutPlayer);

        Game game = getGameDetails(gameId);
        if (game.getStatus() == GameStatus.BETTING) {
            timedOutPlayer.setHasFolded(true);
            gamePlayerRepository.save(timedOutPlayer);
            forcedAction.setType(ActionType.FOLD);
        } else if (game.getStatus() == GameStatus.REVEALING) {
            forcedAction.setType(ActionType.REVEAL);
            forcedAction.setEquation("0");
            forcedAction.setTarget("LOW");
        }
        playerActionRepository.save(forcedAction);
        advanceTurn(gameId);
    }


    public Game getGameDetails(Long gameId) { return gameRepository.findById(gameId).orElseThrow(); }
    public List<Game> findAllGames() { return gameRepository.findAll(); }




    private void advanceTurn(Long gameId) {
        Game game = getGameDetails(gameId);
        Round round = getCurrentRound(gameId);
        List<GamePlayer> activePlayers = gamePlayerRepository.findByGameId(gameId).stream()
                .filter(p -> !p.isEliminated() && !p.isHasFolded()).toList();

        ActionType actionTypeToCount;

        if (game.getStatus() == GameStatus.BETTING) {
            actionTypeToCount = ActionType.BET;
        } else {
            actionTypeToCount = ActionType.REVEAL;
        }
        long actionsInPhase = playerActionRepository.countByRoundIdAndType(round.getId(), actionTypeToCount);

        if (activePlayers.size() <= 1 || actionsInPhase >= activePlayers.size()) {
            if (game.getStatus() == GameStatus.BETTING) {
                System.out.println("-----> Betting round over, moving to REVEALING phase.");
                game.setStatus(GameStatus.REVEALING);
                gameRepository.save(game);
                if (activePlayers.isEmpty()) { determineWinnersAndFinalizeRound(gameId); return; }
                setNextTurn(round, activePlayers.get(0));
            } else {
                System.out.println("-----> Revealing is over. Determining winners now.");
                determineWinnersAndFinalizeRound(gameId);
            }
            return;
        }

        int currentIndex = -1;
        for (int i = 0; i < activePlayers.size(); i++) {
            if (activePlayers.get(i).getId().equals(round.getCurrentTurnPlayerId())) {
                currentIndex = i; break;
            }
        }
        int nextIndex = (currentIndex + 1) % activePlayers.size();
        setNextTurn(round, activePlayers.get(nextIndex));
    }

    private void setNextTurn(Round round, GamePlayer nextPlayer) {
        System.out.println("-----> Turn advances to: " + nextPlayer.getPlayer().getUsername());
        round.setCurrentTurnPlayerId(nextPlayer.getId());

        if (round.getGame().getStatus() == GameStatus.BETTING) {
            round.setTurnDeadline(LocalDateTime.now().plusSeconds(BETTING_TURN_SECONDS));
        } else if (round.getGame().getStatus() == GameStatus.REVEALING) {
            round.setTurnDeadline(LocalDateTime.now().plusSeconds(REVEAL_TURN_SECONDS));
        } else {
            round.setTurnDeadline(null);
        }
        roundRepository.save(round);
    }

    private void determineWinnersAndFinalizeRound(Long gameId) {
        Game game = getGameDetails(gameId);
        Round round = getCurrentRound(gameId);
        int pot = round.getPotSize();

        List<PlayerAction> revealActions = playerActionRepository.findByRoundIdAndType(round.getId(), ActionType.REVEAL);

        Optional<PlayerAction> lowWinnerAction = findClosestPlayer(filterByTarget(revealActions, "LOW"), 1.0);
        Optional<PlayerAction> highWinnerAction = findClosestPlayer(filterByTarget(revealActions, "HIGH"), 20.0);

        if (lowWinnerAction.isPresent() && highWinnerAction.isPresent() && !lowWinnerAction.get().getGamePlayer().getId().equals(highWinnerAction.get().getGamePlayer().getId())) {
            int prize = pot / 2;
            awardChips(lowWinnerAction.get().getGamePlayer(), prize, "LOW");
            awardChips(highWinnerAction.get().getGamePlayer(), prize, "HIGH");
        } else if (lowWinnerAction.isPresent()) {
            awardChips(lowWinnerAction.get().getGamePlayer(), pot, "LOW (and HIGH)");
        } else
            highWinnerAction.ifPresent(playerAction -> awardChips(playerAction.getGamePlayer(), pot, "(HIGH and LOW)"));

        round.setPotSize(0);
        round.setTurnDeadline(null);
        roundRepository.save(round);

        game.setStatus(GameStatus.ROUND_FINISHED);
        gameRepository.save(game);


        checkEndGameConditions(gameId);
    }

    private void checkEndGameConditions(Long gameId) {
        Game game = getGameDetails(gameId);
        List<GamePlayer> allPlayersInGame = gamePlayerRepository.findByGameId(gameId);

        for (GamePlayer gp : allPlayersInGame) {
            if (gp.getChipCount() <= 0 && !gp.isEliminated()) {
                gp.setEliminated(true);
                gamePlayerRepository.save(gp);
                System.out.println("-----> PLAYER ELIMINATED: " + gp.getPlayer().getUsername());
            }
        }

        long activePlayerCount = allPlayersInGame.stream().filter(gp -> !gp.isEliminated()).count();

        if (activePlayerCount <= 1) {
            game.setStatus(GameStatus.GAME_FINISHED);
            gameRepository.save(game);
            System.out.println("-----> GAME OVER! Final winner determined.");
        }
    }

    private void awardChips(GamePlayer winner, int amount, String category) {
        System.out.println("WINNER " + category + ": " + winner.getPlayer().getUsername() + " wins " + amount + " chips!");
        winner.setChipCount(winner.getChipCount() + amount);
        gamePlayerRepository.save(winner);
    }

    private Optional<PlayerAction> findClosestPlayer(List<PlayerAction> actions, double target) {
        return actions.stream()
                .peek(action -> action.setResult(equationService.evaluate(action.getEquation())))
                .min(Comparator.comparingDouble(action -> Math.abs(action.getResult() - target)));
    }

    private List<PlayerAction> filterByTarget(List<PlayerAction> actions, String target) {
        return actions.stream().filter(action -> target.equalsIgnoreCase(action.getTarget())).collect(Collectors.toList());
    }

    private void dealCards(GamePlayer gamePlayer) {
        List<String> nums = new ArrayList<>(List.of("1","2","3","4","5","6","7","8","9","10"));
        List<String> ops = new ArrayList<>(List.of("+", "-", "×", "÷", "√"));
        Collections.shuffle(nums);
        List<String> chosenOps;
        do {
            Collections.shuffle(ops);
            chosenOps = new ArrayList<>(ops.subList(0, 3));
        } while (chosenOps.contains("√") && chosenOps.contains("×"));
        gamePlayer.setNumberCards(String.join(",", nums.subList(0, 4)));
        gamePlayer.setOperatorCards(String.join(",", chosenOps));
        gamePlayerRepository.save(gamePlayer);
    }

    private Round getCurrentRound(Long gameId) {
        return roundRepository.findFirstByGameIdOrderByRoundNumberDesc(gameId)
                .orElseThrow(() -> new RuntimeException("No rounds found for game " + gameId));
    }

    public Game createGame(Long creatorId) { Player p = playerRepository.findById(creatorId).orElseThrow(); return createGameWithPlayer(p); }
}