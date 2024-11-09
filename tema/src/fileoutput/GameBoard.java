package fileoutput;

import fileio.ActionsInput;
import fileio.CardInput;

import java.util.ArrayList;

public class GameBoard {
    private ArrayList<ArrayList<CardProperties>> cardsOnTheBoard;

    public GameBoard() {
        cardsOnTheBoard = new ArrayList<>();
    }

    public ArrayList<ArrayList<CardProperties>> getCardsOnTheBoard() {
        return cardsOnTheBoard;
    }

    public void initializeGameBoard() {
        for (int i = 0; i < 4; i++) {
            ArrayList<CardProperties> row = new ArrayList<>(5);
            cardsOnTheBoard.add(row);
        }
    }

    public int getRowNumber(int handIdx, int activePlayer, Player player) {
        String row = "front row";
        if (player.getCardsInHand().get(handIdx).getCardInput().getName().equals("Sentinel") ||
                player.getCardsInHand().get(handIdx).getCardInput().getName().equals("Berserker") ||
                player.getCardsInHand().get(handIdx).getCardInput().getName().equals("The Cursed One") ||
                player.getCardsInHand().get(handIdx).getCardInput().getName().equals("Disciple")) {
            row = "back row";
        }

        if (activePlayer == 1) {
            return row.equals("back row") ? 3 : 2;
        } else {
            return row.equals("back row") ? 0 : 1;
        }
    }

    public String placeCard(int handIdx, int activePlayer, Player player) {
        if (player.getCardsInHand().size() <= handIdx) {
            return null;
        }

        if (player.getCardsInHand().get(handIdx).getCardInput().getMana() > player.getMana()) {
            return "Not enough mana to place card on table.";
        }

        int rowNumber = getRowNumber(handIdx, activePlayer, player);
        if (cardsOnTheBoard.get(rowNumber).size() == 5) {
            return "Cannot place card on table since row is full.";
        } else {
            cardsOnTheBoard.get(rowNumber).add(player.getCardsInHand().get(handIdx));
            player.setMana(player.getMana() - player.getCardsInHand().get(handIdx).getCardInput().getMana());
            player.getCardsInHand().remove(handIdx);
        }

        return null;
    }

    public boolean isTank(int activePlayer) {
        for (CardProperties card : cardsOnTheBoard.get((activePlayer == 1) ? 1 : 2)) {
            if (card.getType().equals("Tank")) {
                return true;
            }
        }
        return false;
    }

    public boolean isEnemyCard(ActionsInput action, int activePlayer) {
        if (activePlayer == 1) {
            return action.getCardAttacked().getX() != 2 && action.getCardAttacked().getX() != 3;
        } else {
            return action.getCardAttacked().getX() != 0 && action.getCardAttacked().getX() != 1;
        }
    }

    public boolean isEnemyRow(int row, int activePlayer) {
        if (activePlayer == 1) {
            return row == 0 || row == 1;
        } else {
            return row == 2 || row == 3;
        }
    }

    public String attackCard(ActionsInput action, int activePlayer) {
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

        if (action.getCardAttacked().getY() >= cardsOnTheBoard.get(action.getCardAttacked().getX()).size()) {
            return null;
        }

        CardProperties attackedCard = cardsOnTheBoard.get(action.getCardAttacked().getX()).
                get(action.getCardAttacked().getY());
        if (isTank(activePlayer) && !attackedCard.getType().equals("Tank")) {
            return "Attacked card is not of type 'Tank'.";
        }

        attackedCard.getCardInput().setHealth(attackedCard.getCardInput().getHealth() -
                attackerCard.getCardInput().getAttackDamage());
        if (attackedCard.getCardInput().getHealth() <= 0) {
            cardsOnTheBoard.get(action.getCardAttacked().getX()).remove(action.getCardAttacked().getY());
        }

        attackerCard.setHasAttacked(true);
        return null;
    }

    public String useCardAbility(ActionsInput action, int activePlayer) {
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
                cardAttacked.getCardInput().setAttackDamage((cardAttacked.getCardInput().getAttackDamage() < 2) ?
                        0 : (cardAttacked.getCardInput().getAttackDamage() - 2));
            } else if (cardAttacker.getCardInput().getName().equals("Miraj")) {
                int health = cardAttacked.getCardInput().getHealth();
                cardAttacked.getCardInput().setHealth(cardAttacker.getCardInput().getHealth());
                cardAttacker.getCardInput().setHealth(health);
            } else {
                int attack = cardAttacked.getCardInput().getAttackDamage();
                if (attack == 0) {
                    cardsOnTheBoard.get(action.getCardAttacked().getX()).remove(action.getCardAttacked().getY());
                } else {
                    cardAttacked.getCardInput().setAttackDamage(cardAttacked.getCardInput().getHealth());
                    cardAttacked.getCardInput().setHealth(attack);
                }
            }
        }

        cardAttacker.setHasAttacked(true);
        return null;
    }

    public String attackHero(ActionsInput action, int activePlayer, Player attackedPlayer, Player attacker,
                             GameState gameState) {

        CardProperties cardAttacker = cardsOnTheBoard.get(action.getCardAttacker().getX()).
                get(action.getCardAttacker().getY());
        if (cardAttacker.isFrozen()) {
            return "Attacker card is frozen.";
        } else if (cardAttacker.isHasAttacked()) {
            return "Attacker card has already attacked this turn.";
        } else if (isTank(activePlayer)) {
            return "Attacked card is not of type 'Tank'.";
        }

        attackedPlayer.getHero().setHealth(attackedPlayer.getHero().getHealth() -
                cardAttacker.getCardInput().getAttackDamage());
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

    public String useHeroAbility(ActionsInput action, int activePlayer, Player attacker) {
        if (attacker.getMana() < attacker.getHero().getMana()) {
            return "Not enough mana to use hero's ability.";
        } else if (attacker.isHeroHasAttacked()) {
            return "Hero has already attacked this turn.";
        } else if (attacker.getHero().getName().equals("Lord Royce") ||
                attacker.getHero().getName().equals("Empress Thorina")) {
            if(!isEnemyRow(action.getAffectedRow(), activePlayer)) {
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
                if(!cardsOnTheBoard.get(action.getAffectedRow()).isEmpty()) {
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
