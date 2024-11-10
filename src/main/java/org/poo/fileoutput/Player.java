package org.poo.fileoutput;

import org.poo.fileio.CardInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public final class Player {
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

    public void setHero(final CardInput hero) {
        this.hero = hero;
    }

    public boolean isHeroHasAttacked() {
        return heroHasAttacked;
    }

    public void setHeroHasAttacked(final boolean heroHasAttacked) {
        this.heroHasAttacked = heroHasAttacked;
    }

    public void setCardsInHand(final ArrayList<CardProperties> cardsInHand) {
        this.cardsInHand = cardsInHand;
    }

    public ArrayList<CardProperties> getCardsInHand() {
        return cardsInHand;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(final int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(final int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public boolean isTurnFinished() {
        return turnFinished;
    }

    public void setTurnFinished(final boolean turnFinished) {
        this.turnFinished = turnFinished;
    }

    /**
     * Aceasta metoda primeste la input deck-ul cu care vrea sa joace
     * jucatorul, ii face o copie si amesteca cartile in copie.
     * Actiunile din timpul jocului se vor face pe cartile din copie,
     * fara sa afecteze deck-ul initial.
     */
    public void suffleDeck(final ArrayList<CardInput> playerDeck, final int seed) {
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
