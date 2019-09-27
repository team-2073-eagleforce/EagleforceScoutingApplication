package com.team2073.eagleforcescoutingapplication.framework;

public class DeepSpaceScoutingForm implements ScoutingForm {


    private int matchNumber;
    private int teamNumber;
    private int rocketCargo;
    private int rocketCargoF;
    private int rocketHatch;
    private int rocketHatchF;
    private int cargoCargo;
    private int cargoCargoF;
    private int cargoHatch;
    private int cargoHatchF;


    public DeepSpaceScoutingForm() {

    }

    @Override
    public int getTeamNumber() {
        return matchNumber;
    }

    @Override
    public int getMatchNumber() {
        return teamNumber;
    }

    public void setMatchNumber(int matchNumber) {
        this.matchNumber = matchNumber;
    }

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    public int getRocketCargo() {
        return rocketCargo;
    }

    public void setRocketCargo(int rocketCargo) {
        this.rocketCargo = rocketCargo;
    }

    public int getRocketCargoF() {
        return rocketCargoF;
    }

    public void setRocketCargoF(int rocketCargoF) {
        this.rocketCargoF = rocketCargoF;
    }

    public int getRocketHatch() {
        return rocketHatch;
    }

    public void setRocketHatch(int rocketHatch) {
        this.rocketHatch = rocketHatch;
    }

    public int getRocketHatchF() {
        return rocketHatchF;
    }

    public void setRocketHatchF(int rocketHatchF) {
        this.rocketHatchF = rocketHatchF;
    }

    public int getCargoCargo() {
        return cargoCargo;
    }

    public void setCargoCargo(int cargoCargo) {
        this.cargoCargo = cargoCargo;
    }

    public int getCargoCargoF() {
        return cargoCargoF;
    }

    public void setCargoCargoF(int cargoCargoF) {
        this.cargoCargoF = cargoCargoF;
    }

    public int getCargoHatch() {
        return cargoHatch;
    }

    public void setCargoHatch(int cargoHatch) {
        this.cargoHatch = cargoHatch;
    }

    public int getCargoHatchF() {
        return cargoHatchF;
    }

    public void setCargoHatchF(int cargoHatchF) {
        this.cargoHatchF = cargoHatchF;
    }
}
