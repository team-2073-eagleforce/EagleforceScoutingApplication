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
        fieldNames.add("compCode");

        fieldNames.add("name");

        fieldNames.add("teamNumber");
        fieldNames.add("matchNumber");
        clearNames.add("teamNumber");
        clearNames.add("matchNumber");

        //Autos
        fieldNames.add("autoChargingStation");

        fieldNames.add("autoGrid");
        autoNames.add("gridOneTopLeftConeAuto");
        autoNames.add("gridOneTopCubeAuto");
        autoNames.add("gridOneTopRightConeAuto");
        autoNames.add("gridTwoTopLeftConeAuto");
        autoNames.add("gridTwoTopCubeAuto");
        autoNames.add("gridTwoTopRightConeAuto");
        autoNames.add("gridThreeTopLeftConeAuto");
        autoNames.add("gridThreeTopCubeAuto");
        autoNames.add("gridThreeTopRightConeAuto");
        autoNames.add("gridOneMiddleLeftConeAuto");
        autoNames.add("gridOneMiddleCubeAuto");
        autoNames.add("gridOneMiddleRightConeAuto");
        autoNames.add("gridTwoMiddleLeftConeAuto");
        autoNames.add("gridTwoMiddleCubeAuto");
        autoNames.add("gridTwoMiddleRightConeAuto");
        autoNames.add("gridThreeMiddleLeftConeAuto");
        autoNames.add("gridThreeMiddleCubeAuto");
        autoNames.add("gridThreeMiddleRightConeAuto");
        autoNames.add("gridOneBottomLeftHybridAuto");
        autoNames.add("gridOneBottomMiddleHybridAuto");
        autoNames.add("gridOneBottomRightHybridAuto");
        autoNames.add("gridTwoBottomLeftHybridAuto");
        autoNames.add("gridTwoBottomMiddleHybridAuto");
        autoNames.add("gridTwoBottomRightHybridAuto");
        autoNames.add("gridThreeBottomLeftHybridAuto");
        autoNames.add("gridThreeBottomMiddleHybridAuto");
        autoNames.add("gridThreeBottomRightHybridAuto");

        clearNames.add("gridOneTopLeftConeAuto");
        clearNames.add("gridOneTopCubeAuto");
        clearNames.add("gridOneTopRightConeAuto");
        clearNames.add("gridTwoTopLeftConeAuto");
        clearNames.add("gridTwoTopCubeAuto");
        clearNames.add("gridTwoTopRightConeAuto");
        clearNames.add("gridThreeTopLeftConeAuto");
        clearNames.add("gridThreeTopCubeAuto");
        clearNames.add("gridThreeTopRightConeAuto");
        clearNames.add("gridOneMiddleLeftConeAuto");
        clearNames.add("gridOneMiddleCubeAuto");
        clearNames.add("gridOneMiddleRightConeAuto");
        clearNames.add("gridTwoMiddleLeftConeAuto");
        clearNames.add("gridTwoMiddleCubeAuto");
        clearNames.add("gridTwoMiddleRightConeAuto");
        clearNames.add("gridThreeMiddleLeftConeAuto");
        clearNames.add("gridThreeMiddleCubeAuto");
        clearNames.add("gridThreeMiddleRightConeAuto");
        clearNames.add("gridOneBottomLeftHybridAuto");
        clearNames.add("gridOneBottomMiddleHybridAuto");
        clearNames.add("gridOneBottomRightHybridAuto");
        clearNames.add("gridTwoBottomLeftHybridAuto");
        clearNames.add("gridTwoBottomMiddleHybridAuto");
        clearNames.add("gridTwoBottomRightHybridAuto");
        clearNames.add("gridThreeBottomLeftHybridAuto");
        clearNames.add("gridThreeBottomMiddleHybridAuto");
        clearNames.add("gridThreeBottomRightHybridAuto");
        clearNames.add("autoChargingStation");

        //Teleop
        fieldNames.add("teleGrid");
        teleNames.add("gridOneTopLeftConeTeleOp");
        teleNames.add("gridOneTopCubeTeleOp");
        teleNames.add("gridOneTopRightConeTeleOp");
        teleNames.add("gridTwoTopLeftConeTeleOp");
        teleNames.add("gridTwoTopCubeTeleOp");
        teleNames.add("gridTwoTopRightConeTeleOp");
        teleNames.add("gridThreeTopLeftConeTeleOp");
        teleNames.add("gridThreeTopCubeTeleOp");
        teleNames.add("gridThreeTopRightConeTeleOp");
        teleNames.add("gridOneMiddleLeftConeTeleOp");
        teleNames.add("gridOneMiddleCubeTeleOp");
        teleNames.add("gridOneMiddleRightConeTeleOp");
        teleNames.add("gridTwoMiddleLeftConeTeleOp");
        teleNames.add("gridTwoMiddleCubeTeleOp");
        teleNames.add("gridTwoMiddleRightConeTeleOp");
        teleNames.add("gridThreeMiddleLeftConeTeleOp");
        teleNames.add("gridThreeMiddleCubeTeleOp");
        teleNames.add("gridThreeMiddleRightConeTeleOp");
        teleNames.add("gridOneBottomLeftHybridTeleOp");
        teleNames.add("gridOneBottomMiddleHybridTeleOp");
        teleNames.add("gridOneBottomRightHybridTeleOp");
        teleNames.add("gridTwoBottomLeftHybridTeleOp");
        teleNames.add("gridTwoBottomMiddleHybridTeleOp");
        teleNames.add("gridTwoBottomRightHybridTeleOp");
        teleNames.add("gridThreeBottomLeftHybridTeleOp");
        teleNames.add("gridThreeBottomMiddleHybridTeleOp");
        teleNames.add("gridThreeBottomRightHybridTeleOp");

        clearNames.add("gridOneTopLeftConeTeleOp");
        clearNames.add("gridOneTopCubeTeleOp");
        clearNames.add("gridOneTopRightConeTeleOp");
        clearNames.add("gridTwoTopLeftConeTeleOp");
        clearNames.add("gridTwoTopCubeTeleOp");
        clearNames.add("gridTwoTopRightConeTeleOp");
        clearNames.add("gridThreeTopLeftConeTeleOp");
        clearNames.add("gridThreeTopCubeTeleOp");
        clearNames.add("gridThreeTopRightConeTeleOp");
        clearNames.add("gridOneMiddleLeftConeTeleOp");
        clearNames.add("gridOneMiddleCubeTeleOp");
        clearNames.add("gridOneMiddleRightConeTeleOp");
        clearNames.add("gridTwoMiddleLeftConeTeleOp");
        clearNames.add("gridTwoMiddleCubeTeleOp");
        clearNames.add("gridTwoMiddleRightConeTeleOp");
        clearNames.add("gridThreeMiddleLeftConeTeleOp");
        clearNames.add("gridThreeMiddleCubeTeleOp");
        clearNames.add("gridThreeMiddleRightConeTeleOp");
        clearNames.add("gridOneBottomLeftHybridTeleOp");
        clearNames.add("gridOneBottomMiddleHybridTeleOp");
        clearNames.add("gridOneBottomRightHybridTeleOp");
        clearNames.add("gridTwoBottomLeftHybridTeleOp");
        clearNames.add("gridTwoBottomMiddleHybridTeleOp");
        clearNames.add("gridTwoBottomRightHybridTeleOp");
        clearNames.add("gridThreeBottomLeftHybridTeleOp");
        clearNames.add("gridThreeBottomMiddleHybridTeleOp");
        clearNames.add("gridThreeBottomRightHybridTeleOp");

        fieldNames.add("coneTransport");
        fieldNames.add("cubeTransport");
        clearNames.add("coneTransport");
        clearNames.add("cubeTransport");

        //Endgame
        fieldNames.add("endChargingStation");
        clearNames.add("endChargingStation");

        //Submit
        fieldNames.add("driverRanking");
        fieldNames.add("defenseRanking");
        fieldNames.add("comment");
        clearNames.add("driverRanking");
        clearNames.add("defenseRanking");
        clearNames.add("comment");
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
