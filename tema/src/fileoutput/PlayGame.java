package fileoutput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.ActionsInput;
import fileio.CardInput;
import fileio.GameInput;
import fileio.Input;

import java.util.ArrayList;

public class PlayGame {
    private Input input;
    private GameBoard gameBoard;
    private Player playerOne;
    private Player playerTwo;
    private GameActions gameActions;

    public PlayGame(Input input, ObjectMapper objectMapper) {
        this.input = input;
        playerOne = new Player();
        playerTwo = new Player();
        gameActions = new GameActions(objectMapper);
    }

    public void setPlayersHero(CardInput playerOneHero, CardInput playerTwoHero) {
        playerOne.setHero(playerOneHero);
        playerOne.getHero().setHealth(30);
        playerTwo.setHero(playerTwoHero);
        playerTwo.getHero().setHealth(30);
    }

    public void initializePlayersDecks(GameInput game) {
        playerOne.suffleDeck(input.getPlayerOneDecks().getDecks().
                        get(game.getStartGame().getPlayerOneDeckIdx()),
                        game.getStartGame().getShuffleSeed());

        playerTwo.suffleDeck(input.getPlayerTwoDecks().getDecks().
                        get(game.getStartGame().getPlayerTwoDeckIdx()),
                        game.getStartGame().getShuffleSeed());
    }

    public void startRound(int roundNumber) {
        playerOne.setTurnFinished(false);
        playerOne.setMana(playerOne.getMana() + (Math.min(roundNumber, 10)));
        if (!playerOne.getPlayerShuffledDeck().isEmpty()) {
            playerOne.getCardsInHand().add(new CardProperties(playerOne.getPlayerShuffledDeck().get(0)));
            playerOne.getPlayerShuffledDeck().remove(0);
        }

        playerTwo.setTurnFinished(false);
        playerTwo.setMana(playerTwo.getMana() + (Math.min(roundNumber, 10)));
        if (!playerTwo.getPlayerShuffledDeck().isEmpty()) {
            playerTwo.getCardsInHand().add(new CardProperties(playerTwo.getPlayerShuffledDeck().get(0)));
            playerTwo.getPlayerShuffledDeck().remove(0);
        }
    }

    public void endPlayerTurn(Player player, int row) {
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

    public void doAction(GameInput game, ActionsInput action, ArrayNode output, GameState gameState) {
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
            case "getPlayerDeck" -> gameActions.getPlayerDeck(output, action, (action.getPlayerIdx() == 1) ?
                    playerOne.getPlayerShuffledDeck() : playerTwo.getPlayerShuffledDeck());
            case "getPlayerHero" -> gameActions.getPlayerHero(output, action, (action.getPlayerIdx() == 1) ?
                    game.getStartGame().getPlayerOneHero() : game.getStartGame().getPlayerTwoHero());
            case "getPlayerTurn" -> gameActions.getPlayerTurn(output, action, gameState.getActivePlayer());
            case "placeCard" -> {
                String result = gameBoard.placeCard(action.getHandIdx(), gameState.getActivePlayer(),
                        (gameState.getActivePlayer() == 1) ? playerOne : playerTwo);
                if (result != null) {
                    gameActions.placeCardOnBoard(output, action, result);
                }
            }
            case "getCardsInHand" -> gameActions.getCardsInHand(output, action, (action.getPlayerIdx() == 1) ?
                    playerOne.getCardsInHand() : playerTwo.getCardsInHand());
            case "getPlayerMana" -> gameActions.getPlayerMana(output, action, (action.getPlayerIdx() == 1) ?
                    playerOne.getMana() : playerTwo.getMana());
            case "getCardsOnTable" ->
                    gameActions.getCardsOnTable(output, action, gameBoard.getCardsOnTheBoard());
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
                    card = gameBoard.getCardsOnTheBoard().get(action.getX()).get(action.getY()).getCardInput();
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
            case "getFrozenCardsOnTable" -> gameActions.getFrozenCards(output, action, gameBoard.getFrozenCards());
            case "getTotalGamesPlayed" -> gameActions.getTotalGamesPlayed(output, action, playerOne.getGamesPlayed());
            case "getPlayerOneWins" -> gameActions.getGamesWon(output, action, playerOne.getGamesWon());
            case "getPlayerTwoWins" -> gameActions.getGamesWon(output, action, playerTwo.getGamesWon());
        }
    }

    public void doActions(GameInput game, ArrayNode output) {
        startRound(1);
        GameState gameState = new GameState(game.getStartGame().getStartingPlayer());

        for (int i = 0; i < game.getActions().size(); i++) {
            if (gameState.isGameEnd() &&
                    (game.getActions().get(i).getCommand().equals("useHeroAbility") ||
                    game.getActions().get(i).getCommand().equals("useAttackHero") ||
                    game.getActions().get(i).getCommand().equals("cardUsesAbility") ||
                    game.getActions().get(i).getCommand().equals("cardUsesAttack") ||
                    game.getActions().get(i).getCommand().equals("endPlayerTurn"))) {
                continue;
            }
            doAction(game, game.getActions().get(i), output, gameState);
        }
    }

    public void play(ArrayNode output) {
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
