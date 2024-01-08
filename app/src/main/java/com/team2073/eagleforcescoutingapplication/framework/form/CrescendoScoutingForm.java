package com.team2073.eagleforcescoutingapplication.framework.form;

import java.util.ArrayList;

public class CrescendoScoutingForm implements ScoutingForm{

    final private ArrayList<String> fieldNames = new ArrayList<>();
    final private ArrayList<String> teleNames = new ArrayList<>();
    final private ArrayList<String> autoNames = new ArrayList<>();
    final private ArrayList<String> endgameNames = new ArrayList<>();
    final private ArrayList<String> clearNames = new ArrayList<>();


    public CrescendoScoutingForm() {
        //General Info
        fieldNames.add("comp_code");
        fieldNames.add("name");

        fieldNames.add("teamNumber");
        fieldNames.add("matchNumber");
        clearNames.add("teamNumber");
        clearNames.add("matchNumber");

        //Autos
        autoNames.add("autoLeave");
        autoNames.add("autoAmp");
        autoNames.add("autoSpeaker");

        fieldNames.add("autoLeave");
        fieldNames.add("autoAmp");
        fieldNames.add("autoSpeaker");

        clearNames.add("autoLeave");
        clearNames.add("autoAmp");
        clearNames.add("autoSpeaker");

        //Teleop
        teleNames.add("autoAmp");
        teleNames.add("teleSpeaker");
        teleNames.add("teleTrap");
        teleNames.add("coopertition");

        fieldNames.add("autoAmp");
        fieldNames.add("teleSpeaker");
        fieldNames.add("teleTrap");
        fieldNames.add("coopertition");

        clearNames.add("autoAmp");
        clearNames.add("teleSpeaker");
        clearNames.add("teleTrap");
        clearNames.add("coopertition");

        //Endgame
        endgameNames.add("endTrap");
        endgameNames.add("endClimb");

        fieldNames.add("endTrap");
        fieldNames.add("endClimb");

        clearNames.add("endTrap");
        clearNames.add("endClimb");

        //Submit
        fieldNames.add("driverRanking");
        fieldNames.add("defenseRanking");
        fieldNames.add("comment");
        clearNames.add("driverRanking");
        clearNames.add("defenseRanking");
        clearNames.add("comment");
    }

    @Override
    public ArrayList<String> getFieldNames() {
        return fieldNames;
    }

    @Override
    public ArrayList<String> getTeleFieldNames() {
        return teleNames;
    }

    @Override
    public ArrayList<String> getAutoFieldNames() {
        return autoNames;
    }

    @Override
    public ArrayList<String> getEndGameFieldNames() {
        return endgameNames;
    }

    @Override
    public ArrayList<String> getClearNames() {
        return clearNames;
    }

}
