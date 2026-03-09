package com.team2073.eagleforcescoutingapplication.framework.form;

import java.util.ArrayList;

public class RebuiltScoutingForm implements ScoutingForm{
    private ArrayList<String> fieldNames = new ArrayList<>();
    private ArrayList<String> teleNames = new ArrayList<>();
    private ArrayList<String> autoNames = new ArrayList<>();
    private ArrayList<String> endgameNames = new ArrayList<>();
    private ArrayList<String> clearNames = new ArrayList<>();

    public RebuiltScoutingForm(){
        fieldNames.add("teamNumber");
        clearNames.add("teamNumber");


        fieldNames.add("comp_code");


        fieldNames.add("name");


        fieldNames.add("matchNumber");
        clearNames.add("matchNumber");


        fieldNames.add("startPos");
        clearNames.add("startPos");


        fieldNames.add("quantifier");


//Autos
        autoNames.add("autoLeave");
        autoNames.add("autoScore");
        autoNames.add("autoPass");
        autoNames.add("autoClimb");
        for (String name:autoNames) {
            fieldNames.add(name);
            clearNames.add(name);
        }



//Teleop
        teleNames.add("teleScore");
        teleNames.add("telePass");


        for (String name:teleNames) {
            fieldNames.add(name);
            clearNames.add(name);
        }


//Endgame
        endgameNames.add("endClimb");
        fieldNames.add("endClimb");
        clearNames.add("endClimb");

        fieldNames.add("shootingAccuracy");
        clearNames.add("shootingAccuracy");


//Submit
        fieldNames.add("driverRanking");
        clearNames.add("driverRanking");

        fieldNames.add("defenseRanking");
        clearNames.add("defenseRanking");

        fieldNames.add("comment");
        clearNames.add("comment");

        fieldNames.add("isBroken");
        clearNames.add("isBroken");

        fieldNames.add("isDisabled");
        clearNames.add("isDisabled");

        fieldNames.add("isTipped");
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
