package org.poo.fileoutput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.CardInput;
import org.poo.fileio.GameInput;
import org.poo.fileio.Input;

import java.util.ArrayList;

public final class PlayGame {
    private final Input input;
    private GameBoard gameBoard;
    private final Player playerOne;
    private final Player playerTwo;
    private final GameActions gameActions;

    public PlayGame(final Input input, final ObjectMapper objectMapper) {
        this.input = input;
        playerOne = new Player();
        playerTwo = new Player();
        gameActions = new GameActions(objectMapper);
    }

    /**
     * Aceasta metoda seteaza campul "hero" al unui jucator si mana de inceput
     * a eroului.
     */
    public void setPlayersHero(final CardInput playerOneHero, final CardInput playerTwoHero) {
        final int heroMana = 30;
        playerOne.setHero(playerOneHero);
        playerOne.getHero().setHealth(heroMana);
        playerTwo.setHero(playerTwoHero);
        playerTwo.getHero().setHealth(heroMana);
    }

    /**
     * Aceasta metoda ia deck-ul ales de un jucator si pune
     * elemntele acestuia in copia din clasa "Player".
     */
    public void initializePlayersDecks(final GameInput game) {
        playerOne.suffleDeck(input.getPlayerOneDecks().getDecks().
                        get(game.getStartGame().getPlayerOneDeckIdx()),
                game.getStartGame().getShuffleSeed());

        playerTwo.suffleDeck(input.getPlayerTwoDecks().getDecks().
                        get(game.getStartGame().getPlayerTwoDeckIdx()),
                game.getStartGame().getShuffleSeed());
    }

    /**
     * Aceasta metoda implementeaza actiunile de la inceputul unei runde.
     */
    public void startRound(final int roundNumber) {
        final int maxMana = 10;
        playerOne.setTurnFinished(false);
        playerOne.setMana(playerOne.getMana() + (Math.min(roundNumber, maxMana)));
        if (!playerOne.getPlayerShuffledDeck().isEmpty()) {
            playerOne.getCardsInHand().add(new CardProperties(playerOne.getPlayerShuffledDeck()
                    .getFirst()));
            playerOne.getPlayerShuffledDeck().removeFirst();
        }

        playerTwo.setTurnFinished(false);
        playerTwo.setMana(playerTwo.getMana() + (Math.min(roundNumber, maxMana)));
        if (!playerTwo.getPlayerShuffledDeck().isEmpty()) {
            playerTwo.getCardsInHand().add(new CardProperties(playerTwo.getPlayerShuffledDeck()
                    .getFirst()));
            playerTwo.getPlayerShuffledDeck().removeFirst();
        }
    }

    /**
     * Aceasta metoda este apelata cand se termina tura unui jucator,
     * pentru a ii reseta anumiti parametri.
     */
    public void endPlayerTurn(final Player player, final int row) {
        player.setTurnFinished(true);
        player.setHeroHasAttacked(false);

        for (int i = row; i < row + 2; i++) {
            for (CardProperties card : gameBoard.getCardsOnTheBoard().get(i)) {
                card.setHasAttacked(false);
                if (card.isFrozen()) {
                    card.setFreezeCounter(card.getFreezeCounter() - 1);
                    if (card.getFreezeCounter() == 0) {
                        card.setFrozen(false);
                    }
                }
            }
        }
    }

