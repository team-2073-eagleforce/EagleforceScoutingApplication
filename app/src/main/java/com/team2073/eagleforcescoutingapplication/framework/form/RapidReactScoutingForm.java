package com.team2073.eagleforcescoutingapplication.framework.form;

import java.util.ArrayList;

public class RapidReactScoutingForm implements ScoutingForm {


    private ArrayList<String> fieldNames = new ArrayList<>();
    private ArrayList<String> teleNames = new ArrayList<>();
    private ArrayList<String> autoNames = new ArrayList<>();
    private ArrayList<String> endgameNames = new ArrayList<>();
    private ArrayList<String> clearNames = new ArrayList<>();

    public RapidReactScoutingForm() {
        fieldNames.add("teamNumber");
        fieldNames.add("matchNumber");

        fieldNames.add("Tarmac");
        fieldNames.add("Auto Outer");
        fieldNames.add("Auto Bottom");

        autoNames.add("Tarmac");
        autoNames.add("Auto Outer");
        autoNames.add("Auto Bottom");

        clearNames.add("Tarmac");
        clearNames.add("Auto Outer");
        clearNames.add("Auto Bottom");

        fieldNames.add("Teleop Outer");
        fieldNames.add("Teleop Bottom");

        teleNames.add("Teleop Outer");
        teleNames.add("Teleop Bottom");

        clearNames.add("Teleop Outer");
        clearNames.add("Teleop Bottom");

        fieldNames.add("Climb");
        fieldNames.add("Driver Performance");
        fieldNames.add("Defense Performance");

        endgameNames.add("Climb");
        endgameNames.add("Driver Performance");
        endgameNames.add("Defense Performance");

        clearNames.add("Climb");
        clearNames.add("Driver Performance");
        clearNames.add("Defense Performance");

        fieldNames.add("name");
        fieldNames.add("comments");

        fieldNames.add("comp_code");
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
