package com.team2073.eagleforcescoutingapplication.framework;

import java.util.ArrayList;

public interface ScoutingForm {
    ArrayList<String> getFieldNames();
    ArrayList<String> getTeleFieldNames();
    ArrayList<String> getAutoFieldNames();
    ArrayList<String> getEndGameFieldNames();
    ArrayList<String> getClearNames();
}
