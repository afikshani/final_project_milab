package com.example.afikshani.milab_final.helpers;

import android.os.Parcelable;

public class Warnings {

    public static int MAX_NUM_OF_WARNINGS = 3;
    private WarningDetails[] warnings;

    public Warnings (){
        warnings[0] = new WarningDetails();
        warnings[1] = new WarningDetails();
        warnings[2] = new WarningDetails();
    }

    public void addWarning(WarningDetails warning){
        for (int i = 0; i < MAX_NUM_OF_WARNINGS; i++) {
            if (fullWarning(warnings[i])==false){
                warnings[i] = warning;
            }
        }
    }

    public WarningDetails getNextWarning(){
        for (int i = 0; i < MAX_NUM_OF_WARNINGS ; i++) {
            if (fullWarning(warnings[i])==true){
                return warnings[i];
            }
        }
        return null;
    }

    public String toString(){
        StringBuilder warningsAsString = null;
        for (int i = 0; i < MAX_NUM_OF_WARNINGS ; i++) {
            if (fullWarning(warnings[i])==true){
                warningsAsString.append(warnings[i].ToString() + "|break|");
            }
        }
        return warningsAsString.toString();
    }

    private boolean fullWarning(WarningDetails warning){
        boolean isFull = true;
        if (warning.getAddress().isEmpty() && warning.getDetails().isEmpty()){
            isFull = false;
        }
        return isFull;
    }


}
