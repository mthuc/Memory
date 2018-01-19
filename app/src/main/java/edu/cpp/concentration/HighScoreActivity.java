/** *************************************************************
 * file: HighScoreActivity.java
 * author: Nicholas Pham, Andrew Tek
 * class: CS 245 – Programming Graphical User Interfaces
 *
 * assignment: Android App - Concentration
 * date last modified: 12/04/2017
 *
 * purpose: Displays users high scores for a specified game type (determined by number of cards played)
 *          based on file on that selection.
 *
 *************************************************************** */
package edu.cpp.concentration;
 
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HighScoreActivity extends AppCompatActivity {
    // Button to go back to main menu
    @BindView(R.id.highScoreBackButton)
    Button backButton;
    // Number picker scroll wheel from 4 to 20 (evens) based on difficulty
    NumberPicker numberPicker;
    // Display "high scores for # of cards"
    @BindView(R.id.highScoreTextView)
    TextView highScoreTextView;
    // TextView for player 1 score
    @BindView(R.id.highScorePlayerOneTextView)
    TextView playerOne;
    // TextView for player 2 score
    @BindView(R.id.highScorePlayerTwoTextView)
    TextView playerTwo;
    // TextView for player 3 score
    @BindView(R.id.highScorePlayerThreeTextView)
    TextView playerThree;

    // Difficulty
    private int numberofCards;
    // Appended file name #-highscores.txt
    private String filename;
    // ArrayList of Scores that is obtained and used from the file
    private List<Score> scoresList;

    // method: onCreate
    // purpose: Initialize High Score Activity member variables from passed values. Display user score,
    // and check for high score.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        ButterKnife.bind(this);

        // Initialize ArrayList of Scores
        scoresList = new ArrayList<>();
        // Number of cards == difficulty, passed from HighScoreInfoActivity
        numberPicker = findViewById(R.id.numberPickerHighScore);
        numberofCards = getIntent().getIntExtra("numCards", -1);

        // Display # of cards difficulty to screen
        String numScores = "High scores for: " + numberofCards + " cards";
        highScoreTextView.setText(numScores);

        // Filename == #-highscores.txt
        filename = Integer.toString(numberofCards) + "-highscores.txt";
        // Debug filename
        Log.i("Filename", filename);

        displayScores(filename);
    }

    // method: moveBackToStartScreen
    // purpose: button listener; if clicked, go back to main menu
    @OnClick (R.id.highScoreBackButton)
    public void moveBackToStartScreen() {
        Intent intent = new Intent (this, MainActivity.class);
        startActivity(intent);
    }

    // method: onOptionsItemSelected
    // purpose: method to handle the tapping of the "up" button for ancestral navigation
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // method: displayScores
    // purpose: read in contents of file, to add to scoresList and then display high scores to screen.
    // Write out new high scores and user name if appropriate.
    private void displayScores(String highScoreList) {
        try {
            Log.i("displayScores", "Hello from displayScores! This is the start of the method!");
            FileInputStream fis = openFileInput(highScoreList);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String[] info;
            String line;
            String name;
            int score;

            while ((line = br.readLine()) != null) {
                Log.i("displayScores", "Hello from displayScores! Building scoresList!");
                info = line.split(" ");

                name = info[0];
                score = Integer.parseInt(info[1]);
                scoresList.add(new Score(name, score));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Sets top 3 scores to display with TextView
        Log.i("Name Player 1: ", scoresList.get(0).getName() + " " + scoresList.get(0).getScore());
        String playerOneText = "1. " + scoresList.get(0).getName() + ": " + scoresList.get(0).getScore();
        String playerTwoText = "2. " + scoresList.get(1).getName() + ": " + scoresList.get(1).getScore();
        String playerThreeText = "3. " + scoresList.get(2).getName() + ": " + scoresList.get(2).getScore();
        playerOne.setText(playerOneText);
        playerTwo.setText(playerTwoText);
        playerThree.setText(playerThreeText);
    }
}