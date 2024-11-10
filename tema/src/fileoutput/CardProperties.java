package fileoutput;

import fileio.CardInput;

public class CardProperties {
    private final CardInput cardInput;
    private final String type;
    private boolean isFrozen;
    private int freezeCounter;
    private boolean hasAttacked;

    public CardProperties(CardInput cardInput) {
        this.cardInput = cardInput;
        isFrozen = false;
        freezeCounter = 0;
        hasAttacked = false;
        if (cardInput.getName().equals("Goliath") || cardInput.getName().equals("Warden")) {
            type = "Tank";
        } else {
            type = "Card is not Tank";
        }
    }

    public CardInput getCardInput() {
        return cardInput;
    }

    public String getType() {
        return type;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }

    public int getFreezeCounter() {
        return freezeCounter;
    }

    public void setFreezeCounter(int freezeCounter) {
        this.freezeCounter = freezeCounter;
    }

    public boolean isHasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }
}
