/** *************************************************************
 * file: InfoActivity.java
 * author: Andrew Tek, Christopher Kilian
 * class: CS 245 – Programming Graphical User Interfaces
 *
 * assignment: Android App - Concentration
 * date last modified: 12/04/2017
 *
 * purpose: Allows the player to select which type of Concentration game they want
 * to play. Games types are determined by number of cards, and this Activity lets the player choose their
 * number of cards before loading the appropriate game.
 *
 *************************************************************** */
package edu.cpp.concentration;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.NumberPicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InfoActivity extends AppCompatActivity {
    NumberPicker numberPicker;
    @BindView(R.id.submitButton)
    Button submitButton;
    // method: moveToGameActivity
    // purpose: On create will set the numbers that can be selected in number picker
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        numberPicker = findViewById(R.id.numberPicker);
        String [] values = {"4", "6", "8", "10", "12", "14", "16", "18", "20"};
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length - 1);
        numberPicker.setDisplayedValues(values);
        getSupportActionBar().setHomeButtonEnabled(true); //ancestral navigation button
        ButterKnife.bind(this);
    }

    // method: moveToGameActivity
    // purpose: will move to gameScreen and send the number of cards desired to the next activity
    @OnClick(R.id.submitButton)
    public void moveToGameActivity() {
        Intent intent = new Intent(this, GameActivity.class);
        int numCardsSelected = Integer.parseInt(numberPicker.getDisplayedValues()[numberPicker.getValue()]);
        Log.i("toPass","cards selected reads: " + numCardsSelected);
        intent.putExtra("numCards", numCardsSelected);
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
}