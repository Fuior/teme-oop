package fileoutput;

import fileio.CardInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Player {
    private CardInput hero;
    private ArrayList<CardInput> playerShuffledDeck;
    private ArrayList<CardInput> cardsInHand;
    private int mana;
    private int gamesWon;
    private int gamesPlayed;
    private boolean turnFinished;

    public Player() {
        cardsInHand = new ArrayList<>();
        mana = 0;
        gamesWon = 0;
        gamesPlayed = 0;
        turnFinished = false;
    }

    public CardInput getHero() {
        return hero;
    }

    public void setHero(CardInput hero) {
        this.hero = hero;
    }

    public ArrayList<CardInput> getCardsInHand() {
        return cardsInHand;
    }

    public void setCardsInHand(ArrayList<CardInput> cardsInHand) {
        this.cardsInHand = cardsInHand;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public boolean isTurnFinished() {
        return turnFinished;
    }

    public void setTurnFinished(boolean turnFinished) {
        this.turnFinished = turnFinished;
    }

    public void suffleDeck(ArrayList<CardInput> playerDeck, int seed) {
        playerShuffledDeck = new ArrayList<>(playerDeck);
        Random random = new Random(seed);
        Collections.shuffle(playerShuffledDeck, random);
    }

    public ArrayList<CardInput> getPlayerShuffledDeck() {
        return playerShuffledDeck;
    }
}
