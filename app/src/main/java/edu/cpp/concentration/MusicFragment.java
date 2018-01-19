/** *************************************************************
 * file: MusicFragment.java
 * author: Christopher Kilian, Andrew Tek
 * class: CS 245 – Programming Graphical User Interfaces
 *
 * assignment: Android App - Concentration
 * date last modified: 12/04/2017
 *
 * purpose: Handles the playing of music during the main game. This fragment allows the music to continue
 * playing (or not, depending on user preference) when the game state changes, such as on rotate, without
 * any pause noticeable to the player.
 *
 *************************************************************** */
package edu.cpp.concentration;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

public class MusicFragment extends Fragment {

    MediaPlayer mediaPlayer;
    private View thisFragmentView;
    private boolean wasPlaying;

    // method: onCreate
    // purpose: Create instance and set wasPlaying to true
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        wasPlaying = true;
    }

    // method: onCreateView
    // purpose: runs every time the fragment is reinitialized on state-change (including the very first time)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisFragmentView = inflater.inflate(R.layout.fragment_music, container, false);
        if(mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getActivity(), R.raw.mario_song);
            mediaPlayer.setLooping(true);
        }
        if(wasPlaying){
            playMusic();
        }

        return thisFragmentView;
    }

    // method: onPause
    // purpose: when called will pause the media player
    @Override
    public void onPause() {
        super.onPause();
        pauseMusic();
        Log.i("PAUSE", "Hello from onPause!");
    }

    // method: onDestroy
    // purpose: runs once when the game is actually finished and the fragment is permanently destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMusic();
        Log.i("DESTROY", "Hello from onDestroy!");
    }

    // method: playMusic
    // purpose: when called will play the music
    private void playMusic(){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    // method: stopMusic
    // purpose: when called will stop music
    private void stopMusic(){
        if(mediaPlayer!= null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // method: pauseMusic
    // purpose: if music is playing will pause song
    private void pauseMusic(){
        if(mediaPlayer!= null && mediaPlayer.isPlaying()){
            wasPlaying = true;
            mediaPlayer.pause();
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setWasPlaying(boolean playing){
        wasPlaying = playing;
    }

}
