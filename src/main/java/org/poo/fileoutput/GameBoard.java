package org.poo.fileoutput;

import org.poo.fileio.ActionsInput;
import org.poo.fileio.CardInput;

import java.util.ArrayList;

public final class GameBoard {
    private final ArrayList<ArrayList<CardProperties>> cardsOnTheBoard;
    private final int rowSize = 5;
    private final int playerOneFrontRow = 2;
    private final int playerOneBackRow = 3;
    private final int playerTwoFrontRow = 1;
    private final int playerTwoBackRow = 0;


    public GameBoard() {
        cardsOnTheBoard = new ArrayList<>();
    }

    public ArrayList<ArrayList<CardProperties>> getCardsOnTheBoard() {
        return cardsOnTheBoard;
    }

    /**
     * Aloca memorie pentru tabla de joc.
     */
    public void initializeGameBoard() {
        final int arraySize = 4;
        for (int i = 0; i < arraySize; i++) {
            ArrayList<CardProperties> row = new ArrayList<>(rowSize);
            cardsOnTheBoard.add(row);
        }
    }

    /**
     * Returneaza numarul randului de pe tabla pe care trebuie sa fie plasata
     * cartea din input, in functie de tipul acesteia si de jucator.
     */
    public int getRowNumber(final int handIdx, final int activePlayer, final Player player) {
        String row = "front row";
        if (player.getCardsInHand().get(handIdx).getCardInput().getName().equals("Sentinel")
                || player.getCardsInHand().get(handIdx).getCardInput().getName().equals("Berserker")
                || player.getCardsInHand().get(handIdx).getCardInput().getName()
                .equals("The Cursed One")
                || player.getCardsInHand().get(handIdx).getCardInput().getName()
                .equals("Disciple")) {
            row = "back row";
        }

        if (activePlayer == 1) {
            return row.equals("back row") ? playerOneBackRow : playerOneFrontRow;
        } else {
            return row.equals("back row") ? playerTwoBackRow : playerTwoFrontRow;
        }
    }

    /**
     * Plaseaza o carte pe tabla de joc.
     */
    public String placeCard(final int handIdx, final int activePlayer, final Player player) {
        if (player.getCardsInHand().size() <= handIdx) {
            return null;
        }

        if (player.getCardsInHand().get(handIdx).getCardInput().getMana() > player.getMana()) {
            return "Not enough mana to place card on table.";
        }

        int rowNumber = getRowNumber(handIdx, activePlayer, player);
        if (cardsOnTheBoard.get(rowNumber).size() == rowSize) {
            return "Cannot place card on table since row is full.";
        } else {
            cardsOnTheBoard.get(rowNumber).add(player.getCardsInHand().get(handIdx));
            player.setMana(player.getMana() - player.getCardsInHand().get(handIdx)
                    .getCardInput().getMana());
            player.getCardsInHand().remove(handIdx);
        }

        return null;
    }

