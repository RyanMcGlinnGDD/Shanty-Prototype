package com.ryanmcglinn.shanty;

/**
 * Created by Ryan on 5/9/2017.
 */

public class Timespan {
    private int hours;
    private int minutes;
    private int seconds;

    //constructor
    public Timespan(int pHours, int pMinutes, int pSeconds){
        hours = pHours;
        minutes = pMinutes;
        seconds = pSeconds;
    }

    public void SetHours(int pValue){
        hours = pValue;
    }

    public int GetHours(){
        return hours;
    }

    public void SetMinutes(int pValue){
        minutes = pValue;
    }

    public int GetMinutes(){
        return minutes;
    }

    public void SetSeconds(int pValue){
        seconds = pValue;
    }

    public int GetSeconds(){
        return seconds;
    }

    public void AddTimeString(String milliseconds){
        int total = Integer.parseInt(milliseconds);
        //seconds
        total = total/1000;

        int hours = total/3600;
        total = total % 3600;

        int minutes = total/60;
        total = total % 60;

        int seconds = total;
        AddTime(hours, minutes, seconds);
    }

    public void AddTime(int pHours, int pMinutes, int pSeconds){
        int extra = 0;

        seconds = seconds + pSeconds;
        if(seconds >= 60){
            extra = seconds / 60;
            seconds = seconds % 60;
        }

        minutes = minutes + pMinutes + extra;
        extra = 0;
        if(minutes >= 60){
            extra = minutes / 60;
            minutes = minutes % 60;
        }

        hours = hours + pHours + extra;
    }

    public void SubtractTime(int pHours, int pMinutes, int pSeconds){
        hours = hours - pHours;
        if(hours < 0){
            int multiplier = hours * -1;
            hours = 0;
            minutes = minutes - (multiplier * 60);
        }

        minutes = minutes - pMinutes;
        if(minutes < 0){
            int multiplier = minutes * -1;
            minutes = 0;
            seconds = seconds - (multiplier * 60);
        }

        seconds = seconds - pSeconds;
        if(seconds < 0){
            seconds = 0;
        }
    }

    public void SetTime(int pHours, int pMinutes, int pSeconds){
        hours = pHours;
        minutes = pMinutes;
        seconds = pSeconds;
    }

    public String GetDurationString(){
        String outputHours = hours + "";

        String outputMinutes = minutes + "";
        if(minutes / 10 == 0){
            outputMinutes = "0" +outputMinutes;
        }

        String outputSeconds = seconds + "";
        if(seconds / 10 == 0){
            outputSeconds = "0" +outputSeconds;
        }

        return outputHours + " : " + outputMinutes + " : " + outputSeconds;
    }

    public int GetSecondDuration(){
        return (hours * 3600) + (minutes * 60) + seconds;
    }
}
