package org.poo.fileoutput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.ActionsInput;
import org.poo.fileio.CardInput;
import org.poo.fileio.Coordinates;

import java.util.ArrayList;

public final class Output {
    private final ActionsInput action;
    private ArrayList<CardInput> cards;
    private String message;

    public Output(final ActionsInput action) {
        this.action = action;
    }

    public Output(final ActionsInput action, final ArrayList<CardInput> cards) {
        this(action);
        this.cards = cards;
    }

    public Output(final ActionsInput action, final String message) {
        this(action);
        this.message = message;
    }


    /**
     * Aceasta metoda pune elemtele unei carti intr-un ObjectNode.
     */
    public ObjectNode cardToObjectNode(final ObjectMapper objectMapper, final CardInput card) {
        ObjectNode cardNode = objectMapper.createObjectNode();

        cardNode.put("mana", card.getMana());
        if (!action.getCommand().equals("getPlayerHero")) {
            cardNode.put("attackDamage", card.getAttackDamage());
            cardNode.put("health", card.getHealth());
        }
        cardNode.put("description", card.getDescription());

        ArrayNode colorsArray = objectMapper.createArrayNode();
        for (String color : card.getColors()) {
            colorsArray.add(color);
        }
        cardNode.set("colors", colorsArray);
        cardNode.put("name", card.getName());
        if (action.getCommand().equals("getPlayerHero")) {
            cardNode.put("health", card.getHealth());
        }

        return cardNode;
    }

    /**
     * Aceasta metoda itereaza printr-un array de carti si le pune intr-un ArrayNode.
     */
    public ArrayNode cardsToArrayNode(final ObjectMapper objectMapper) {
        ArrayNode cardArray = objectMapper.createArrayNode();
        for (CardInput card : cards) {
            ObjectNode cardNode = cardToObjectNode(objectMapper, card);
            cardArray.add(cardNode);
        }
        return cardArray;
    }

    /**
     * Aceasta metoda itereaza prin cartile de pe tabla de joc. Extrage campul de tip
     * CardInput al fiecarei carti si le pune intr-un ArrayNode.
     */
    public ArrayNode cardsToArrayNode(final ObjectMapper objectMapper,
                                      final ArrayList<ArrayList<CardInput>> cardsOnTheBoard) {
        ArrayNode cardArray = objectMapper.createArrayNode();
        for (ArrayList<CardInput> row : cardsOnTheBoard) {
            ArrayNode rowArrayNode = objectMapper.createArrayNode();
            for (CardInput card : row) {
                ObjectNode cardNode = cardToObjectNode(objectMapper, card);
                rowArrayNode.add(cardNode);
            }
            cardArray.add(rowArrayNode);
        }
        return cardArray;
    }

    /**
     * Aceasta metoda pune un array de carti intr-un ObjectNode si
     * seteaza si alti parametri in functie de comanda efectuata.
     * Genereaza outputul pentru comenzile: "getPlayerDeck",
     * "getCardsInHand", "getFrozenCardsOnTable".
     */
    public ObjectNode cardsToObjectNode(final ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        if (!action.getCommand().equals("getFrozenCardsOnTable")) {
            node.put("playerIdx", action.getPlayerIdx());
        }
        node.set("output", cardsToArrayNode(objectMapper));
        return node;
    }

