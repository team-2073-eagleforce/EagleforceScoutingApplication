package com.team2073.eagleforcescoutingapplication.framework;

import java.util.ArrayList;

public class DeepSpaceScoutingForm implements ScoutingForm {

    private ArrayList<String> fieldNames = new ArrayList<>();
    private ArrayList<String> teleNames = new ArrayList<>();
    private ArrayList<String> autoNames = new ArrayList<>();
    private ArrayList<String> detailNames = new ArrayList<>();

    public DeepSpaceScoutingForm() {
        fieldNames.add("Starting Position");
        fieldNames.add("Auto Rocket Cargo");
        fieldNames.add("Failed Auto Rocket Cargo");
        fieldNames.add("Auto Rocket Hatch");
        fieldNames.add("Failed Auto Rocket Hatch");
        fieldNames.add("Auto Cargoship Cargo");
        fieldNames.add("Failed Auto Cargoship Cargo");
        fieldNames.add("Auto Cargoship Hatch");
        fieldNames.add("Failed Auto Cargoship Hatch");

        autoNames.add("Starting Position");
        autoNames.add("Auto Rocket Cargo");
        autoNames.add("Failed Auto Rocket Cargo");
        autoNames.add("Auto Rocket Hatch");
        autoNames.add("Failed Auto Rocket Hatch");
        autoNames.add("Auto Cargoship Cargo");
        autoNames.add("Failed Auto Cargoship Cargo");
        autoNames.add("Auto Cargoship Hatch");
        autoNames.add("Failed Auto Cargoship Hatch");

        fieldNames.add("Teleop Rocket Cargo");
        fieldNames.add("Failed Teleop Rocket Cargo");
        fieldNames.add("Teleop Rocket Hatch");
        fieldNames.add("Failed Teleop Rocket Hatch");
        fieldNames.add("Teleop Cargoship Cargo");
        fieldNames.add("Failed Teleop Cargoship Cargo");
        fieldNames.add("Teleop Cargoship Hatch");
        fieldNames.add("Failed Teleop Cargoship Hatch");

        teleNames.add("Teleop Rocket Cargo");
        teleNames.add("Failed Teleop Rocket Cargo");
        teleNames.add("Teleop Rocket Hatch");
        teleNames.add("Failed Teleop Rocket Hatch");
        teleNames.add("Teleop Cargoship Cargo");
        teleNames.add("Failed Teleop Cargoship Cargo");
        teleNames.add("Teleop Cargoship Hatch");
        teleNames.add("Failed Teleop Cargoship Hatch");

        fieldNames.add("Driver Performance");
        fieldNames.add("Auto Performance");
        fieldNames.add("Climb Level");

        detailNames.add("Driver Performance");
        detailNames.add("Auto Performance");
        detailNames.add("Climb Level");

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
    public ArrayList<String> getDetailsFieldNames() {
        return detailNames;
    }
}
