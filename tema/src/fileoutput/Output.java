package fileoutput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.ActionsInput;
import fileio.CardInput;
import fileio.Coordinates;

import java.util.ArrayList;

public class Output {
    private ActionsInput action;
    private ArrayList<CardInput> cards;
    private String message;

    public Output(ActionsInput action) {
        this.action = action;
    }

    public Output(ActionsInput action, ArrayList<CardInput> cards) {
        this(action);
        this.cards = cards;
    }

    public Output(ActionsInput action, String message) {
        this(action);
        this.message = message;
    }

    public ObjectNode cardToObjectNode(ObjectMapper objectMapper, CardInput card) {
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

    public ArrayNode cardsToArrayNode(ObjectMapper objectMapper) {
        ArrayNode cardArray = objectMapper.createArrayNode();
        for (CardInput card : cards) {
            ObjectNode cardNode = cardToObjectNode(objectMapper, card);
            cardArray.add(cardNode);
        }
        return cardArray;
    }

    public ArrayNode cardsToArrayNode(ObjectMapper objectMapper, ArrayList<ArrayList<CardInput>> cardsOnTheBoard) {
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

    public ObjectNode cardsToObjectNode(ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        if (!action.getCommand().equals("getFrozenCardsOnTable")) {
            node.put("playerIdx", action.getPlayerIdx());
        }
        node.set("output", cardsToArrayNode(objectMapper));
        return node;
    }

    public ObjectNode cardsToObjectNode(ObjectMapper objectMapper, ArrayList<ArrayList<CardInput>> cardsOnTheBoard) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        node.set("output", cardsToArrayNode(objectMapper, cardsOnTheBoard));
        return node;
    }

    public ObjectNode cardToObjectNode(ObjectMapper objectMapper, boolean isAvailable, CardInput card) {
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

    public ObjectNode placeCardToObjectNode(ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        node.put("handIdx", action.getHandIdx());
        node.put("error", message);
        return node;
    }

    public ObjectNode coordinatesToObjectNode(ObjectMapper objectMapper, Coordinates coordinates) {
        ObjectNode cardNode = objectMapper.createObjectNode();
        cardNode.put("x", coordinates.getX());
        cardNode.put("y", coordinates.getY());
        return cardNode;
    }

    public ObjectNode attackCardToObjectNode(ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        node.set("cardAttacker", coordinatesToObjectNode(objectMapper, action.getCardAttacker()));
        node.set("cardAttacked", coordinatesToObjectNode(objectMapper, action.getCardAttacked()));
        node.put("error", message);
        return node;
    }

    public ObjectNode heroToObjectNode(ObjectMapper objectMapper, CardInput hero) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        node.put("playerIdx", action.getPlayerIdx());
        node.set("output", cardToObjectNode(objectMapper, hero));
        return node;
    }

    public ObjectNode useHeroToObjectNode(ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        node.put("affectedRow", action.getAffectedRow());
        node.put("error", message);
        return node;
    }

    public ObjectNode attackedHeroToObjectNode(ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.createObjectNode();
        if (message.equals("Player one killed the enemy hero.") ||
                message.equals("Player two killed the enemy hero.")) {
            node.put("gameEnded", message);
        } else {
            node.put("command", action.getCommand());
            node.set("cardAttacker", coordinatesToObjectNode(objectMapper, action.getCardAttacker()));
            node.put("error", message);
        }
        return node;
    }

    public ObjectNode playerManaToObjectNode(ObjectMapper objectMapper, int playerMana) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        node.put("playerIdx", action.getPlayerIdx());
        node.put("output", playerMana);
        return node;
    }

    public ObjectNode playerTurnToObjectNode(ObjectMapper objectMapper, int playerNumber) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", action.getCommand());
        node.put("output", playerNumber);
        return node;
    }
}
