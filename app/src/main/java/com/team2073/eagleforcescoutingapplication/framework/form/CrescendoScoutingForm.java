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
        fieldNames.add("quantifier");

        fieldNames.add("teamNumber");
        fieldNames.add("matchNumber");
        clearNames.add("teamNumber");
        clearNames.add("matchNumber");

        //Autos
        autoNames.add("autoLeave");
        autoNames.add("autoAmp");
        autoNames.add("autoSpeakerMake");
        autoNames.add("autoSpeakerMiss");

        fieldNames.add("autoLeave");
        fieldNames.add("autoAmp");
        fieldNames.add("autoSpeakerMake");
        fieldNames.add("autoSpeakerMiss");

        clearNames.add("autoLeave");
        clearNames.add("autoAmp");
        clearNames.add("autoSpeakerMake");
        clearNames.add("autoSpeakerMiss");

        //Teleop
        teleNames.add("teleopAmp");
        teleNames.add("teleopSpeakerMake");
        teleNames.add("teleopSpeakerMiss");
        teleNames.add("coopertition");

        fieldNames.add("teleopAmp");
        fieldNames.add("teleopSpeakerMake");
        fieldNames.add("teleopSpeakerMiss");
        fieldNames.add("coopertition");

        clearNames.add("teleopAmp");
        clearNames.add("teleopSpeakerMake");
        clearNames.add("teleopSpeakerMiss");
        clearNames.add("coopertition");

        //Endgame
        endgameNames.add("endClimb");

        fieldNames.add("endClimb");

        clearNames.add("endClimb");

        //Trap
        fieldNames.add("trapOne");
        fieldNames.add("trapTwo");
        fieldNames.add("trapThree");

        clearNames.add("trapOne");
        clearNames.add("trapTwo");
        clearNames.add("trapThree");

        //Submit
        fieldNames.add("driverRanking");
        fieldNames.add("defenseRanking");
        fieldNames.add("comment");
        fieldNames.add("isBroken");
        fieldNames.add("isDisabled");
        fieldNames.add("isTipped");

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
