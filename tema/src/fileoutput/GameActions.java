package fileoutput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.ActionsInput;
import fileio.CardInput;

import java.util.ArrayList;

public class GameActions {
    private final ObjectMapper objectMapper;

    public GameActions(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void getPlayerDeck(ArrayNode output, ActionsInput action, ArrayList<CardInput> deck) {
        Output decksOutput = new Output(action, deck);
        output.add(decksOutput.cardsToObjectNode(objectMapper));
    }

    public void getPlayerHero(ArrayNode output, ActionsInput action, CardInput hero) {
        Output heroOutput = new Output(action);
        output.add(heroOutput.heroToObjectNode(objectMapper, hero));
    }

    public void getPlayerTurn(ArrayNode output, ActionsInput action, int playerNumber) {
        Output turnOutput = new Output(action);
        output.add(turnOutput.playerTurnToObjectNode(objectMapper, playerNumber));
    }

    public void placeCardOnBoard(ArrayNode output, ActionsInput action, String error) {
        Output placeCard = new Output(action, error);
        output.add(placeCard.placeCardToObjectNode(objectMapper));
    }

    public void getCardsInHand(ArrayNode output, ActionsInput action, ArrayList<CardProperties> cardsInHand) {
        ArrayList<CardInput> cardInputs = new ArrayList<>();
        for (CardProperties cards : cardsInHand) {
            cardInputs.add(cards.getCardInput());
        }
        Output decksOutput = new Output(action, cardInputs);
        output.add(decksOutput.cardsToObjectNode(objectMapper));
    }

    public void getPlayerMana(ArrayNode output, ActionsInput action, int playerMana) {
        Output playerManaOutput = new Output(action);
        output.add(playerManaOutput.playerManaToObjectNode(objectMapper, playerMana));
    }

    public void getCardsOnTable(ArrayNode output, ActionsInput action,
                                ArrayList<ArrayList<CardProperties>> cardsOnBoard) {
        ArrayList<ArrayList<CardInput>> cardInputs = new ArrayList<>();
        for (ArrayList<CardProperties> cardProperties : cardsOnBoard) {
            ArrayList<CardInput> row = new ArrayList<>();
            for (CardProperties cards : cardProperties) {
                row.add(cards.getCardInput());
            }
            cardInputs.add(row);
        }
        Output cardsOnTable = new Output(action);
        output.add(cardsOnTable.cardsToObjectNode(objectMapper, cardInputs));
    }

    public void cardAttacks(ArrayNode output, ActionsInput action, String error) {
        Output attackCard = new Output(action, error);
        output.add(attackCard.attackCardToObjectNode(objectMapper));
    }

    public void getCardAtPosition(ArrayNode output, ActionsInput action, CardInput card, boolean isAvailable) {
        Output cardOutput = new Output(action);
        output.add(cardOutput.cardToObjectNode(objectMapper, isAvailable, card));
    }

    public void attackHero(ArrayNode output, ActionsInput action, String message) {
        Output attackHero = new Output(action, message);
        output.add(attackHero.attackedHeroToObjectNode(objectMapper));
    }

    public void useHeroAbility(ArrayNode output, ActionsInput action, String error) {
        Output heroOutput = new Output(action, error);
        output.add(heroOutput.useHeroToObjectNode(objectMapper));
    }

    public void getFrozenCards(ArrayNode output, ActionsInput action, ArrayList<CardInput> frozenCards) {
        Output frozenCardsOutput = new Output(action, frozenCards);
        output.add(frozenCardsOutput.cardsToObjectNode(objectMapper));
    }

    public void getTotalGamesPlayed(ArrayNode output, ActionsInput action, int gamesPlayed) {
        Output gamesPlayedOutput = new Output(action);
        output.add(gamesPlayedOutput.gamesPlayedToObjectNode(objectMapper, gamesPlayed));
    }

    public void getGamesWon(ArrayNode output, ActionsInput action, int gamesWon) {
        Output gamesWonOutput = new Output(action);
        output.add(gamesWonOutput.gamesWon(objectMapper, gamesWon));
    }
}
