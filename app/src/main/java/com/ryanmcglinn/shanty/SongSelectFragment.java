package com.ryanmcglinn.shanty;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import javax.xml.datatype.Duration;

/**
 * Created by Ryan on 5/6/2017.
 */

public class SongSelectFragment extends Fragment {

    private Timespan targetDuration;
    private Timespan actualDuration;

    private List<MediaFileInfo> mediaList;
    private List<MediaFileInfo> playlist;

    private int choice1;
    private int choice2;

    private RelativeLayout button1;
    private RelativeLayout button2;

    public static SongSelectFragment newInstance(){

        SongSelectFragment myFragment = new SongSelectFragment();

        //set transferable variables
        Bundle args = new Bundle();
        myFragment.setArguments(args);

        return myFragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //bundle handling
        Bundle args = getArguments();

        //import duration variables
        actualDuration = ((MainActivity)getActivity()).getActualDuration();
        targetDuration = ((MainActivity)getActivity()).getTargetDuration();
        mediaList = ((MainActivity)getActivity()).getMediaList();
        playlist = ((MainActivity)getActivity()).getPlaylist();

        //inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.song_select_fragment, container, false);
        return myView;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button1 = (RelativeLayout) view.findViewById(R.id.choice1);
        button2 = (RelativeLayout) view.findViewById(R.id.choice2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add index to the playlist
                playlist.add(mediaList.get(choice1));

                //compound duration
                actualDuration.AddTimeString(mediaList.get(choice1).getDuration());

                //delete index from the mediaList
                mediaList.remove(choice1);

                resetElements();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add index to the playlist
                playlist.add(mediaList.get(choice2));

                //compound duration
                actualDuration.AddTimeString(mediaList.get(choice2).getDuration());

                //delete index from the mediaList
                mediaList.remove(choice2);
                resetElements();
            }
        });


        resetElements();
    }

    public void resetElements(){
        if(actualDuration.GetSecondDuration() < targetDuration.GetSecondDuration()){
            ((MainActivity)getActivity()).setTitle(actualDuration.GetDurationString() + " / " + targetDuration.GetDurationString());
            int songCount = mediaList.size();
            //select two items in the media list and pit them against one another
            //get two randoms in the range
            choice1 = (int)(Math.random() * songCount + 1);
            choice2 = (int)(Math.random() * songCount + 1);
            while(choice1 == choice2){
                choice2 = (int)(Math.random() * songCount + 1);
            }
            //populate the elements
            ((TextView)getView().findViewById(R.id.choiceText1)).setText(mediaList.get(choice1).getFileName());
            ((TextView)getView().findViewById(R.id.albumText1)).setText(mediaList.get(choice1).getAlbum());
            ((TextView)getView().findViewById(R.id.choiceText2)).setText(mediaList.get(choice2).getFileName());
            ((TextView)getView().findViewById(R.id.albumText2)).setText(mediaList.get(choice2).getAlbum());
        } else{
            ((MainActivity)getActivity()).makeToast("Target duration has been reached");
            getFragmentManager().popBackStackImmediate();
        }

    }
}