    /**
     * Aceasta metoda pune cartile de pe tabla intr-un ObjectNode.
     * Genereaza outputul pentru comanda: "getCardsOnTable".
     */
    public ObjectNode cardsToObjectNode(final ObjectMapper objectMapper,
                                        final ArrayList<ArrayList<CardInput>> cardsOnTheBoard) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        node.set("output", cardsToArrayNode(objectMapper, cardsOnTheBoard));
        return node;
    }

    /**
     * Aceasta metoda pune o carte intr-un ObjectNode.
     * Este folosita pentru comanda: "getCardAtPosition".
     */
    public ObjectNode cardToObjectNode(final ObjectMapper objectMapper, final boolean isAvailable,
                                       final CardInput card) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        node.put("x", action.getX());
        node.put("y", action.getY());
        if (isAvailable) {
            node.set("output", cardToObjectNode(objectMapper, card));
        } else {
            node.put("output", "No card available at that position.");
        }
        return node;
    }

    /**
     * Aceasta metoda genereaza outputul pentru comanda: "placeCard".
     */
    public ObjectNode placeCardToObjectNode(final ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        node.put("handIdx", action.getHandIdx());
        node.put("error", message);
        return node;
    }

    /**
     * Aceasta metoda pune coordonatele unei carti intr-un ObjectNode.
     */
    public ObjectNode coordinatesToObjectNode(final ObjectMapper objectMapper,
                                              final Coordinates coordinates) {
        ObjectNode cardNode = objectMapper.createObjectNode();
        cardNode.put("x", coordinates.getX());
        cardNode.put("y", coordinates.getY());
        return cardNode;
    }

    /**
     * Aceasta metoda genereaza outputul pentru comenzile: "cardUsesAttack",
     * "cardUsesAbility".
     */
    public ObjectNode attackCardToObjectNode(final ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        node.set("cardAttacker", coordinatesToObjectNode(objectMapper, action.getCardAttacker()));
        node.set("cardAttacked", coordinatesToObjectNode(objectMapper, action.getCardAttacked()));
        node.put("error", message);
        return node;
    }

    /**
     * Aceasta metoda genereaza outputul pentru comanda: "getPlayerHero".
     */
    public ObjectNode heroToObjectNode(final ObjectMapper objectMapper, final CardInput hero) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        node.put("playerIdx", action.getPlayerIdx());
        node.set("output", cardToObjectNode(objectMapper, hero));
        return node;
    }

    /**
     * Aceasta metoda genereaza outputul pentru comanda: "useHeroAbility".
     */
    public ObjectNode useHeroToObjectNode(final ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        node.put("affectedRow", action.getAffectedRow());
        node.put("error", message);
        return node;
    }

    /**
     * Aceasta metoda genereaza outputul pentru comanda: "useAttackHero".
     */
    public ObjectNode attackedHeroToObjectNode(final ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.createObjectNode();
        if (message.equals("Player one killed the enemy hero.")
                || message.equals("Player two killed the enemy hero.")) {
            node.put("gameEnded", message);
        } else {
            node.put("command", action.getCommand());
            node.set("cardAttacker", coordinatesToObjectNode(objectMapper,
                    action.getCardAttacker()));
            node.put("error", message);
        }
        return node;
    }

    /**
     * Aceasta metoda genereaza outputul pentru comanda: "getPlayerMana".
     */
    public ObjectNode playerManaToObjectNode(final ObjectMapper objectMapper,
                                             final int playerMana) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        node.put("playerIdx", action.getPlayerIdx());
        node.put("output", playerMana);
        return node;
    }

    /**
     * Aceasta metoda genereaza outputul pentru comanda: "getPlayerTurn".
     */
    public ObjectNode playerTurnToObjectNode(final ObjectMapper objectMapper,
                                             final int playerNumber) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        node.put("output", playerNumber);
        return node;
    }

    /**
     * Aceasta metoda genereaza outputul pentru comanda: "getTotalGamesPlayed".
     */
    public ObjectNode gamesPlayedToObjectNode(final ObjectMapper objectMapper,
                                              final int gamesPlayed) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        node.put("output", gamesPlayed);
        return node;
    }

    /**
     * Aceasta metoda genereaza outputul pentru comenzile: "getPlayerOneWins",
     * "getPlayerTwoWins".
     */
    public ObjectNode gamesWon(final ObjectMapper objectMapper, final int gamesWon) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        node.put("output", gamesWon);
        return node;
    }
}
