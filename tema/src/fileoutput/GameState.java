package fileoutput;

public class GameState {
    private int activePlayer;
    private int roundNumber;

    public GameState(int activePlayer) {
        this.activePlayer = activePlayer;
        roundNumber = 1;
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(int activePlayer) {
        this.activePlayer = activePlayer;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }
}
