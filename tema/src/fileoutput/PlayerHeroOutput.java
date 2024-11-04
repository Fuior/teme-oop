package fileoutput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;

public class PlayerHeroOutput {
    private String command;
    private int playerIdx;
    private CardInput hero;

    public PlayerHeroOutput(String command, int playerIdx, CardInput hero) {
        this.command = command;
        this.playerIdx = playerIdx;
        this.hero = hero;
    }

    public ObjectNode heroCardToObjectNode(ObjectMapper objectMapper, CardInput card) {
        ObjectNode cardNode = objectMapper.createObjectNode();
        cardNode.put("mana", card.getMana());
        cardNode.put("description", card.getDescription());

        ArrayNode colorsArray = objectMapper.createArrayNode();
        for (String color : card.getColors()) {
            colorsArray.add(color);
        }
        cardNode.set("colors", colorsArray);
        cardNode.put("name", card.getName());
        cardNode.put("health", card.getHealth());

        return cardNode;
    }

    public ObjectNode heroToObjectNode(ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", command);
        node.put("playerIdx", playerIdx);
        node.set("output", heroCardToObjectNode(objectMapper, hero));
        return node;
    }
}
