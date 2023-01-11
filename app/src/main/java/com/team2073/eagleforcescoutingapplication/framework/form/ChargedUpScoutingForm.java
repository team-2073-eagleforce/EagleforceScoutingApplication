package com.team2073.eagleforcescoutingapplication.framework.form;

import java.util.ArrayList;

public class ChargedUpScoutingForm implements ScoutingForm {

    final private ArrayList<String> fieldNames = new ArrayList<>();
    final private ArrayList<String> teleNames = new ArrayList<>();
    final private ArrayList<String> autoNames = new ArrayList<>();
    final private ArrayList<String> endgameNames = new ArrayList<>();
    final private ArrayList<String> clearNames = new ArrayList<>();


    public ChargedUpScoutingForm() {
        //General Info
        fieldNames.add("teamNumber");
        fieldNames.add("matchNumber");
        clearNames.add("teamNumber");
        clearNames.add("matchNumber");

        fieldNames.add("name");
        fieldNames.add("comment");
        clearNames.add("name");
        clearNames.add("comment");

        fieldNames.add("compCode");
        clearNames.add("compCode");

        //Autos
        fieldNames.add("mobility");
        fieldNames.add("autoBottom");
        fieldNames.add("autoMiddle");
        fieldNames.add("autoTop");
        fieldNames.add("autoChargingStation");
        clearNames.add("mobility");
        clearNames.add("autoBottom");
        clearNames.add("autoMiddle");
        clearNames.add("autoTop");
        clearNames.add("autoChargingStation");

        //Teleop
        fieldNames.add("teleBottom");
        fieldNames.add("teleMiddle");
        fieldNames.add("teleTop");
        clearNames.add("teleBottom");
        clearNames.add("teleMiddle");
        clearNames.add("teleTop");

        fieldNames.add("coneTransport");
        fieldNames.add("cubeTransport");
        clearNames.add("coneTransport");
        clearNames.add("cubeTransport");

        //Endgame
        fieldNames.add("teleChargingStation");
        clearNames.add("teleChargingStation");
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
