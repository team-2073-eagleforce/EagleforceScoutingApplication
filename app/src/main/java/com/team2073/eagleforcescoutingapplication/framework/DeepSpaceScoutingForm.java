package com.team2073.eagleforcescoutingapplication.framework;

import java.util.ArrayList;

public class DeepSpaceScoutingForm implements ScoutingForm {

    private ArrayList<String> fieldNames = new ArrayList<>();

    public DeepSpaceScoutingForm() {
        fieldNames.add("matchNumber");
        fieldNames.add("teamNumber");
        fieldNames.add("rocketCargo");
        fieldNames.add("rocketCargoF");
        fieldNames.add("rocketHatch");
        fieldNames.add("rocketHatchF");
        fieldNames.add("matchNumber");
        fieldNames.add("matchNumber");
        fieldNames.add("cargoCargo");
        fieldNames.add("cargoCargoF");
        fieldNames.add("cargoHatch");
        fieldNames.add("cargoHatchF");
        fieldNames.add("name");
        fieldNames.add("comments");
    }

    @Override
    public ArrayList<String> getFieldNames() {
        return fieldNames;
    }
}
