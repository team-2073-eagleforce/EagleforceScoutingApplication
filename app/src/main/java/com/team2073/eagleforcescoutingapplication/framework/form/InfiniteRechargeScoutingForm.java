package com.team2073.eagleforcescoutingapplication.framework.form;

import java.util.ArrayList;

public class InfiniteRechargeScoutingForm implements ScoutingForm {

    private ArrayList<String> fieldNames = new ArrayList<>();
    private ArrayList<String> teleNames = new ArrayList<>();
    private ArrayList<String> autoNames = new ArrayList<>();
    private ArrayList<String> endgameNames = new ArrayList<>();
    private ArrayList<String> clearNames = new ArrayList<>();

    public InfiniteRechargeScoutingForm() {
        fieldNames.add("Auto Cross");
        fieldNames.add("Auto Inner");
        fieldNames.add("Auto Outer");
        fieldNames.add("Auto Bottom");

        autoNames.add("Auto Cross");
        autoNames.add("Auto Inner");
        autoNames.add("Auto Outer");
        autoNames.add("Auto Bottom");

        clearNames.add("Auto Cross");
        clearNames.add("Auto Inner");
        clearNames.add("Auto Outer");
        clearNames.add("Auto Bottom");

        fieldNames.add("Teleop Inner");
        fieldNames.add("Teleop Outer");
        fieldNames.add("Teleop Bottom");
        fieldNames.add("Control Panel Rotation");
        fieldNames.add("Control Panel Position");

        teleNames.add("Teleop Inner");
        teleNames.add("Teleop Outer");
        teleNames.add("Teleop Bottom");
        teleNames.add("Control Panel Rotation");
        teleNames.add("Control Panel Position");

        clearNames.add("Teleop Inner");
        clearNames.add("Teleop Outer");
        clearNames.add("Teleop Bottom");
        clearNames.add("Control Panel Rotation");
        clearNames.add("Control Panel Position");

        fieldNames.add("Climb");
        fieldNames.add("Level");
        fieldNames.add("Driver Performance");
        fieldNames.add("Auto Performance");

        endgameNames.add("Climb");
        endgameNames.add("Level");
        endgameNames.add("Driver Performance");
        endgameNames.add("Auto Performance");

        clearNames.add("Climb");
        clearNames.add("Level");
        clearNames.add("Driver Performance");
        clearNames.add("Auto Performance");

        fieldNames.add("name");
        fieldNames.add("comments");
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
