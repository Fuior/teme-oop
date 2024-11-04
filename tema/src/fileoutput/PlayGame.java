package fileoutput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.CardInput;
import fileio.Input;

public class PlayGame {
    private Input input;
    private GameBoard gameBoard;
    private Player playerOne;
    private Player playerTwo;
    private GameActions gameActions;

    public PlayGame(Input input, ObjectMapper objectMapper) {
        this.input = input;
        gameBoard = new GameBoard();
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

    public void initializePlayersDecks(int gameIndex) {
        playerOne.suffleDeck(input.getPlayerOneDecks().getDecks()
                        .get(input.getGames().get(gameIndex).getStartGame().getPlayerOneDeckIdx()),
                        input.getGames().get(gameIndex).getStartGame().getShuffleSeed());

        playerTwo.suffleDeck(input.getPlayerTwoDecks().getDecks()
                        .get(input.getGames().get(gameIndex).getStartGame().getPlayerTwoDeckIdx()),
                input.getGames().get(gameIndex).getStartGame().getShuffleSeed());
    }

    public void startRound() {
        playerOne.setMana(1);
        if (!playerOne.getPlayerShuffledDeck().isEmpty()) {
            playerOne.getCardsInHand().add(playerOne.getPlayerShuffledDeck().get(0));
            playerOne.getPlayerShuffledDeck().remove(0);
        }

        playerTwo.setMana(1);
        if (!playerTwo.getPlayerShuffledDeck().isEmpty()) {
            playerTwo.getCardsInHand().add(playerTwo.getPlayerShuffledDeck().get(0));
            playerTwo.getPlayerShuffledDeck().remove(0);
        }
    }

    public void doActions(int index, ArrayNode output) {
        startRound();
        int activePlayer = input.getGames().get(index).getStartGame().getStartingPlayer();

        for (int i = 0; i < input.getGames().get(index).getActions().size(); i++) {

            String action = input.getGames().get(index).getActions().get(i).getCommand();

            if (action.equals("endPlayerTurn")) {
                if (activePlayer == 1) {
                    playerOne.setTurnFinished(true);
                    activePlayer = 2;
                } else {
                    playerTwo.setTurnFinished(true);
                    activePlayer = 1;
                }
                if (!playerOne.isTurnFinished() && !playerTwo.isTurnFinished()) {
                    startRound();
                }
            } else if (action.equals("getPlayerDeck")) {
                int playerIdx = input.getGames().get(index).getActions().get(i).getPlayerIdx();
                output.add(gameActions.getPlayerDeck(action, playerIdx,
                        (playerIdx == 1) ? playerOne.getPlayerShuffledDeck() : playerTwo.getPlayerShuffledDeck()));
            } else if (action.equals("getPlayerHero")) {
                int playerIdx = input.getGames().get(index).getActions().get(i).getPlayerIdx();
                output.add(gameActions.getPlayerHero(action, playerIdx,
                        (playerIdx == 1) ? input.getGames().get(index).getStartGame().getPlayerOneHero() :
                                input.getGames().get(index).getStartGame().getPlayerTwoHero()));
            } else if (action.equals("getPlayerTurn")) {
                output.add(gameActions.getPlayerTurn(action, activePlayer));
            }
        }
    }

    public void play(ArrayNode output) {
        for (int i = 0; i < input.getGames().size(); i++) {
            setPlayersHero(input.getGames().get(i).getStartGame().getPlayerOneHero(),
                    input.getGames().get(i).getStartGame().getPlayerTwoHero());
            initializePlayersDecks(i);
            doActions(i, output);
        }
    }
}
