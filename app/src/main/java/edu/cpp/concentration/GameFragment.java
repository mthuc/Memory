/** *************************************************************
 * file: GameFragment.java
 * author: Christopher Kilian
 * class: CS 245 – Programming Graphical User Interfaces
 *
 * assignment: Android App - Concentration
 * date last modified: 12/04/2017
 *
 * purpose: Handles the main Concentration game. Manages card placement, card flipping, card matching, and other related
 * game activities. Also holds and manipulates reference to GameHandler object which manages game scoring, match checking, and
 * other non-UI related game activities. Designed to handle both portrait and landscape layouts in conjunction with the fragment_game
 * xml files found in both layout and layout-land directories.
 *
 *************************************************************** */
package edu.cpp.concentration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class GameFragment extends Fragment {

    private int numCards;
    private List<Button> buttonList;
    private Map<Button, Integer> buttonMap;
    private GameHandler theGame;
    private Button firstSelected;
    private Button secondSelected;
    private View thisFragmentView;
    private TextView score;
    private final int[] CARD_FACES = {R.drawable.anduin, R.drawable.druid, R.drawable.garrosh,
            R.drawable.guldan, R.drawable.jaina, R.drawable.lich, R.drawable.rexxar, R.drawable.thrall,
            R.drawable.uther, R.drawable.valeera};

    // method: onCreate
    // purpose: onCreate only runs once for a fragment in which it is set to retain instance (until it is destroyed
    // along with its Activity).Set up initial game state, including card generation, random face placement, and more.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle passed = getArguments();
        numCards = passed.getInt("numCards", -1);
        if (numCards == -1) {
            Log.i("gameError", "Number of cards not properly passed! Defaulting to max cards.");
            numCards = 20;
        }

        theGame = new GameHandler(numCards);
        initCardsHandler();
    }

    // method: onCreateView
    // purpose: this runs each time the fragments state changes, such as on initialization or rotation.
    // Handles the setting of visible score for player, the initialization of the appropriate view model
    // (either landscape or portrait) and the placement of the cards within that model.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        score = getActivity().findViewById(R.id.scoreTextView);
        thisFragmentView = inflater.inflate(R.layout.fragment_game, container, false); // Inflate the layout for this fragment
        if(savedInstanceState != null){ //only not null on initial generation of fragment
            clearCardView();
        }
        initCardView();
        String scoreText = "Score: " + theGame.getScore();
        score.setText(scoreText);
        return thisFragmentView;
    }

    // method: initCardsHandler
    // purpose: method to programatically create the proper number of card buttons for the game. Once buttons have been
    //          created, mapCards() is called to randomly map each card button to its face-value.
    private void initCardsHandler(){
        buttonList = new ArrayList<>();
        for(int i = 0; i < numCards; i++){
            Button cardButton = initButton(i);
            buttonList.add(cardButton);
        }
        mapCards();
    }

    // method: initButton
    // purpose: method to set up a button with the proper parameters - used by initCardsHandler method
    private Button initButton(int i){
        Button cardButton = new Button(getActivity());
        String buttonTag = "button" + i;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(5,5,5,5);
        cardButton.setLayoutParams(params);
        cardButton.setBackgroundResource(R.drawable.cardback);
        cardButton.setTag(buttonTag);
        cardButton.setOnClickListener(onClickFlipper(cardButton));
        return cardButton;
    }

    // method: onClickFlipper
    // purpose: custom click listener to be assigned to each of the "card" buttons in the game
    private View.OnClickListener onClickFlipper(final Button button)  {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("cardClick","The card selected was: " + button.getTag());
                if(theGame.getCardsSelected() == 0){ //if no cards selected, this is first card - flip and save
                    firstSelected = button;
                    button.setBackgroundResource(buttonMap.get(button));
                    theGame.selectFirstCard(buttonMap.get(button));
                }else if(theGame.getCardsSelected() == 1){ //if one card selected, this is the second and compare operations must happen
                    secondSelected = button;
                    button.setBackgroundResource(buttonMap.get(button));
                    theGame.selectSecondCard(buttonMap.get(button));
                    if(theGame.isLastPairMatch()){ //on match, disable those buttons and release reference (they are out of play)
                        firstSelected.setEnabled(false);
                        secondSelected.setEnabled(false);
                        firstSelected = null;
                        secondSelected = null;
                    }
                    if(theGame.isGameWon()){ //call gameOver method if the game reports as won
                        gameOver();
                    }
                }
                String scoreText = "Score: " + theGame.getScore();
                score.setText(scoreText);
            }
        };
    }

    // method mapCards
    // purpose: method that only calls after buttonList has been initialized (call initCardsHandler)
    // Creates a list of pairs of card face values, then shuffles these values. Finally, each card button
    // is mapped to each of these faces in turn, creating the randomized placement of each card pair.
    private void mapCards(){
        buttonMap = new HashMap<>();
        List<Integer> cardFaceList = new ArrayList<>();
        for(int i = 0; i < numCards; i++){
            int index = i/2; //integer division ensures that two copies of each card face end up in list
            cardFaceList.add(CARD_FACES[index]);
            Log.i("faces","card face index added: " + index);
        }
        Collections.shuffle(cardFaceList); //randomize order of card faces represented in the list
        for(Button button : buttonList){
            buttonMap.put(button, cardFaceList.remove(0)); //map each button to a card face from the list
        }
    }

    // method: initCardView
    // purpose: method to display the programatically created cards on the screen. Orientation of the
    // device is checked so that row placement can be handled appropriately.
    private void initCardView(){
        boolean portraitFlag = true; //true if portrait, false if landscape
        if(getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {
            portraitFlag = false;
        }

        for(int i = 0; i < buttonList.size(); i++){
            LinearLayout layout = getRow(i);
            setWeight(i, portraitFlag);
            layout.addView(buttonList.get(i));
        }

    }

    // method: setWeight
    // purpose: Applies the appropriate weight to each card button before it's placed so that cards aren't
    // stretched too much when placed in a row that isn't full.
    private void setWeight(int i, boolean portraitFlag){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)buttonList.get(i).getLayoutParams();
        int lastRowSize;
        if(portraitFlag){
            lastRowSize = numCards%4; //portrait orientation - numCards%4 gives the number of cards in the last row
        }else{
            lastRowSize = numCards%7; //landscape orientation - numCards%7 gives the number of cards in the last row
        }

        if(i < (numCards-lastRowSize)) {
            params.weight = 1; //All non-last row cards given weight 1 to ensure they're the same size
        }else{
            params.weight = 0; //All last row cards given weight 0 so they don't stretch too much
        }

    }

    // method: clearCardView
    // purpose: method to clear button associations to prior layouts so that they can be re-placed (for rotation)
    private void clearCardView(){
        for(Button button : buttonList) {
            if (button.getParent() != null) {
                ((ViewGroup) button.getParent()).removeView(button);
            }
        }
    }

    // method: getRow
    //  purpose: method to get the appropriate row into which to place a button, based on how many have been placed so far.
    //  Only want a max of 4 buttons per row in portrait view. Max of 7 per row in landscape.
    private LinearLayout getRow(int i){
        LinearLayout layout = thisFragmentView.findViewById(R.id.firstRow); //default to first row
        if(getResources().getConfiguration().orientation != ORIENTATION_LANDSCAPE) {
            if (i >= 4 && i <= 7) { //second row indices 4 through 7
                layout = thisFragmentView.findViewById(R.id.secondRow);
            } else if (i >= 8 && i <= 11) { //third row indices 8 through 11
                layout = thisFragmentView.findViewById(R.id.thirdRow);
            } else if (i >= 12 && i <= 15) { //fourth row indices 12 through 15
                layout = thisFragmentView.findViewById(R.id.fourthRow);
            } else if (i >= 16 && i <= 19) { //fifth row indices 16 through 19
                layout = thisFragmentView.findViewById(R.id.fifthRow);
            }
        }else{
            if(i >= 7 && i <= 13){ //second row indices 7 through 13
                layout = thisFragmentView.findViewById(R.id.secondRow);
            }else if(i >= 14 && i <= 19){ //third row indices 8 through 19
                layout = thisFragmentView.findViewById(R.id.thirdRow);
            }
        }

        return layout;
    }

    // method: gameOver
    // purpose: transition to GameOverActivity, sending the info. of user score and difficulty played
    private void gameOver(){
        Intent gameOverIntent = new Intent(getActivity(), GameOverActivity.class);
        int finalScore = theGame.getScore();
        gameOverIntent.putExtra("score", finalScore);
        gameOverIntent.putExtra("numCards", numCards);
        Log.i("toPass", "score: " + finalScore);
        Log.i("toPass", "card num: " + numCards);
        getActivity().startActivity(gameOverIntent);
    }

    // method: tryAgainHandler
    // purpose: Handles the display elements which change when the player taps "Try Again". Resets card image to
    // the cardback, releases references to the face values selected, and runs the game objects "tryAgain" method
    // to manage scoreing and other internal game states.
    public void tryAgainHandler(){
        if(theGame.getCardsSelected() == 2){

            if(!(theGame.isLastPairMatch()) && (firstSelected != null) && (secondSelected != null)){
                firstSelected.setBackgroundResource(R.drawable.cardback);
                secondSelected.setBackgroundResource(R.drawable.cardback);
                firstSelected = null;
                secondSelected = null;
            }
            theGame.tryAgain();
        }
    }

    // method: getButtonMap
    // purpose: Getter for the buttonMap
    public Map<Button, Integer> getButtonMap() {
        return buttonMap;
    }

    // method: getButtonList
    // purpose: Getter for the buttonList
    public List<Button> getButtonList() {
        return buttonList;
    }

    // method: getTheGame
    // purpose: Getter for the game object
    public GameHandler getTheGame(){
        return theGame;
    }

}
