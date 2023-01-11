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

        fieldNames.add("name");
        fieldNames.add("comment");

        fieldNames.add("compCode");

        //Autos
        fieldNames.add("mobility");
        fieldNames.add("autoBottom");
        fieldNames.add("autoMiddle");
        fieldNames.add("autoTop");
        fieldNames.add("autoChargingStation");

        //Teleop
        fieldNames.add("teleBottom");
        fieldNames.add("teleMiddle");
        fieldNames.add("teleTop");

        fieldNames.add("coneTransport");
        fieldNames.add("cubeTransport");

        //Endgame
        fieldNames.add("teleChargingStation");
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
