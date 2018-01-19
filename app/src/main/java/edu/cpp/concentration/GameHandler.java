/** *************************************************************
 * file: GameHandler.java
 * author: Christopher Kilian
 * class: CS 245 – Programming Graphical User Interfaces
 *
 * assignment: Android App - Concentration
 * date last modified: 12/04/2017
 *
 * purpose: Handles all non-UI based Concentration game activities, including checking for card matches, checking how many
 * cards are currently selected, game over conditions, and other necessary game management activities.
 *
 *************************************************************** */
package edu.cpp.concentration;

import android.util.Log;

public class GameHandler {

    private int numCards;
    private int cardsSelected;
    private int score;
    private int firstSelectedValue;
    private int secondSelectedValue;
    private int matches;
    private boolean lastPairMatch;
    private boolean gameWon;

    // method: GameHandler
    // purpose: Constructor. Sets initial game state values.
    public GameHandler(int numCards) {
        this.numCards = numCards;
        cardsSelected = 0;
        score = 0;
        matches = 0;
        lastPairMatch = false;
        gameWon = false;
    }

    // method: selectFirstCard
    // purpose: selects the first card and puts counter to 1 card selected. Store face-value integer reference.
    public void selectFirstCard(int cardVal){
        if(!gameWon && (cardsSelected == 0)){ //only run if the game isn't won and no cards have been selected
            cardsSelected = 1;
            firstSelectedValue = cardVal;
        }else{
            Log.i("game", "The game has either been won, or the first card has already been selected. No action taken.");
        }
    }

    // method: selectSecondCard
    // purpose: selects second card and puts counter to 2 cards selected. Checks two face values of cards against each other,
    // and sets lastPairMatch accordingly. Finally, matchHandler is called to deal with operations on card match.
    public void selectSecondCard(int cardVal){
        if(!gameWon && (cardsSelected == 1)){ //only run if game isn't won and 1 card has already been selected
            cardsSelected = 2;
            secondSelectedValue = cardVal;
            if(firstSelectedValue == secondSelectedValue){
                lastPairMatch = true;
            }else{
                lastPairMatch = false;
            }
            matchHandler(); //handles updating of scores and other variables based on lastPairMatch setting
        }else{
            Log.i("game", "The game has either been won, or the proper number of cards has not been selected yet. No action taken.");
        }
    }

    // method: matchHandler
    // purpose: only to be called by another method from within after lastPairMatch has been set to the proper value.
    // Used to handle game management tasks after checking for a match, including score adjustments and resetting of
    // game state values. Also checks against total number of cards divided by 2 (number of pairs) to see if the game
    // has been won or not.
    private void matchHandler(){
        if(lastPairMatch) {
            score += 2; //matches gain 2 points
            matches++; //increment total matches
            cardsSelected = 0; //reset selected to zero (no need for "tryAgain" if a match is made)
            firstSelectedValue = 0;
            secondSelectedValue = 0;
            if((numCards/2) == matches){ //check for game winning state
                gameWon = true;
                Log.i("game", "Game has been won! Score was: " + score);
            }
            Log.i("match","The selected cards match!");
        }else{
            if(score > 0){ //only decrement score if it's above zero (no negative scores)
                score -= 1;
            }
            Log.i("match","No match, try again!");
        }
    }

    // method: tryAgain
    // purpose: resets card selected counter and other game state values to initial settings.
    public void tryAgain(){
        if(!gameWon && (cardsSelected == 2)){ //only run if the game isn't won and two cards have already been selected
            cardsSelected = 0;
            firstSelectedValue = 0;
            secondSelectedValue = 0;
            lastPairMatch = false;
        }else{
            Log.i("game", "The game has either been won, or the proper number of cards has not been selected yet. No action taken.");
        }
    }

    // method: getCardsSelected
    // purpose: Getter for number of cards selected
    public int getCardsSelected() {
        return cardsSelected;
    }

    // method: getScore
    // purpose: Getter for the score
    public int getScore() {
        return score;
    }

    // method: isLastPairMatch
    // purpose: Getter to check if last pair was a match
    public boolean isLastPairMatch() {
        return lastPairMatch;
    }

    // method: isGameWon
    // purpose: Getter to check for game won state
    public boolean isGameWon(){
        return gameWon;
    }
}
