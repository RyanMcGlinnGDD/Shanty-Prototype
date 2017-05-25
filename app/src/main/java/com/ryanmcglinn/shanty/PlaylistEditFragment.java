package com.ryanmcglinn.shanty;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.Duration;

/**
 * Created by Ryan on 5/6/2017.
 */

public class PlaylistEditFragment extends Fragment {

    private FloatingActionButton fab;
    private Timespan targetDuration;
    private Timespan actualDuration;
    private ListView displayList;

    public static PlaylistEditFragment newInstance(){

        PlaylistEditFragment myFragment = new PlaylistEditFragment();

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

        //inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.playlist_edit_fragment, container, false);

        //set values and methods for things contained in this view
        ((TextView)myView.findViewById(R.id.actualDuration)).setText(actualDuration.GetDurationString());

        return myView;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //number picker initialization
        NumberPicker npHour = (NumberPicker) getView().findViewById(R.id.np_hour);
        npHour.setMinValue(0);
        npHour.setMaxValue(9);
        npHour.setValue(targetDuration.GetHours());
        npHour.setWrapSelectorWheel(true);
        npHour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                targetDuration.SetHours(newVal);
            }
        });

        NumberPicker npTen = (NumberPicker) getView().findViewById(R.id.np_ten);
        npTen.setMinValue(0);
        npTen.setMaxValue(5);
        npTen.setValue(targetDuration.GetMinutes() / 10);
        npTen.setWrapSelectorWheel(true);
        npTen.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                targetDuration.SetMinutes(Integer.parseInt(newVal + "" + (targetDuration.GetMinutes() % 10)));
            }
        });


        NumberPicker npOne = (NumberPicker) getView().findViewById(R.id.np_one);
        npOne.setMinValue(0);
        npOne.setMaxValue(9);
        npOne.setValue(targetDuration.GetMinutes() % 10);
        npOne.setWrapSelectorWheel(true);
        npOne.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                targetDuration.SetMinutes(Integer.parseInt((targetDuration.GetMinutes() / 10) + "" + newVal));
            }
        });

        //floating action button stuff in hopes of alleviating weirdness with the support library
        //set fab action
        fab = (FloatingActionButton) view.findViewById(R.id.playlistFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(((MainActivity)getActivity()).getMediaList().size() == 0){
                ((MainActivity)getActivity()).makeToast("The music folder is empty!");
            } if(targetDuration.GetSecondDuration() > actualDuration.GetSecondDuration()){
                ((MainActivity)getActivity()).loadFragment("SongSelect");
            } else{
                ((MainActivity)getActivity()).makeToast("Duration currently meets target");
            }
            }
        });

        //fix for fab bug, forces fab to make a second draw pass
        //https://issuetracker.google.com/issues/37118105
        view.post(new Runnable() {
            @Override
            public void run() {
                fab.requestLayout();
            }
        });

        //the back/up button should not appear on this screen
        ((MainActivity)getActivity()).setBackButtonVisibility(false);
        ((MainActivity)getActivity()).setTitle("Shanty");

        //populate the listview
        /*displayList = (ListView) view.findViewById(R.id.playlistView);

        List fromMain = ((MainActivity)getActivity()).getPlaylist();
        List<String> transferred = new ArrayList<String>();
        for(int i = 0; i < fromMain.size(); i++){
            transferred.add(((MediaFileInfo) fromMain.get(i)).getFileName());
        }

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, transferred);

        displayList.setAdapter(arrayAdapter);*/
    }
}