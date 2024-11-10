package org.poo.fileoutput;

public final class GameState {
    private int activePlayer;
    private int roundNumber;
    private boolean gameEnd;

    public GameState(final int activePlayer) {
        this.activePlayer = activePlayer;
        roundNumber = 1;
        gameEnd = false;
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(final int activePlayer) {
        this.activePlayer = activePlayer;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(final int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public boolean isGameEnd() {
        return gameEnd;
    }

    public void setGameEnd(final boolean gameEnd) {
        this.gameEnd = gameEnd;
    }
}
