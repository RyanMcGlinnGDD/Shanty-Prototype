package com.ryanmcglinn.shanty;

import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //saving copies of the active fragments
    private PlaylistEditFragment playlistEditFragment;
    private SongSelectFragment songSelectFragment;

    //duration and playback variables
    private boolean currentlyPlaying;
    private Timespan targetDuration;
    private Timespan actualDuration;

    //media variabels
    private List<MediaFileInfo> mediaList;
    private List<MediaFileInfo> playlist;
    private MediaPlayer mediaPlayer;
    private int currentPlaybackIndex;
    private boolean mediaPlayerInitialized;
    private int elapsedTime;
    private Handler timeLabelHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //duration and playback initialization
        targetDuration = new Timespan(0,0,0);
        actualDuration = new Timespan(0,0,0);
        currentlyPlaying = false;
        mediaPlayerInitialized = false;

        //initialize fragments
        playlistEditFragment = PlaylistEditFragment.newInstance();
        songSelectFragment = SongSelectFragment.newInstance();

        //media initialization
        mediaList = new ArrayList<MediaFileInfo>();
        parseAllAudio();
        playlist = new ArrayList<MediaFileInfo>();
        currentPlaybackIndex = -1;
        timeLabelHandler = new Handler();
        timeLabelHandler.postDelayed(labelLoop, 100);

        //load active fragment
        loadFragment("PlaylistEdit");
    }

    //dynamically loads the passed in fragment
    public void loadFragment(String which){
        if(which.equals("PlaylistEdit")) {
            //change button properties
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            //transaction stuff
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.animator.fragment_animation_fade_in, R.animator.fragment_animation_fade_out);
            ft.replace(R.id.frame, playlistEditFragment);
            ft.commit();

        } else if(which.equals("SongSelect")){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.animator.fragment_animation_fade_in, R.animator.fragment_animation_fade_out);
            ft.addToBackStack(null);
            ft.replace(R.id.frame, songSelectFragment);
            ft.commit();
        }
    }

    public void makeToast (String value){
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }

    //for playing the next song
    public void playNext() {
        //pre increment the playback index
        currentPlaybackIndex++;

        //add the current duration to the elapsed timespan, provided it's a case where the mediaplayer is initialized
        if(currentPlaybackIndex > 0){
            elapsedTime += mediaPlayer.getDuration();
        }

        if(currentPlaybackIndex < playlist.size()){
            ((TextView)findViewById(R.id.titleLabel)).setText(playlist.get(currentPlaybackIndex).getFileName());

            mediaPlayer.reset();
            mediaPlayer = MediaPlayer.create(MainActivity.this, Uri.parse(playlist.get(currentPlaybackIndex).getFilePath()));
            //make it play the next
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNext();
                }
            });
            mediaPlayer.start();
        } else{
            //reset key variables
            mediaPlayer.stop();
            currentPlaybackIndex = -1;
            currentlyPlaying = false;
            findViewById(R.id.playButton).setVisibility(View.VISIBLE);
            findViewById(R.id.pauseButton).setVisibility(View.GONE);
            mediaPlayerInitialized = false;
            ((TextView)findViewById(R.id.titleLabel)).setText("No Playback in Progress");
            makeToast("Playlist is complete");
        }
    }
    //called when the dynamic media button on the lower bar is pressed
    public void togglePlayback(View view){
        //check to make sure that the playlist isnt empty
        if(actualDuration.GetSecondDuration() > 0){
            //check to make sure that the playlist is actually longer than the target
            if(targetDuration.GetSecondDuration() <= actualDuration.GetSecondDuration()){
                //initialization handling
                if(!mediaPlayerInitialized){
                    //shuffle the playlist
                    Collections.shuffle(playlist);

                    elapsedTime = 0;
                    //initialize
                    mediaPlayer = MediaPlayer.create(this, Uri.parse(playlist.get(0).getFilePath()));
                    mediaPlayerInitialized = true;
                    currentlyPlaying = true;

                    //set ui
                    findViewById(R.id.playButton).setVisibility(View.GONE);
                    findViewById(R.id.pauseButton).setVisibility(View.VISIBLE);

                    playNext();
                } else{
                    //toggle handling
                    if(currentlyPlaying){
                        currentlyPlaying = false;
                        findViewById(R.id.playButton).setVisibility(View.VISIBLE);
                        findViewById(R.id.pauseButton).setVisibility(View.GONE);
                        mediaPlayer.pause();
                    } else{
                        currentlyPlaying = true;
                        findViewById(R.id.playButton).setVisibility(View.GONE);
                        findViewById(R.id.pauseButton).setVisibility(View.VISIBLE);
                        mediaPlayer.start();
                    }
                }


            } else{
                makeToast("Playlist does not meet target duration!");
            }

        } else{

            makeToast("Playlist is empty!");
        }
    }

    //called remotely to dynamically change the visibility of the toolbar's back button
    public void setBackButtonVisibility(boolean value){
        getSupportActionBar().setDisplayHomeAsUpEnabled(value);
    }

    public void setTitle(String value){
        getSupportActionBar().setTitle(value);
    }

    //gives up button back stack functionality
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //selects which menu to inflate when overflow is clicked
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //case methods for menu options
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String printedline = "";
        switch (item.getItemId()) {
            case R.id.action_settings:
                printedline = playlist.size() + "";
                Toast.makeText(this, printedline, Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_debug:
                printedline = mediaList.size() + "";
                Toast.makeText(this, printedline, Toast.LENGTH_SHORT).show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    //handler functions as an android specific timer
    private Runnable labelLoop = new Runnable() {
        @Override
        public void run() {
            //execute loop stuff
            if(currentlyPlaying){
                Timespan tempspan = new Timespan(0,0,0);
                tempspan.AddTimeString(elapsedTime + "");
                tempspan.AddTimeString(mediaPlayer.getCurrentPosition() + "");
                ((TextView) findViewById(R.id.timeLabel)).setText(tempspan.GetDurationString() + " / " + actualDuration.GetDurationString());
            }

            //call the loop again
            timeLabelHandler.postDelayed(this, 100);
        }
    };

    //media stuff
    // example method modified from https://github.com/mukesh4u/MediaListing
    private void parseAllAudio() {
        try {
            //get cursor for the media store database
            //Cursor cur = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
            Cursor cur;
            Context con = getApplicationContext();
            ContentResolver conres = con.getContentResolver();
            cur = conres.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

            if (cur == null) {
                // Something went wrong

            } else if (!cur.moveToFirst()) {
                // No music found on device

            } else {
                do {
                    String artistData = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String titleData = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String albumData = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String albumIdData = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                    String durationData = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String idData = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media._ID));
                    String filePathData = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                    MediaFileInfo audio = new MediaFileInfo(titleData, filePathData, artistData, albumData, durationData, idData);

                    //substring ensures that the audio file is located in the music folder
                    if(audio.getFilePath().length() >= 26){
                        if(audio.getFilePath().substring(0, 26).equals("/storage/emulated/0/Music/")){
                            mediaList.add(audio);
                        }
                    }

                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //duration variable management
    public Timespan getActualDuration(){
        return actualDuration;
    }
    public Timespan getTargetDuration(){
        return targetDuration;
    }
    public List getMediaList(){
        return mediaList;
    }
    public List getPlaylist(){
        return playlist;
    }
}