    /**
     * Verifica daca o carte este de tip "Tank".
     */
    public boolean isTank(final int activePlayer) {
        for (CardProperties card : cardsOnTheBoard.get((activePlayer == 1)
                ? playerTwoFrontRow : playerOneFrontRow)) {
            if (card.getType().equals("Tank")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica daca o carte este a inamicului.
     */
    public boolean isEnemyCard(final ActionsInput action, final int activePlayer) {
        if (activePlayer == 1) {
            return action.getCardAttacked().getX() != playerOneFrontRow
                    && action.getCardAttacked().getX() != playerOneBackRow;
        } else {
            return action.getCardAttacked().getX() != playerTwoBackRow
                    && action.getCardAttacked().getX() != playerTwoFrontRow;
        }
    }

    /**
     * Verifica daca randul dat in input este al inamicului.
     */
    public boolean isEnemyRow(final int row, final int activePlayer) {
        if (activePlayer == 1) {
            return row == playerTwoBackRow || row == playerTwoFrontRow;
        } else {
            return row == playerOneFrontRow || row == playerOneBackRow;
        }
    }

    /**
     * Aceasta metoda implementeaza actiunea de a ataca o carte a inamicului.
     */
    public String attackCard(final ActionsInput action, final int activePlayer) {
        if (!isEnemyCard(action, activePlayer)) {
            return "Attacked card does not belong to the enemy.";
        }

        CardProperties attackerCard = cardsOnTheBoard.get(action.getCardAttacker().getX()).
                get(action.getCardAttacker().getY());
        if (attackerCard.isHasAttacked()) {
            return "Attacker card has already attacked this turn.";
        } else if (attackerCard.isFrozen()) {
            return "Attacker card is frozen.";
        }

        if (action.getCardAttacked().getY() >= cardsOnTheBoard.get(action.getCardAttacked()
                .getX()).size()) {
            return null;
        }

        CardProperties attackedCard = cardsOnTheBoard.get(action.getCardAttacked().getX()).
                get(action.getCardAttacked().getY());
        if (isTank(activePlayer) && !attackedCard.getType().equals("Tank")) {
            return "Attacked card is not of type 'Tank'.";
        }

        attackedCard.getCardInput().setHealth(attackedCard.getCardInput().getHealth()
                - attackerCard.getCardInput().getAttackDamage());
        if (attackedCard.getCardInput().getHealth() <= 0) {
            cardsOnTheBoard.get(action.getCardAttacked().getX())
                    .remove(action.getCardAttacked().getY());
        }

        attackerCard.setHasAttacked(true);
        return null;
    }

    /**
     * Aceasta metoda implemnteaza actiunea de a folosi abilitatea unei carti speciale.
     */
    public String useCardAbility(final ActionsInput action, final int activePlayer) {
        CardProperties cardAttacker = cardsOnTheBoard.get(action.getCardAttacker().getX()).
                get(action.getCardAttacker().getY());

        if (cardAttacker.isFrozen()) {
            return "Attacker card is frozen.";
        } else if (cardAttacker.isHasAttacked()) {
            return "Attacker card has already attacked this turn.";
        }

        CardProperties cardAttacked = cardsOnTheBoard.get(action.getCardAttacked().getX()).
                get(action.getCardAttacked().getY());
        if (cardAttacker.getCardInput().getName().equals("Disciple")) {
            if (isEnemyCard(action, activePlayer)) {
                return "Attacked card does not belong to the current player.";
            }
            cardAttacked.getCardInput().setHealth(cardAttacked.getCardInput().getHealth() + 2);
        } else {
            if (!isEnemyCard(action, activePlayer)) {
                return "Attacked card does not belong to the enemy.";
            } else if (isTank(activePlayer) && !cardAttacked.getType().equals("Tank")) {
                return "Attacked card is not of type 'Tank'.";
            } else if (cardAttacker.getCardInput().getName().equals("The Ripper")) {
                cardAttacked.getCardInput()
                        .setAttackDamage((cardAttacked.getCardInput().getAttackDamage() < 2)
                        ? 0 : (cardAttacked.getCardInput().getAttackDamage() - 2));
            } else if (cardAttacker.getCardInput().getName().equals("Miraj")) {
                int health = cardAttacked.getCardInput().getHealth();
                cardAttacked.getCardInput().setHealth(cardAttacker.getCardInput().getHealth());
                cardAttacker.getCardInput().setHealth(health);
            } else {
                int attack = cardAttacked.getCardInput().getAttackDamage();
                if (attack == 0) {
                    cardsOnTheBoard.get(action.getCardAttacked().getX())
                            .remove(action.getCardAttacked().getY());
                } else {
                    cardAttacked.getCardInput().setAttackDamage(cardAttacked.getCardInput()
                            .getHealth());
                    cardAttacked.getCardInput().setHealth(attack);
                }
            }
        }

        cardAttacker.setHasAttacked(true);
        return null;
    }

    /**
     * Aceasta metoda implementeaza actiunea de a ataca eroul inamicului.
     */
    public String attackHero(final ActionsInput action, final int activePlayer,
                             final Player attackedPlayer, final Player attacker,
                             final GameState gameState) {

        CardProperties cardAttacker = cardsOnTheBoard.get(action.getCardAttacker().getX()).
                get(action.getCardAttacker().getY());
        if (cardAttacker.isFrozen()) {
            return "Attacker card is frozen.";
        } else if (cardAttacker.isHasAttacked()) {
            return "Attacker card has already attacked this turn.";
        } else if (isTank(activePlayer)) {
            return "Attacked card is not of type 'Tank'.";
        }

        attackedPlayer.getHero().setHealth(attackedPlayer.getHero().getHealth()
                - cardAttacker.getCardInput().getAttackDamage());
        if (attackedPlayer.getHero().getHealth() <= 0) {
            attacker.setGamesWon(attacker.getGamesWon() + 1);
            attacker.setGamesPlayed(attacker.getGamesPlayed() + 1);
            attackedPlayer.setGamesPlayed(attacker.getGamesPlayed());
            gameState.setGameEnd(true);

            if (activePlayer == 1) {
                return "Player one killed the enemy hero.";
            }
            return "Player two killed the enemy hero.";
        }

        cardAttacker.setHasAttacked(true);
        return null;
    }

    /**
     * Aceasta metoda implementeaza actiunea de a folosi abilitatea unui erou.
     */
    public String useHeroAbility(final ActionsInput action, final int activePlayer,
                                 final Player attacker) {
        if (attacker.getMana() < attacker.getHero().getMana()) {
            return "Not enough mana to use hero's ability.";
        } else if (attacker.isHeroHasAttacked()) {
            return "Hero has already attacked this turn.";
        } else if (attacker.getHero().getName().equals("Lord Royce")
                || attacker.getHero().getName().equals("Empress Thorina")) {
            if (!isEnemyRow(action.getAffectedRow(), activePlayer)) {
                return "Selected row does not belong to the enemy.";
            } else if (attacker.getHero().getName().equals("Lord Royce")) {
                for (CardProperties card : cardsOnTheBoard.get(action.getAffectedRow())) {
                    card.setFrozen(true);
                    card.setFreezeCounter(card.getFreezeCounter() + 1);
                }
            } else {
                int maxHealth = 0, column = 0, iter = 0;
                for (CardProperties card : cardsOnTheBoard.get(action.getAffectedRow())) {
                    if (card.getCardInput().getHealth() > maxHealth) {
                        maxHealth = card.getCardInput().getHealth();
                        column = iter;
                    }
                    iter++;
                }
                if (!cardsOnTheBoard.get(action.getAffectedRow()).isEmpty()) {
                    cardsOnTheBoard.get(action.getAffectedRow()).remove(column);
                }
            }
        } else {
            if (isEnemyRow(action.getAffectedRow(), activePlayer)) {
                return "Selected row does not belong to the current player.";
            } else if (attacker.getHero().getName().equals("King Mudface")) {
                for (CardProperties card : cardsOnTheBoard.get(action.getAffectedRow())) {
                    card.getCardInput().setHealth(card.getCardInput().getHealth() + 1);
                }
            } else {
                for (CardProperties card : cardsOnTheBoard.get(action.getAffectedRow())) {
                    card.getCardInput().setAttackDamage(card.getCardInput().getAttackDamage() + 1);
                }
            }
        }

        attacker.setHeroHasAttacked(true);
        attacker.setMana(attacker.getMana() - attacker.getHero().getMana());
        return null;
    }

    /**
     * Aceasta metoda itereaza prin cartile de pe tabla
     * si le pune pe cele inghetate intr-un array pentru a le returna.
     */
    public ArrayList<CardInput> getFrozenCards() {
        ArrayList<CardInput> frozenCards = new ArrayList<>();
        for (ArrayList<CardProperties> cards : cardsOnTheBoard) {
            for (CardProperties card : cards) {
                if (card.isFrozen()) {
                    frozenCards.add(card.getCardInput());
                }
            }
        }
        return frozenCards;
    }
}
