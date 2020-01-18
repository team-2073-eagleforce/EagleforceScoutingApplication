package com.team2073.eagleforcescoutingapplication.framework.form;

import java.util.ArrayList;

public class PitScoutingForm implements ScoutingForm {

    private ArrayList<String> fieldNames = new ArrayList<>();

    public PitScoutingForm() {
        fieldNames.add("Height");
        fieldNames.add("Weight");
    }

    @Override
    public ArrayList<String> getFieldNames() {
        return fieldNames;
    }

    @Override
    public ArrayList<String> getTeleFieldNames() {
        return null;
    }

    @Override
    public ArrayList<String> getAutoFieldNames() {
        return null;
    }

    @Override
    public ArrayList<String> getEndGameFieldNames() {
        return null;
    }

    @Override
    public ArrayList<String> getClearNames() {
        return null;
    }
}