    /**
     * Aceasta metoda primeste o actiune la input si apeleaza metoda specifica ei,
     * pentru a modifica anumiti parametri ai jocului, jucatorilor si/sau tablei de joc
     * si pentru a genera outputul necesar.
     */
    public void doAction(final GameInput game, final ActionsInput action, final ArrayNode output,
                         final GameState gameState) {
        switch (action.getCommand()) {
            case "endPlayerTurn" -> {
                if (gameState.getActivePlayer() == 1) {
                    endPlayerTurn(playerOne, 2);
                    gameState.setActivePlayer(2);
                } else {
                    endPlayerTurn(playerTwo, 0);
                    gameState.setActivePlayer(1);
                }
                if (playerOne.isTurnFinished() && playerTwo.isTurnFinished()) {
                    gameState.setRoundNumber(gameState.getRoundNumber() + 1);
                    startRound(gameState.getRoundNumber());
                }
            }
            case "getPlayerDeck" -> gameActions.getPlayerDeck(output, action,
                    (action.getPlayerIdx() == 1) ? playerOne.getPlayerShuffledDeck()
                            : playerTwo.getPlayerShuffledDeck());
            case "getPlayerHero" -> gameActions.getPlayerHero(output, action,
                    (action.getPlayerIdx() == 1) ? game.getStartGame().getPlayerOneHero()
                            : game.getStartGame().getPlayerTwoHero());
            case "getPlayerTurn" -> gameActions.getPlayerTurn(output, action,
                    gameState.getActivePlayer());
            case "placeCard" -> {
                String result = gameBoard.placeCard(action.getHandIdx(),
                        gameState.getActivePlayer(),
                        (gameState.getActivePlayer() == 1) ? playerOne : playerTwo);
                if (result != null) {
                    gameActions.placeCardOnBoard(output, action, result);
                }
            }
            case "getCardsInHand" -> gameActions.getCardsInHand(output, action,
                    (action.getPlayerIdx() == 1) ? playerOne.getCardsInHand()
                            : playerTwo.getCardsInHand());
            case "getPlayerMana" -> gameActions.getPlayerMana(output, action,
                    (action.getPlayerIdx() == 1) ? playerOne.getMana()
                            : playerTwo.getMana());
            case "getCardsOnTable" -> gameActions.getCardsOnTable(output, action,
                    gameBoard.getCardsOnTheBoard());
            case "cardUsesAttack" -> {
                String result = gameBoard.attackCard(action, gameState.getActivePlayer());
                if (result != null) {
                    gameActions.cardAttacks(output, action, result);
                }
            }
            case "getCardAtPosition" -> {
                boolean isAvailable = false;
                CardInput card = null;
                if (action.getY() < gameBoard.getCardsOnTheBoard().get(action.getX()).size()) {
                    isAvailable = true;
                    card = gameBoard.getCardsOnTheBoard().
                            get(action.getX()).get(action.getY()).getCardInput();
                }
                gameActions.getCardAtPosition(output, action, card, isAvailable);
            }
            case "cardUsesAbility" -> {
                String result = gameBoard.useCardAbility(action, gameState.getActivePlayer());
                if (result != null) {
                    gameActions.cardAttacks(output, action, result);
                }
            }
            case "useAttackHero" -> {
                String message = gameBoard.attackHero(action, gameState.getActivePlayer(),
                        (gameState.getActivePlayer() == 1) ? playerTwo : playerOne,
                        (gameState.getActivePlayer() == 1) ? playerOne : playerTwo, gameState);
                if (message != null) {
                    gameActions.attackHero(output, action, message);
                }
            }
            case "useHeroAbility" -> {
                String result = gameBoard.useHeroAbility(action, gameState.getActivePlayer(),
                        (gameState.getActivePlayer() == 1) ? playerOne : playerTwo);
                if (result != null) {
                    gameActions.useHeroAbility(output, action, result);
                }
            }
            case "getFrozenCardsOnTable" -> gameActions.getFrozenCards(output, action,
                    gameBoard.getFrozenCards());
            case "getTotalGamesPlayed" -> gameActions.getTotalGamesPlayed(output, action,
                    playerOne.getGamesPlayed());
            case "getPlayerOneWins" -> gameActions.getGamesWon(output, action,
                    playerOne.getGamesWon());
            case "getPlayerTwoWins" -> gameActions.getGamesWon(output, action,
                    playerTwo.getGamesWon());
            default -> System.out.println("Invalid command");
        }
    }

    /**
     * Aceasta metoda itereaza prin toate actiunile unui joc si le executa
     * apeland functia anterioara.
     */
    public void doActions(final GameInput game, final ArrayNode output) {
        startRound(1);
        GameState gameState = new GameState(game.getStartGame().getStartingPlayer());

        for (int i = 0; i < game.getActions().size(); i++) {
            if (gameState.isGameEnd() && (game.getActions().get(i).getCommand()
                    .equals("useHeroAbility")
                    || game.getActions().get(i).getCommand().equals("useAttackHero")
                    || game.getActions().get(i).getCommand().equals("cardUsesAbility")
                    || game.getActions().get(i).getCommand().equals("cardUsesAttack")
                    || game.getActions().get(i).getCommand().equals("endPlayerTurn"))) {
                continue;
            }
            doAction(game, game.getActions().get(i), output, gameState);
        }
    }

    /**
     * Aceasta metoda itereaza prin toate jocurile din inputul unui test
     * si executa toate actiunile fiecarui joc apeland metoda anterioara.
     */
    public void play(final ArrayNode output) {
        for (int i = 0; i < input.getGames().size(); i++) {
            gameBoard = new GameBoard();
            playerOne.setMana(0);
            playerOne.setCardsInHand(new ArrayList<>());
            playerTwo.setMana(0);
            playerTwo.setCardsInHand(new ArrayList<>());

            gameBoard.initializeGameBoard();
            setPlayersHero(input.getGames().get(i).getStartGame().getPlayerOneHero(),
                    input.getGames().get(i).getStartGame().getPlayerTwoHero());
            initializePlayersDecks(input.getGames().get(i));

            doActions(input.getGames().get(i), output);
        }
    }
}
