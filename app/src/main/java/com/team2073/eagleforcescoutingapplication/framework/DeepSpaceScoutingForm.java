package com.team2073.eagleforcescoutingapplication.framework;

import java.util.ArrayList;

public class DeepSpaceScoutingForm implements ScoutingForm {

    private ArrayList<String> fieldNames = new ArrayList<>();
    private ArrayList<String> teleNames = new ArrayList<>();
    private ArrayList<String> autoNames = new ArrayList<>();
    private ArrayList<String> detailNames = new ArrayList<>();

    public DeepSpaceScoutingForm() {
        fieldNames.add("teamNumber");
        fieldNames.add("matchNumber");

        fieldNames.add("name");
        fieldNames.add("comments");

        fieldNames.add("autoRocketCargo");
        fieldNames.add("autoRocketCargoF");
        fieldNames.add("autoRocketHatch");
        fieldNames.add("autoRocketHatchF");
        fieldNames.add("autoCargoCargo");
        fieldNames.add("autoCargoCargoF");
        fieldNames.add("autoCargoHatch");
        fieldNames.add("autoCargoHatchF");

        autoNames.add("autoRocketCargo");
        autoNames.add("autoRocketCargoF");
        autoNames.add("autoRocketHatch");
        autoNames.add("autoRocketHatchF");
        autoNames.add("autoCargoCargo");
        autoNames.add("autoCargoCargoF");
        autoNames.add("autoCargoHatch");
        autoNames.add("autoCargoHatchF");

        fieldNames.add("teleRocketCargoF");
        fieldNames.add("teleRocketHatch");
        fieldNames.add("teleRocketHatchF");
        fieldNames.add("teleCargoCargo");
        fieldNames.add("teleCargoCargoF");
        fieldNames.add("teleCargoHatch");
        fieldNames.add("teleCargoHatchF");

        teleNames.add("teleRocketCargo");
        teleNames.add("teleRocketCargoF");
        teleNames.add("teleRocketHatch");
        teleNames.add("teleRocketHatchF");
        teleNames.add("teleCargoCargo");
        teleNames.add("teleCargoCargoF");
        teleNames.add("teleCargoHatch");
        teleNames.add("teleCargoHatchF");

        fieldNames.add("driverPerformance");
        fieldNames.add("autoPerformance");
        fieldNames.add("climbLevel");

        detailNames.add("driverPerformance");
        detailNames.add("autoPerformance");
        detailNames.add("climbLevel");
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
    public ArrayList<String> getDetailsFieldNames() {
        return detailNames;
    }
}
