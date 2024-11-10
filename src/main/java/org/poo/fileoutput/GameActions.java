package org.poo.fileoutput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.CardInput;

import java.util.ArrayList;

public final class GameActions {
    private final ObjectMapper objectMapper;

    public GameActions(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void getPlayerDeck(final ArrayNode output, final ActionsInput action,
                              final ArrayList<CardInput> deck) {
        Output decksOutput = new Output(action, deck);
        output.add(decksOutput.cardsToObjectNode(objectMapper));
    }

    public void getPlayerHero(final ArrayNode output, final ActionsInput action,
                              final CardInput hero) {
        Output heroOutput = new Output(action);
        output.add(heroOutput.heroToObjectNode(objectMapper, hero));
    }

    public void getPlayerTurn(final ArrayNode output, final ActionsInput action,
                              final int playerNumber) {
        Output turnOutput = new Output(action);
        output.add(turnOutput.playerTurnToObjectNode(objectMapper, playerNumber));
    }

    public void placeCardOnBoard(final ArrayNode output, final ActionsInput action,
                                 final String error) {
        Output placeCard = new Output(action, error);
        output.add(placeCard.placeCardToObjectNode(objectMapper));
    }

    public void getCardsInHand(final ArrayNode output, final ActionsInput action,
                               final ArrayList<CardProperties> cardsInHand) {
        ArrayList<CardInput> cardInputs = new ArrayList<>();
        for (CardProperties cards : cardsInHand) {
            cardInputs.add(cards.getCardInput());
        }
        Output decksOutput = new Output(action, cardInputs);
        output.add(decksOutput.cardsToObjectNode(objectMapper));
    }

    public void getPlayerMana(final ArrayNode output, final ActionsInput action,
                              final int playerMana) {
        Output playerManaOutput = new Output(action);
        output.add(playerManaOutput.playerManaToObjectNode(objectMapper, playerMana));
    }

    public void getCardsOnTable(final ArrayNode output, final ActionsInput action,
                                final ArrayList<ArrayList<CardProperties>> cardsOnBoard) {
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

    public void cardAttacks(final ArrayNode output, final ActionsInput action,
                            final String error) {
        Output attackCard = new Output(action, error);
        output.add(attackCard.attackCardToObjectNode(objectMapper));
    }

    public void getCardAtPosition(final ArrayNode output, final ActionsInput action,
                                  final CardInput card, final boolean isAvailable) {
        Output cardOutput = new Output(action);
        output.add(cardOutput.cardToObjectNode(objectMapper, isAvailable, card));
    }

    public void attackHero(final ArrayNode output, final ActionsInput action,
                           final String message) {
        Output attackHero = new Output(action, message);
        output.add(attackHero.attackedHeroToObjectNode(objectMapper));
    }

    public void useHeroAbility(final ArrayNode output, final ActionsInput action,
                               final String error) {
        Output heroOutput = new Output(action, error);
        output.add(heroOutput.useHeroToObjectNode(objectMapper));
    }

    public void getFrozenCards(final ArrayNode output, final ActionsInput action,
                               final ArrayList<CardInput> frozenCards) {
        Output frozenCardsOutput = new Output(action, frozenCards);
        output.add(frozenCardsOutput.cardsToObjectNode(objectMapper));
    }

    public void getTotalGamesPlayed(final ArrayNode output, final ActionsInput action,
                                    final int gamesPlayed) {
        Output gamesPlayedOutput = new Output(action);
        output.add(gamesPlayedOutput.gamesPlayedToObjectNode(objectMapper, gamesPlayed));
    }

    public void getGamesWon(final ArrayNode output, final ActionsInput action,
                            final int gamesWon) {
        Output gamesWonOutput = new Output(action);
        output.add(gamesWonOutput.gamesWon(objectMapper, gamesWon));
    }
}
