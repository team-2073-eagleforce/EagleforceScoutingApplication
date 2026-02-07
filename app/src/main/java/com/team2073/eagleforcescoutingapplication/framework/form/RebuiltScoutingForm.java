package com.team2073.eagleforcescoutingapplication.framework.form;

import java.util.ArrayList;

public class RebuiltScoutingForm implements ScoutingForm{

    private ArrayList<String> fieldNames = new ArrayList<>();
    private ArrayList<String> teleNames = new ArrayList<>();
    private ArrayList<String> autoNames = new ArrayList<>();
    private ArrayList<String> endgameNames = new ArrayList<>();
    private ArrayList<String> clearNames = new ArrayList<>();

    public RebuiltScoutingForm(){
        //General Info
        fieldNames.add("teamNumber");
        clearNames.add("teamNumber");

        fieldNames.add("comp_code");

        fieldNames.add("name");

        fieldNames.add("matchNumber");
        clearNames.add("matchNumber");

        fieldNames.add("startPos");
        clearNames.add("startPos");

        fieldNames.add("quantifier");
        clearNames.add("fieldSide");

        //Autos
        autoNames.add("missed_auto");
        autoNames.add("autoLeave");
        autoNames.add("autoNet");
        autoNames.add("autoProcessor");
        autoNames.add("autoRemoved");
        autoNames.add("autoPath");
        autoNames.add("autoL1");
        autoNames.add("autoL2");
        autoNames.add("autoL3");
        autoNames.add("autoL4");
        for (String name:autoNames) {
            fieldNames.add(name);
            clearNames.add(name);
        }
        clearNames.add("sourceA");
        clearNames.add("sourceB");

        //Teleop
        teleNames.add("teleProcessor");
        teleNames.add("teleRemoved");
        teleNames.add("telenet"); //for future reference <- this is a Chris request, it's supposed to be lowercase
        teleNames.add("teleL1");
        teleNames.add("teleL2");
        teleNames.add("teleL3");
        teleNames.add("teleL4");

        for (String name:teleNames) {
            fieldNames.add(name);
            clearNames.add(name);
        }

        //Endgame
        endgameNames.add("endClimb");
        fieldNames.add("endClimb");
        clearNames.add("endClimb");

        //Submit
        fieldNames.add("driverRanking");
        fieldNames.add("defenseRanking");
        fieldNames.add("shootingAccuracy");
        fieldNames.add("comment");
        fieldNames.add("isBroken");
        fieldNames.add("isDisabled");
        fieldNames.add("isTipped");

        clearNames.add("driverRanking");
        clearNames.add("shootingAccuracy");
        clearNames.add("comment");
        clearNames.add("isBroken");
        clearNames.add("isDisabled");
        clearNames.add("isTipped");

    }
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
