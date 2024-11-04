package fileoutput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PlayerTurnOutput {
    private String command;
    private int playerNumber;

    public PlayerTurnOutput(String command, int playerNumber) {
        this.command = command;
        this.playerNumber = playerNumber;
    }

    public ObjectNode playerTurnToObjectNode(ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", command);
        node.put("output", playerNumber);
        return node;
    }
}
