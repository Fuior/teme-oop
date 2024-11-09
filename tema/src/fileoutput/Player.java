package fileoutput;

import fileio.CardInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Player {
    private CardInput hero;
    private boolean heroHasAttacked;
    private ArrayList<CardInput> playerShuffledDeck;
    private ArrayList<CardProperties> cardsInHand;
    private int mana;
    private int gamesWon;
    private int gamesPlayed;
    private boolean turnFinished;

    public Player() {
        heroHasAttacked = false;
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

    public boolean isHeroHasAttacked() {
        return heroHasAttacked;
    }

    public void setHeroHasAttacked(boolean heroHasAttacked) {
        this.heroHasAttacked = heroHasAttacked;
    }

    public void setCardsInHand(ArrayList<CardProperties> cardsInHand) {
        this.cardsInHand = cardsInHand;
    }

    public ArrayList<CardProperties> getCardsInHand() {
        return cardsInHand;
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
        playerShuffledDeck = new ArrayList<>();
        for (CardInput card : playerDeck) {
            CardInput copiedCard = new CardInput();
            copiedCard.setMana(card.getMana());
            copiedCard.setAttackDamage(card.getAttackDamage());
            copiedCard.setHealth(card.getHealth());
            copiedCard.setDescription(card.getDescription());
            copiedCard.setColors(new ArrayList<>(card.getColors()));
            copiedCard.setName(card.getName());
            playerShuffledDeck.add(copiedCard);
        }
        Random random = new Random(seed);
        Collections.shuffle(playerShuffledDeck, random);
    }

    public ArrayList<CardInput> getPlayerShuffledDeck() {
        return playerShuffledDeck;
    }
}
