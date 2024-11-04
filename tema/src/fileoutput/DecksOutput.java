package fileoutput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;

import java.util.ArrayList;

public class DecksOutput {
    private String command;
    private int playerIdx;
    private ArrayList<CardInput> deck;

    public DecksOutput(String command, int playerIdx, ArrayList<CardInput> deck) {
        this.command = command;
        this.playerIdx = playerIdx;
        this.deck = deck;
    }

    public ObjectNode cardToObjectNode(ObjectMapper objectMapper, CardInput card) {
        ObjectNode cardNode = objectMapper.createObjectNode();
        cardNode.put("mana", card.getMana());
        cardNode.put("attackDamage", card.getAttackDamage());
        cardNode.put("health", card.getHealth());
        cardNode.put("description", card.getDescription());

        ArrayNode colorsArray = objectMapper.createArrayNode();
        for (String color : card.getColors()) {
            colorsArray.add(color);
        }
        cardNode.set("colors", colorsArray);
        cardNode.put("name", card.getName());

        return cardNode;
    }

    public ArrayNode deckToArrayNode(ObjectMapper objectMapper) {
        ArrayNode cardArray = objectMapper.createArrayNode();
        for (CardInput card : deck) {
            ObjectNode cardNode = cardToObjectNode(objectMapper, card);
            cardArray.add(cardNode);
        }
        return cardArray;
    }

    public ObjectNode deckToObjectNode(ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", command);
        node.put("playerIdx", playerIdx);
        node.set("output", deckToArrayNode(objectMapper));
        return node;
    }
}
