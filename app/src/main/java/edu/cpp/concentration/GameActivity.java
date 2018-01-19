/** *************************************************************
 * file: GameActivity.java
 * author: Christopher Kilian, Andrew Tek
 * class: CS 245 – Programming Graphical User Interfaces
 *
 * assignment: Android App - Concentration
 * date last modified: 12/04/2017
 *
 * purpose: Handles the main Concentration game and related fragments (the game and music fragments).
 *          Also manages button clicks for New Game, End Game, Try Again, and Toggle Music buttons.
 *
 *************************************************************** */
package edu.cpp.concentration;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameActivity extends AppCompatActivity {

    private int numCards;
    private GameFragment theGameFragment;
    private MusicFragment musicFragment;
    private final String SAVED_FRAGMENT_TAG = "theGameFragment";
    private final String SAVED_MUSIC_FRAGMENT_TAG = "thisMusicFragment";

    TextView score;
    @BindView(R.id.toggleMusicButton)
    Button toggleMusic;
    @BindView(R.id.endGameButton)
    Button endGame;
    @BindView(R.id.tryAgainButton)
    Button tryAgain;
    @BindView(R.id.newGameButton)
    Button newGame;

    // method: onCreate
    // purpose: Builds the activity. Runs every time the activity is created or recreated, so local variables are
    //set here, including the getting of fragments from the fragment manager, or the instantiation of those
    //fragments if necessary.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        score = findViewById(R.id.scoreTextView);
        numCards = getIntent().getIntExtra("numCards", -1);
        if (numCards == -1) {
            Log.i("gameError", "Number of cards not properly passed! Defaulting to max cards.");
            numCards = 20;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        theGameFragment = (GameFragment) fragmentManager.findFragmentByTag(SAVED_FRAGMENT_TAG); //handles game display
        musicFragment = (MusicFragment) fragmentManager.findFragmentByTag(SAVED_MUSIC_FRAGMENT_TAG); //handles music

        if (theGameFragment == null) { //only instantiate fragments if they don't already exist in the manager
            Bundle myBundle = new Bundle();
            myBundle.putInt("numCards", numCards);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            theGameFragment = new GameFragment();
            theGameFragment.setArguments(myBundle);
            fragmentTransaction.add(R.id.fragment_container, theGameFragment, SAVED_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
        if (musicFragment == null) {
            FragmentTransaction musicTransaction = fragmentManager.beginTransaction();
            musicFragment = new MusicFragment();
            musicTransaction.add(R.id.music_fragment_container, musicFragment, SAVED_MUSIC_FRAGMENT_TAG);
            musicTransaction.commit();
        }

        getSupportActionBar().setHomeButtonEnabled(true); //ancestral navigation button

        ButterKnife.bind(this);
    }

    // method: onResume
    // purpose: Last step in the lifecycle before the Activity is displayed to the user. Ensures music toggle
    // is displaying properly. Also sets score text.
    @Override
    protected void onResume() {
        super.onResume();
        if(musicFragment.getMediaPlayer().isPlaying()){
            toggleMusic.setText("Turn Music Off");
        }else{
            toggleMusic.setText("Turn Music On");
        }
        String scoreText = "Score: " + theGameFragment.getTheGame().getScore();
        score.setText(scoreText);
    }

    // method: endGameHandler
    // purpose: disable all buttons besides music toggle, flip over all cards for
    // 5 seconds then return to main activity screen. This delay allows the user to see what the
    // card positions were before being returned to the main menu.
    @OnClick(R.id.endGameButton)
    public void endGameHandler() {
        endGame.setEnabled(false);
        newGame.setEnabled(false);
        tryAgain.setEnabled(false);
        Map<Button, Integer> buttonMap = theGameFragment.getButtonMap();
        for (Button imageButton : theGameFragment.getButtonList()) {
            imageButton.setBackgroundResource(buttonMap.get(imageButton));
            imageButton.setEnabled(false);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                moveToMainActivity();
            }
        }, 5000);
    }

    // method: newGameButton
    // purpose: Button on click will move to InfoActivity class
    @OnClick(R.id.newGameButton)
    public void newGameButton() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    // method: tryAgainButton
    // purpose: Calls the method within the game fragment to handle the "try again" action
    @OnClick(R.id.tryAgainButton)
    public void tryAgainHandler() {
        theGameFragment.tryAgainHandler();

    }

    // method: toggleMusic
    // purpose: play music and set appropriate text for the button
    @OnClick(R.id.toggleMusicButton)
    public void toggleMusic() {
        MediaPlayer mediaPlayer = musicFragment.getMediaPlayer();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            toggleMusic.setText("Turn Music On");
            musicFragment.setWasPlaying(false); //for tracking on rotation
        }
        else {
            mediaPlayer.start();
            toggleMusic.setText("Turn Music Off");
            musicFragment.setWasPlaying(true); //for tracking on rotation
        }
    }

    // method: opOptionsItemSelected
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

    // method: moveToMainActivity
    // purpose: will move screen to main activity (main menu)
    public void moveToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}