package fileoutput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;

import java.util.ArrayList;

public class GameActions {
    private ObjectMapper objectMapper;

    public GameActions(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ObjectNode getPlayerDeck(String command, int playerIdx, ArrayList<CardInput> deck) {
        DecksOutput decksOutput = new DecksOutput(command, playerIdx, deck);
        return decksOutput.deckToObjectNode(objectMapper);
    }

    public ObjectNode getPlayerHero(String command, int playerIdx, CardInput hero) {
        PlayerHeroOutput heroOutput = new PlayerHeroOutput(command, playerIdx, hero);
        return heroOutput.heroToObjectNode(objectMapper);
    }

    public ObjectNode getPlayerTurn(String command, int playerNumber) {
        PlayerTurnOutput turnOutput = new PlayerTurnOutput(command, playerNumber);
        return turnOutput.playerTurnToObjectNode(objectMapper);
    }
}
