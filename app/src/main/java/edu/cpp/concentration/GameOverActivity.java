/** *************************************************************
 * file: GameOverActivity.java
 * author: Nicholas Pham, Christopher Kilian
 * class: CS 245 – Programming Graphical User Interfaces
 *
 * assignment: Android App - Concentration
 * date last modified: 12/04/2017
 *
 * purpose: Handles ending a game, including checking player score, displaying final score,
 *          gathering user name if they have a high score, and writing to the high-score files so that
 *          HighScoreActivity may use it.
 *
 *************************************************************** */
package edu.cpp.concentration;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameOverActivity extends AppCompatActivity {
    // Score passed from GameFragment
    private int score;
    // Difficulty of game played; passed from GameFragment
    private int numCards;
    // Filename to write/read to based on difficulty
    private String filename;
    // List of scores from file
    private ArrayList<Score> scoresList;

    // TextView to prompt user for score
    @BindView(R.id.askForScore)
    TextView askForScore;

    // TextView to display final score
    @BindView(R.id.finalScore)
    TextView finalScore;

    // TextView to display "GAME OVER"
    @BindView(R.id.highScoreTextView)
    TextView highScoreTextView;

    // Button for player to submit their name and score to save to high scores
    @BindView(R.id.submitScore)
    Button submitScore;

    // EditText for player to type in their name (if high score)
    @BindView(R.id.nameSubmit)
    EditText nameSubmit;

    // Button to return to main menu if not high score
    @BindView(R.id.mainMenuButton)
    Button mainMenuButton;

    // method: onCreate
    // purpose: Build the activity and set its instance variables to passed values. Display user score, and
    // check for high score.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        ButterKnife.bind(this);
        // Get both score and difficulty from GameFragment
        score = getIntent().getIntExtra("score", 0);
        numCards = getIntent().getIntExtra("numCards", 0);

        scoresList = new ArrayList<Score>();
        Log.i("retreived: ", "score: " + score);
        Log.i("num cards", "numcards: " + numCards);

        String theScore = "Your score is: " + score;
        finalScore.setText(theScore);
      
        // filename is appended from number of cards played to the following rest of text for the file
        filename = Integer.toString(numCards) + "-highscores.txt";

        // Run method to see if high score
        // If it is, add to list, if not, display score and button to return to main menu
        isHighScore();
    }

    // method: isHighScore
    // purpose: if the user gets a new high score, then the program will ask them for their name so it can
    //          store it to an arraylist to store to the file
    private void isHighScore() {
        try {
            FileInputStream fis = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String[] info;
            String line;
            String name;
            int score;

            // in file: "NAME 000"
            while ((line = br.readLine()) != null) {
                info = line.split(" ");
                name = info[0];
                score = Integer.parseInt(info[1]);
                scoresList.add(new Score(name, score));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Compare scores and sort highest to lowest
        CompareScore comparator = new CompareScore();
        Collections.sort(scoresList, comparator);

        // If it's a high score
        if (score >= scoresList.get(2).getScore()) {
            // Remove return to main menu button -> ask for user input
            mainMenuButton.setVisibility(View.GONE);
            askForScore.setText("You have a new high score! Please enter a name for this score: ");
            // What to do when submit is clicked
            submitScore.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // AAk user for name
                    String input = nameSubmit.getText().toString();
                    input = input.replaceAll("\\s+",""); //Remove spaces
                    // If none inputed, default is "ABC"
                    if (nameSubmit.getText().toString().equals("") || nameSubmit.getText().toString().equals(null)) {
                        input = "ABC";
                    }
                    // Erase last node (lowest score) on high scores list to add new (latest score)
                    while (scoresList.size() > 2) {
                        scoresList.remove((scoresList.size()) - 1);
                    }
                    scoresList.add(new Score(input, score));
                    CompareScore comparator = new CompareScore();
                    Collections.sort(scoresList, comparator);
                    Log.i("name/score", input);
                    // Debugging info
                    for (int i = 0; i < scoresList.size(); i++) {
                        Log.i("array", "NAME: " + scoresList.get(i).getName() + " | SCORE: " + scoresList.get(i).getScore());
                    }

                    // Write new ArrayList of Scores to relevant file of difficulty
                    try {
                    OutputStream os = openFileOutput(filename, MODE_PRIVATE);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

                    // Write 3 Scores (top 3)
                    for (int i = 0; i < 3; i++) {
                        bw.write(scoresList.get(i).getName() + " " + scoresList.get(i).getScore() + "\n");
                        Log.i("NAME WRITTEN: ", scoresList.get(i).getName());
                    }
                    os.flush();
                    bw.flush();
                    bw.close();
                    os.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Switch to main menu after write
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    startActivity(intent);
                }
            }));
        }
        // If the player did not get a high score
        else {
            // Do not ask for name, and hide the items that are displayed when a high score should be asked for
            askForScore.setVisibility(View.GONE);
            nameSubmit.setVisibility(View.GONE);
            submitScore.setVisibility(View.GONE);
        }
    }

    // method: returnToMainMenu
    // purpose: button listener to go from this activity to main menu
    @OnClick(R.id.mainMenuButton)
    public void returnToMainMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
