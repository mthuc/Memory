/***************************************************************
 * file: MainActivity.java
 * author: Christopher Kilian, Nicholas Pham, Andrew Tek
 * class: CS 245 – Programming Graphical User Interfaces
 *
 * assignment: Android App - Concentration
 * date last modified: 12/04/2017
 *
 * purpose: Builds the main menu, along with xml file activity_main. Also loads the files necessary
 * for saving and loading player high-scores.
 *
 ****************************************************************/
package edu.cpp.concentration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.util.Log;
import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class MainActivity extends AppCompatActivity {

    @BindView(R.id.startGameButton)
    Button startGame;
    @BindView(R.id.highScoreButton)
    Button highScore;

    // method: onCreate
    // purpose: Create and display the main menu of the game
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setHomeButtonEnabled(false); //ancestral navigation button
        highScoreFileToMemory();
        ButterKnife.bind(this);
    }

    // method: changeScreenToActivityInfo
    // purpose: button listener to move to InfoActivity
    @OnClick(R.id.startGameButton)
    public void changeScreenToActivityInfo() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    // method: changeScreenToHighScoreInfo
    // purpose: move intent to highscore activity
    @OnClick(R.id.highScoreButton)
    public void changeScreenToHighScoreInfo() {
        Intent intent = new Intent (this, HighScoreInfoActivity.class);
        startActivity(intent);
    }

    // method: highScoreFileToMemory
    // purpose: takes the high scores file associated with the difficulty and places it in internal memory if it isn't there
    private void highScoreFileToMemory() {
        String filename = "-highscores.txt";
        String appendedFilename;

        try {
            for (int i = 4; i <= 20; i+=2) {
                // i = number, so #-highscores.txt (see assets folder)
                appendedFilename = i + filename;

                File file = getBaseContext().getFileStreamPath(appendedFilename);
                if (!file.exists()) {
                    FileOutputStream fos = openFileOutput(appendedFilename, Context.MODE_PRIVATE);
                    fos.write(loadFile(appendedFilename).getBytes());
                    fos.close();
                    Log.i("Writing file: ", appendedFilename);
                }
                else {
                    // File exists
                    Log.i("FILE_EXIST", appendedFilename + " EXISTS");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // method: loadFile
    // purpose: retrieve contents of file
    private String loadFile(String file) {
        String contents = "";
        try {
            InputStreamReader isr = new InputStreamReader(getAssets().open(file));
            BufferedReader br = new BufferedReader(isr);
            // Read 3 lines (top 3 scores)
            for (int i = 0; i < 3; i++) {
                contents += br.readLine() + "\n";
            }
            isr.close();
            br.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return contents;
    }

}
